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

(Aquí van las pruebas, con su captura.)

## 8. Roles

- Team Leader: Pablo Lara
- Programador experto: Víctor Tierno
- Responsable de pruebas (QA): Pablo Hernández
- Responsable de documentación: Alfonso Bercedo (ausente, lo hace Pablo Lara)