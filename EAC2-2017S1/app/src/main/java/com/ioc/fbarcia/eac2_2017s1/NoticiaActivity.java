package com.ioc.fbarcia.eac2_2017s1;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Created by fbarcia on 23/10/2017.
 */

public class NoticiaActivity extends AppCompatActivity {

    public static final String ITEM_PASSAT = NoticiaActivity.class.getCanonicalName() + ".ITEM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.noticia_completa);

        WebView webView = (WebView) findViewById(R.id.webView);

        //Rebem l'item de l'altre activity
        Bundle extras = getIntent().getExtras();
        Noticia item = (Noticia)extras.getSerializable(ITEM_PASSAT);

        if (item != null) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(item.getTitol());
            }

            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            // Si conexió a internet, descarreguem l'XML, si no, missatge d'error
            if (networkInfo != null) {
                webView.setWebViewClient(new WebViewClient());
                webView.loadUrl(item.getEnllac());
            } else {
                String html = noticiaHTML(item);
                webView.loadData(html, "text/html; charset=UTF-8", null);
            }
        }
    }

    private String noticiaHTML(Noticia noticia) {
        StringBuilder htmlString = new StringBuilder();

        htmlString.append("<h3>"+noticia.getTitol()+"</h3><hr>");
        htmlString.append("<p>"+noticia.getResum()+"</p><hr>");
        htmlString.append("<p align=\"right\">"+noticia.getAutor()+"</p><hr>");
        htmlString.append("<p><b>Categories: </b>"+ noticia.getCategories()+"</p>");
        htmlString.append("<p>"+noticia.getDataPublicacio()+"</p>");

        return htmlString.toString();

        /*
        return String.format("<h3>"+noticia.getTitol()+"</h3>" +
                        "<hr>" +
                        "<p>"+noticia.getResum()+"</p>" +
                        "<hr>" +
                        "<p style='text-align: end'><em>"+noticia.getAutor()+"</em></p>" +
                        "<hr>" +
                        "<p>"+noticia.getDataPublicacio()+"</p>"
        );
        */
    }
}