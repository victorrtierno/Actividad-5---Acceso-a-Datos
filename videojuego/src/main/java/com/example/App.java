package com.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;

public class App {

    private static List<Videojuego> catalogo = new ArrayList<>();
    
    public static void main( String[] args ) {

        Scanner scanner = new Scanner(System.in);
        int opcion = -1;

        while (opcion != 0) {
            mostrarMenu();
            System.out.print("Seleccione una opción: ");
            opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar el buffer

            switch (opcion) {
                case 1:
                    CargarcatalogoDesdeCSV();
                    break;
                case 2:
                    mostrarCatalogo();
                    break;
                case 3:
                    exportarCatalogoAXML();
                    break;
                case 4:
                    cargarCatalogoDesdeXML();
                    break;
                case 5:
                    exportarCatalogoACSV();
                    break;
                case 6:
                    buscarVideojuego();
                    break;
                case 7:
                    informacionDeFicheros();
                    break;
                case 0:
                    System.out.println("Saliendo del programa...");
                    break;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
            }
        }

    }
    public static void CargarcatalogoDesdeCSV() {
        // Lógica para cargar el catálogo desde un archivo CSV
        String ruta = "videojuegos.csv"; 
        File fichero = new File(ruta);

        if (!fichero.exists()) {
            System.err.println("Error: El fichero CSV no se encuentra en la ruta: " + fichero.getAbsolutePath());
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(fichero))) {
            String linea;
            boolean cabecera = true;
            int registrosProcesados = 0;
            
            catalogo.clear(); // Limpiamos la colección por si se carga más de una vez en la misma sesión

            while ((linea = br.readLine()) != null) {
                if (cabecera) {
                    cabecera = false;
                    continue; // Saltamos la primera línea de encabezados
                }

                String[] datos = linea.split(","); // Tenemos en cuenta separación por comas

                if (datos.length == 7) {
                    try {
                        Videojuego juego = new Videojuego(
                            Integer.parseInt(datos[0].trim()),  // ID
                            datos[1].trim(),                    // Título
                            datos[2].trim(),                    // Plataforma
                            datos[3].trim(),                    // Género
                            Double.parseDouble(datos[4].trim()),// Precio
                            Integer.parseInt(datos[5].trim()),  // Stock
                            datos[6].trim()                     // Código Proveedor
                        );
                        
                        catalogo.add(juego);
                        registrosProcesados++;
                        
                    } catch (NumberFormatException e) {
                        System.err.println("Error de conversión numérica en el registro: " + linea);
                    }
                } else {
                    System.err.println("Error de formato (columnas incorrectas) en: " + linea);
                }
            }
            System.out.println("Carga completada. Se han añadido " + registrosProcesados + " videojuegos al catálogo.");

        } catch (IOException e) {
            System.err.println("Error crítico al leer el archivo CSV.");
        }
    }

    public static void mostrarCatalogo() {
        if (catalogo.isEmpty()) {
            System.err.println("Error: El catálogo está vacío. Cargue primero los datos desde CSV o XML.");
            return;
        }
        System.out.println("\n--- LISTADO DE VIDEOJUEGOS ---");
        for (Videojuego v : catalogo) {
            System.out.println(v.toString()); 
        }
        System.out.println("Total: " + catalogo.size() + " videojuegos.\n");
    }

    public static void exportarCatalogoAXML() {
        if (catalogo.isEmpty()) {
            System.err.println("Error: No hay datos en memoria para exportar a XML.");
            return;
        }
        try {
            Catalogo envoltorio = new Catalogo();
            envoltorio.setVideojuegos(catalogo);
            
            JAXBContext context = JAXBContext.newInstance(Catalogo.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            
            File ficheroXML = new File("catalogo.xml");
            marshaller.marshal(envoltorio, ficheroXML);
            
            System.out.println("Éxito: El catálogo ha sido exportado correctamente a XML en " + ficheroXML.getAbsolutePath());
        } catch (JAXBException e) {
            System.err.println("Error durante el procesamiento XML al exportar: " + e.getMessage());
        }
    }
    
    public static void cargarCatalogoDesdeXML() {
        File ficheroXML = new File("catalogo.xml");
        
        if (!ficheroXML.exists()) {
            System.err.println("Error: El fichero XML no existe. Debe exportarlo primero.");
            return;
        }
        try {
            JAXBContext context = JAXBContext.newInstance(Catalogo.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            
            Catalogo envoltorio = (Catalogo) unmarshaller.unmarshal(ficheroXML);
            catalogo = envoltorio.getVideojuegos();
            
            System.out.println("Éxito: Se han cargado " + catalogo.size() + " videojuegos desde el archivo XML.");
        } catch (JAXBException e) {
            System.err.println("Error durante el procesamiento XML al importar: " + e.getMessage());
        }
    }

    public static void exportarCatalogoACSV() {
        if (catalogo.isEmpty()) {
            System.err.println("Error: No hay datos para exportar a CSV.");
            return;
        }
        
        File archivoCSV = new File("videojuegos_exportado.csv");
        
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoCSV))) {
            // Escribir cabecera
            bw.write("id,titulo,plataforma,genero,precio,stock,codigoProveedor");
            bw.newLine();
            
            for (Videojuego v : catalogo) {
                // Si el objeto proviene del XML, codigoProveedor no aparecera por el @XmlTransient
                String codigo = (v.getCodigoProveedor() != null) ? v.getCodigoProveedor() : "N/A";
                
                String linea = v.getId() + "," + v.getTitulo() + "," + v.getPlataforma() + "," + 
                               v.getGenero() + "," + v.getPrecio() + "," + v.getStock() + "," + codigo;
                bw.write(linea);
                bw.newLine();
            }
            System.out.println("Éxito: Se ha generado un nuevo fichero CSV en " + archivoCSV.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error de lectura/escritura al intentar generar el CSV.");
        }
    }

    public static void buscarVideojuego() {
        if (catalogo.isEmpty()) {
            System.err.println("Error: El catálogo está vacío. Realice una carga previa.");
            return;
        }
        
        // Se asume que usas el mismo scanner global, si no, puedes instanciar uno local
        Scanner sc = new Scanner(System.in);
        System.out.print("Introduzca el ID o Título del videojuego a buscar: ");
        String busqueda = sc.nextLine().toLowerCase();
        
        boolean encontrado = false;
        for (Videojuego v : catalogo) {
            if (String.valueOf(v.getId()).equals(busqueda) || v.getTitulo().toLowerCase().contains(busqueda)) {
                System.out.println("Coincidencia encontrada: " + v.toString());
                encontrado = true;
            }
        }
        
        if (!encontrado) {
            System.out.println("Aviso: No se ha encontrado ningún videojuego con ese criterio.");
        }
    }

    public static void informacionDeFicheros() {
        String[] rutas = {"videojuegos.csv", "catalogo.xml", "videojuegos_exportado.csv"};
        System.out.println("\n--- INFORMACIÓN DE FICHEROS ---");
        
        for (String ruta : rutas) {
            File f = new File(ruta);
            System.out.println("Fichero: " + ruta);
            if (f.exists()) {
                System.out.println("  - Existencia: Sí");
                System.out.println("  - Tamaño: " + f.length() + " bytes");
                System.out.println("  - Ruta absoluta: " + f.getAbsolutePath());
            } else {
                System.out.println("  - Existencia: No");
            }
            System.out.println("-------------------------------");
        }
    }

    // MENÚ 
    private static void mostrarMenu() {
        System.out.println("========================================");
        System.out.println("| PROMEHUB DATA EXCHANGE                |");
        System.out.println("========================================");
        System.out.println("| 1. Cargar catálogo desde CSV         |");
        System.out.println("| 2. Mostrar catálogo                  |");
        System.out.println("| 3. Exportar catálogo a XML           |");
        System.out.println("| 4. Cargar catálogo desde XML         |");
        System.out.println("| 5. Exportar catálogo a CSV           |");
        System.out.println("| 6. Buscar videojuego                 |");
        System.out.println("| 7. Información de ficheros           |");
        System.out.println("| 0. Salir                             |");
        System.out.println("========================================");
    }
    
}



