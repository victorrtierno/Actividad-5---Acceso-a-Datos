package com.example;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import java.util.List;

@XmlRootElement(name = "catalogo")
@XmlAccessorType(XmlAccessType.FIELD)
public class Catalogo {

    @XmlElement(name = "videojuego")
    private List<Videojuego> videojuegos;

    // JAXB necesita un constructor vacío
    public Catalogo() {}

    public List<Videojuego> getVideojuegos() {
        return videojuegos;
    }

    public void setVideojuegos(List<Videojuego> videojuegos) {
        this.videojuegos = videojuegos;
    }
}
