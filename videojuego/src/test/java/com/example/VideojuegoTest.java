package com.example;

import jakarta.xml.bind.annotation.XmlAccessOrder;
import jakarta.xml.bind.annotation.XmlAccessorOrder;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name = "videojuego") //elemento raíz en el XML
@XmlAccessorType(XmlAccessType.FIELD) //JAXB trabaja directamente con los atributos de la clase <-> elementos xml
/* Opciones ppales para AccessType
XmlAccessType.FIELD -> Trabaja con los campos
XmlAccessType.PROPERTY -> Trabaja principalmente con las propiedades JavaBean, es decir, getters/setter
XmlAccessType.PUBLIC_MEMBER -> Utiliza miembros públicos según las reglas de JAXB
XmlAccessType.NONE -> No mapea automáticamente los miembros. Tendremos que indicar explícitamente qué queremos mapear mediante anotaciones
 */

//@XmlAccessorOrder(XmlAccessOrder.UNDEFINED) //no se garantiza ningún orden para los elementos
//@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL) controla el orden de campos y propiedades de una clase -> orden alfabético
@XmlType(propOrder = {  //indica el orden específico de los elementos
    "id",
    "titulo",
    "plataforma",
    "genero",
    "precio",
    "stock",
    "codigoProveedor"
})
public class VideojuegoTest {

    //@XmlElement(name = "identificador"), si quisieramos cambiar el nombre del nodo en el xml
    private int id;

    @XmlElement(required = true)  //titulo es requerido
    private String titulo;

    private String plataforma;

    private double precio;

    private int stock;

    //@XmlTransient --> señala que el elemento no se incluirá en el xml
    @XmlTransient
    private int codigoProveedor;

    @XmlAttribute //señala categoría como atributo de Juego
    private String genero; 


    public VideojuegoTest() {
        //el constructor vacío es necesario para el unmarshalling
    }

    public VideojuegoTest (int id, String titulo, String plataforma, String genero, double precio, int stock, int codigoProveedor) {
        this.id = id;
        this.titulo = titulo;
        this.plataforma = plataforma;
        this.genero = genero;
        this.precio = precio;
        this.stock = stock;
        this.codigoProveedor = codigoProveedor;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getCodigoProveedor() {
        return codigoProveedor;
    }

    public void setCodigoProveedor(int codigoProveedor) {
        this.codigoProveedor = codigoProveedor;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    @Override
    public String toString() {
        return " JUEGO [ID: " + id + "]\n"
         + " ├─ Título:     " + titulo + "\n"
         + " ├─ Plataforma: " + plataforma + "\n"
         + " ├─ Género:     " + genero + "\n"
         + " ├─ Precio:     " + precio + " €\n"
         + " ├─ Stock:      " + stock + " uds.\n"
         + " └─ Código del Proveedor:  " + codigoProveedor;
    }
   
}
