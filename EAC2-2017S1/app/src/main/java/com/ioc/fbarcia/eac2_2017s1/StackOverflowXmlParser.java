package com.ioc.fbarcia.eac2_2017s1;

/**
 * Created by fbarcia on 18/10/2017.
 */

import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class StackOverflowXmlParser {
    // No fem servir namespaces
    private static final String ns = null;

    //Aquesta classe representa una entrada de noticia del RSS Feed
    public List<Noticia> analitza(InputStream in) throws XmlPullParserException, IOException {
        try {
            //Obtenim analitzador
            XmlPullParser parser = Xml.newPullParser();

            //No fem servir namespaces
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

            //Especifica l'entrada de l'analitzador
            parser.setInput(in, null);

            //Obtenim la primera etiqueta
            parser.nextTag();

            //Retornem la llista de noticies
            return llegirNoticies(parser);
        } finally {
            in.close();
        }
    }

    //Llegeix una llista de noticies d'eldiario.com a partir del parser i retorna una llista d'Entrades
    private List<Noticia> llegirNoticies(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Noticia> llistaEntrades = new ArrayList<Noticia>();

        //Comprova si l'event actual és del tipus esperat (START_TAG) i del nom "rss"
        parser.require(XmlPullParser.START_TAG, ns, "rss");
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, ns, "channel");

        //Mentre que no arribem al final d'etiqueta
        while (parser.next() != XmlPullParser.END_TAG) {
            //Ignorem tots els events que no siguin un començament d'etiqueta
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                //Saltem al seguent event
                continue;
            }

            //Obtenim el nom de l'etiqueta
            String name = parser.getName();
            // Si aquesta etiqueta és una entrada de noticia
            if (name.equals("item")) {
                //Afegim l'entrada a la llista
                llistaEntrades.add(llegirEntrada(parser));
            } else {
                //Si és una altra cosa la saltem
                saltar(parser);
            }
        }
        return llistaEntrades;
    }

    //Analitza el contingut d'una entrada. Si troba un títol, resum o enllaç, crida els mètodes de lectura
    //propis per processar-los. Si no, ignora l'etiqueta.
    private Noticia llegirEntrada(XmlPullParser parser) throws XmlPullParserException, IOException {
        String titol = null;
        String resum = null;
        String enllac = null;
        String dataPublicacio = null;
        String autor = null;
        String categories = null;
        String thumbnail = null;

        //L'etiqueta actual ha de ser "entry"
        parser.require(XmlPullParser.START_TAG, ns, "item");
        //Mentre que no acabe l'etiqueta de "item"
        while (parser.next() != XmlPullParser.END_TAG) {
            //Ignora fins que no trobem un començament d'etiqueta
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            //Obtenim el nom de l'etiqueta
            String etiqueta = parser.getName();

            switch (etiqueta) {
                case "title":
                    titol = llegeixText(parser, "title");
                    break;
                case "description":
                    resum = llegirResum(parser);
                    break;
                case "link":
                    enllac = llegeixText(parser, "link");
                    break;
                case "pubDate":
                    dataPublicacio = llegeixText(parser, "pubDate");
                    break;
                case "author":
                    autor = llegeixText(parser, "author");
                    break;
                case "media:keywords":
                    categories = llegeixText(parser, "media:keywords");
                    break;
                case "media:thumbnail":
                    thumbnail = llegirThumbnail(parser);
                    break;
                default:
                    //les altres etiquetes les saltem
                    saltar(parser);
                    break;
            }
        }

        //Creem una nova entrada amb aquestes dades i la retornem
        return new Noticia(titol, resum, enllac, autor, categories, thumbnail, dataPublicacio);
    }

    //Llegeix el resum de una notícia del rss i el retorna com String
    private String llegirResum(XmlPullParser parser) throws IOException, XmlPullParserException {
        //L'etiqueta actual ha de ser "summary"
        parser.require(XmlPullParser.START_TAG, ns, "description");

        String resumComplet = llegeixText(parser, "description");
        String[] resumPartit = resumComplet.split("</p>");
        String[] resumFinal = resumPartit[1].split("<p>");
        String resumDefinitu = resumFinal[1];

        parser.require(XmlPullParser.END_TAG, ns, "description");
        return resumDefinitu;
    }

    //Llegeix el resum de una notícia del rss i el retorna com String
    private String llegirThumbnail(XmlPullParser parser) throws IOException, XmlPullParserException {
        //L'etiqueta actual ha de ser "summary"
        parser.require(XmlPullParser.START_TAG, ns, "media:thumbnail");

        //String thumbnail = llegeixText(parser, "media:thumbnail");
        String thumbnail = parser.getAttributeValue(0);
        parser.nextTag();

        parser.require(XmlPullParser.END_TAG, ns, "media:thumbnail");
        return thumbnail;
    }


    //Extrau el valor de text per les etiquetes titol, resum
    private String llegeixText(XmlPullParser parser, String requiredTag) throws IOException, XmlPullParserException {
        String resultat = "";
        parser.require(XmlPullParser.START_TAG, ns, requiredTag);

        if (parser.next() == XmlPullParser.TEXT) {
            resultat = parser.getText();
            parser.nextTag();
        }

        parser.require(XmlPullParser.END_TAG, ns, requiredTag);

        return resultat;
    }

    //Aquesta funció serveix per saltar-se una etiqueta i les seves subetiquetes aniuades.
    private void saltar(XmlPullParser parser) throws XmlPullParserException, IOException {
        //Si no és un començament d'etiqueta: ERROR
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;

        //Comprova que ha passat per tantes etiquetes de començament com acabament d'etiqueta

        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    //Cada vegada que es tanca una etiqueta resta 1
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    //Cada vegada que s'obre una etiqueta augmenta 1
                    depth++;
                    break;
            }
        }
    }

}