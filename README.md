# PromeHub Data Exchange

Práctica de Acceso a Datos (DAM 2). Hecha por Víctor Tierno, Pablo Hernández, Alfonso Bercedo y Pablo Lara.

## Índice

1. Qué hace la app
2. Cómo ejecutarla
3. El menú
4. Cómo funciona
5. Anotaciones JAXB
6. Errores que controla
7. Pruebas
8. Lo que falta
9. Roles

## 1. Qué hace la app

Es una app de consola en Java que pasa el catálogo de videojuegos de CSV a XML y de XML a CSV.

- PromeHub Manager nos da el catálogo en CSV.
- PromeHub Store lo necesita en XML.

El código del proveedor no tiene que salir en el XML.

## 2. Cómo ejecutarla

1. Hace falta JDK 17 y Maven.
2. Compilar con `mvn clean compile`.
3. Ejecutar la clase `com.example.App` desde el IDE.
4. La app busca el CSV en `videojuego/videojuegos.csv`, así que hay que ejecutarla desde la carpeta que tiene dentro la carpeta `videojuego`.

## 3. El menú

1. Cargar catálogo desde CSV
2. Mostrar catálogo
3. Exportar catálogo a XML
4. Cargar catálogo desde XML
5. Exportar catálogo a CSV
6. Buscar videojuego (por id o título)
7. Información de ficheros
0. Salir

## 4. Cómo funciona

- **CSV a Java:** leemos el CSV línea a línea con `BufferedReader`, saltamos la cabecera y creamos un `Videojuego` por cada línea buena. Se guardan en una lista.
- **Java a XML:** metemos la lista en `Catalogo` y con JAXB hacemos `catalogo.xml`.
- **XML a Java:** con JAXB leemos `catalogo.xml` y recuperamos la lista.
- **Java a CSV:** escribimos `videojuegos_exportado.csv`. El código del proveedor sale como `N/A` porque no viaja en el XML.

El XML queda así:

```xml
<catalogo>
    <videojuego id="1">
        <titulo>Cyberpunk 2077</titulo>
        <plataforma>PC</plataforma>
        <genero>RPG</genero>
        <precio>39.99</precio>
        <stock>12</stock>
    </videojuego>
</catalogo>
```

## 5. Anotaciones JAXB

- `@XmlRootElement`: marca la raíz del XML.
- `@XmlAccessorType(FIELD)`: JAXB usa los campos de la clase.
- `@XmlAttribute`: el `id` sale como atributo.
- `@XmlElement`: el resto sale como elemento.
- `@XmlTransient`: el código del proveedor no sale en el XML.
- `@XmlType(propOrder)`: pone los elementos en orden.

## 6. Errores que controla

- El CSV o el XML no existe.
- Error al leer el fichero.
- Línea del CSV con columnas de más o de menos.
- Número mal escrito.
- XML roto.
- Catálogo vacío.
- Opción del menú que no existe.

Los mensajes salen en español y dicen qué ha pasado.

## 7. Pruebas


Para verificar el correcto funcionamiento de la aplicación, el equipo de pruebas ejecutó una batería de 14 casos que cubren tanto el camino feliz como situaciones de error y reglas de seguridad.


### CP-01: Carga secuencial del fichero oficial (`videojuegos.csv`)

* Objetivo: Leer el archivo secuencialmente, ignorar la cabecera y guardar los 5 videojuegos en memoria.
* Problema detectado: Salía un error `FileNotFoundException` porque el terminal ejecutaba desde la carpeta raíz `Actividad5` y buscaba el archivo fuera de la subcarpeta `videojuego`.
* Solución aplicada: Se corrigió la ruta en el código a `"videojuego/videojuegos.csv"` y se añadió comprobación previa con `fichero.exists()`.
* Resultado: Éxito (PASS). Carga los 5 registros e informa por consola.

---

### CP-02: Tolerancia a fallos con CSV corrupto (`videojuegos_errores.csv`)

* Objetivo: Comprobar que la aplicación no colapsa si un registro contiene texto en el precio o un ID inválido.
* Problema detectado: `Double.parseDouble()` lanzaba una excepción `NumberFormatException` no controlada que detenía la lectura completa del archivo.
* Solución aplicada: Se introdujo un bloque `try-catch` dentro del bucle de lectura. Si una fila está corrupta, se descarta, se muestra aviso por consola con el número de fila y se continúa con las siguientes.
* Resultado: Éxito (PASS). Carga únicamente las filas válidas sin abortar.

---

### CP-03: Control de fichero inexistente

* Objetivo: Probar el comportamiento de la opción 1 si se indica una ruta que no existe.
* Resultado inicial: Funcionó correctamente desde el inicio. La condición `!fichero.exists()` captura el fallo y muestra un mensaje pedagógico en español.
* Resultado: Éxito (PASS).

---

### CP-04: Mostrar catálogo con datos cargados

* Objetivo: Listar en la consola los videojuegos para verificar que los objetos en memoria son correctos.
* Resultado inicial: Funcionó correctamente. El método `toString()` muestra la información completa y formateada (ID, título, plataforma, género, precio, stock y proveedor).
* Resultado: Éxito (PASS).

---

### CP-05: Mostrar catálogo sin haber cargado datos

* Objetivo: Probar la opción 2 con el catálogo en memoria vacío nada más iniciar la aplicación.
* Resultado inicial: Funcionó correctamente. Comprueba `catalogo.isEmpty()` y muestra una advertencia solicitando cargar datos primero.
* Resultado: Éxito (PASS).

---

### CP-06: Exportación del catálogo a XML (`catalogo.xml`)

* Objetivo: Convertir la lista de objetos en memoria a un documento XML usando JAXB, asegurando que `id` sea un atributo.
* Problema detectado: JAXB lanzaba una excepción al hacer el marshalling. Ocurría porque el campo `id` estaba incluido en la lista `propOrder` de `@XmlType`. En JAXB, los campos `@XmlAttribute` no pueden tener orden posicional.
* Solución aplicada: Se retiró `"id"` del array `propOrder` en `Videojuego.java`, dejando en el orden únicamente los elementos hijos.
* Resultado: Éxito (PASS). Genera el XML con `<videojuego id="...">` de forma limpia.

---

### CP-07: Comprobación de confidencialidad en XML

* Objetivo: Garantizar que el código interno de proveedor no aparece en el XML destinado a la tienda.
* Problema detectado: En las primeras versiones aparecía la etiqueta `<codigoProveedor>PROV-001</codigoProveedor>` en el XML generado.
* Solución aplicada: Se añadió la anotación `@XmlTransient` sobre `codigoProveedor` en `Videojuego.java` y se retiró de `propOrder`.
* Resultado: Éxito (PASS). El término "PROV" no aparece en ninguna parte de `catalogo.xml`.

---

### CP-08: Importar catálogo desde XML

* Objetivo: Reconstruir la colección de videojuegos en memoria leyendo el fichero `catalogo.xml` mediante el `Unmarshaller`.
* Resultado inicial: Funcionó correctamente. Carga los 5 juegos y deja el campo `codigoProveedor` en `null` de forma controlada.
* Resultado: Éxito (PASS).

---

### CP-09: Exportar de XML a CSV (Ciclo inverso)

* Objetivo: Generar un nuevo archivo CSV (`videojuegos_exportado.csv`) tras haber cargado los datos desde el XML.
* Problema detectado: Se producía un `NullPointerException` al escribir la fila porque el código de proveedor venía como `null` desde el XML.
* Solución aplicada: Se implementó un operador ternario al escribir la línea: si el proveedor es nulo, escribe `"N/A"` en la columna sin fallar.
* Resultado: Éxito (PASS). Genera el CSV completo sin errores.

---

### CP-10: Búsqueda exacta por ID numérico

* Objetivo: Buscar un videojuego por su identificador introduciendo el valor `"2"`.
* Problema detectado: Al buscar `"2"`, la consola devolvía dos juegos: el 2 (EA Sports FC 26) y el 1 (Cyberpunk 2077), porque el título de Cyberpunk contiene el carácter '2' (2077) y la condición usaba `.contains()`.
* Solución aplicada: Se aplicó una expresión regular (`busqueda.matches("\\d+")`). Si el usuario introduce solo números, busca únicamente coincidencia exacta por ID. Si contiene texto, busca por título.
* Resultado: Éxito (PASS). Al buscar `"2"` devuelve únicamente EA Sports FC 26.

---

### CP-11: Búsqueda parcial por título

* Objetivo: Buscar un juego introduciendo texto parcial (por ejemplo `"mine"` o `"GTA"`).
* Resultado inicial: Funcionó correctamente. Pasa el texto a minúsculas y usa `.contains()`, ignorando mayúsculas y minúsculas.
* Resultado: Éxito (PASS).

---

### CP-12: Consulta de metadatos de ficheros

* Objetivo: Probar la opción 7 para consultar la información de los ficheros del sistema.
* Resultado inicial: Funcionó correctamente. Mediante `java.io.File` informa de la existencia, tamaño en bytes y ruta absoluta en disco.
* Resultado: Éxito (PASS).

---

### CP-13: Control de entradas erróneas en el menú

* Objetivo: Introducir letras (ej: `"abc"`) en el selector numérico del menú principal.
* Problema detectado: `scanner.nextInt()` lanzaba un `InputMismatchException` no controlado que cerraba la aplicación bruscamente.
* Solución aplicada: Se protegió la lectura con un bloque `try-catch (InputMismatchException)` y `scanner.nextLine()` para vaciar el buffer, manteniendo el menú activo.
* Resultado: Éxito (PASS).

---

### CP-14: Salida limpia de la aplicación

* Objetivo: Seleccionar la opción `0` para cerrar el programa.
* Resultado inicial: Funcionó correctamente. Muestra el mensaje de despedida y finaliza la máquina virtual con código 0.
* Resultado: Éxito (PASS).

---

> **Conclusión:**  
 Se ejecutaron 14 pruebas que cubren todas las opciones del menú, el tratamiento de excepciones y las reglas de negocio. Se corrigieron 5 incidencias reales detectadas durante el desarrollo (resolución de rutas, conflicto de ordenación en JAXB, regla de confidencialidad, colisiones en la búsqueda por ID y control de valores nulos), logrando que las pruebas fueran superadas con éxito.

## 8. Roles

- Team Leader: Pablo Lara
- Programador experto: Víctor Tierno
- Responsable de pruebas (QA): Pablo Hernández
- Responsable de documentación: Alfonso Bercedo (ausente, lo hace Pablo Lara)