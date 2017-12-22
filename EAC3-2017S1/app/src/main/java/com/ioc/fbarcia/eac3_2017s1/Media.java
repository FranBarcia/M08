package com.ioc.fbarcia.eac3_2017s1;

import java.io.Serializable;

/**
 * Created by fbarcia on 07/11/2017.
 */

public class Media implements Serializable {

    public Integer id;
    public String nom;
    public String ubicacio;
    public Integer tipusMedia;
    public Double latitud;
    public Double longitud;

    //Constructors
    public Media(Integer id, String nom, String ubicacio, Integer tipusMedia, Double latitud, Double longitud) {
        this.id = id;
        this.nom = nom;
        this.ubicacio = ubicacio;
        this.tipusMedia = tipusMedia;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Media(String nom, String ubicacio, Integer tipusMedia, Double latitud, Double longitud) {
        this.nom = nom;
        this.ubicacio = ubicacio;
        this.tipusMedia = tipusMedia;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Media() {}

    //Getters
    public Integer getId() {
        return this.id;
    }
    public String getNom() {
        return this.nom;
    }
    public String getUbicacio() {
        return this.ubicacio;
    }
    public Integer getTipusMedia() {
        return this.tipusMedia;
    }
    public Double getLatitud() {
        return this.latitud;
    }
    public Double getLongitud() {
        return this.longitud;
    }

    // Setters
    public void setId(Integer id) {
        this.id = id;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public void setUbicacio(String ubicacio) {
        this.ubicacio = ubicacio;
    }
    public void setTipusMedia(Integer tipusMedia) {
        this.tipusMedia = tipusMedia;
    }
    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }
    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

}
