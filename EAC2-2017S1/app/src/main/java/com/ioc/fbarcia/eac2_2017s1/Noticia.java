package com.ioc.fbarcia.eac2_2017s1;

import java.io.Serializable;

/**
 * Created by fbarcia on 22/10/2017.
 */

public class Noticia implements Serializable {

    public Integer id;                //Títol
    public String titol;              //Títol
    public String enllac;             //Enllaç
    public String resum;              //Resum
    public String autor;              //Autor
    public String categories;         //Categories
    public String thumbnail;          //Thumbnail
    public String dataPublicacio;     //Data de publicació
    public String thumbnailPath;      //Thumbnail

    public Noticia(String title, String summary, String link, String author, String cathegories, String thumbnail, String pubDate) {
        this.titol = title;
        this.resum = summary;
        this.enllac = link;
        this.autor = author;
        this.categories = cathegories;
        this.thumbnail = thumbnail;
        this.dataPublicacio = pubDate;
    }

    public Noticia(String title, String summary, String link, String author, String cathegories, String pubDate) {
        this.titol = title;
        this.resum = summary;
        this.enllac = link;
        this.autor = author;
        this.categories = cathegories;
        this.dataPublicacio = pubDate;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitol() {
        return this.titol;
    }

    public void setTitol(String titol) {
        this.titol = titol;
    }

    public String getResum() {
        return this.resum;
    }

    public void setResum(String resum) {
        this.resum = resum;
    }

    public String getEnllac() {
        return this.enllac;
    }

    public void setEnllac(String enllac) {
        this.enllac = enllac;
    }

    public String getAutor() {
        return this.autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getCategories() {
        return this.categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getThumbnail() {
        return this.thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDataPublicacio() {
        return this.dataPublicacio;
    }

    public void setDataPublicacio(String dataPublicacio) {
        this.dataPublicacio = dataPublicacio;
    }

    public String getThumbnailPath() {
        return this.thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

}
