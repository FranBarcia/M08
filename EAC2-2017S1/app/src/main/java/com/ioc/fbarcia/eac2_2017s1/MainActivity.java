package com.ioc.fbarcia.eac2_2017s1;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import org.xmlpull.v1.XmlPullParserException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String URL = "http://www.eldiario.es/rss/catalunya";
    private ArrayList<Noticia> llistaEntrades = new ArrayList<Noticia>();
    DBTools bd;
    ImageButton search, refresh, searchHideButton;
    ProgressBar progressBar;
    RecyclerView rView;
    LinearLayout searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bd = new DBTools(this);

        search = (ImageButton) findViewById(R.id.searchButton);
        refresh = (ImageButton) findViewById(R.id.refreshButton);
        progressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        rView = (RecyclerView) findViewById(R.id.recycler_view);
        searchBar = (LinearLayout) findViewById(R.id.searchBar);
        searchHideButton = (ImageButton) findViewById(R.id.searchHideButton);

        refresh.setOnClickListener(this);
        search.setOnClickListener(this);
        searchHideButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void actualitzaEstatXarxa() {
        //Obtenim un gestor de les connexions de xarxa per obtenir l'estat de la xarxa
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // Si conexió a internet, descarreguem l'XML, si no, missatge d'error
        if (networkInfo != null) {
            carregaNoticies();
        } else {
            carregaNoticiesModeOffline();
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_LONG).show();
        }
    }

    //Fa servir AsyncTask per descarregar el feed XML de stackoverflow.com
    public void carregaNoticies() {
        new DescarregaXmlTask().execute(URL);
    }

    public void carregaNoticiesModeOffline() {
        //SQLite sqLite = new SQLite(this, "mibaseguay", null, 1);
        //SQLiteDatabase dbreader = sqLite.getReadableDatabase();
        //Cursor c = dbreader.rawQuery("SELECT * FROM mibaseguay", null);

        bd.obre();

        Cursor mCursor = bd.obtenirTotesNoticies();

        for(int i = 0; i < mCursor.getCount(); i++) {
            mCursor.moveToPosition(i);
            String titol = mCursor.getString(mCursor.getColumnIndex("titol"));
            String enllac = mCursor.getString(mCursor.getColumnIndex("enllac"));
            String descripcio = mCursor.getString(mCursor.getColumnIndex("resum"));
            String autor = mCursor.getString(mCursor.getColumnIndex("autor"));
            String categoria = mCursor.getString(mCursor.getColumnIndex("categories"));
            String dataPublicacio = mCursor.getString(mCursor.getColumnIndex("pubdata"));
            llistaEntrades.add(i, new Noticia(titol, autor, enllac, descripcio, dataPublicacio, categoria));
        }
        bd.tanca();

        emplenarRV();
    }

    //Descarrega XML d'eldiario.es, l'analitza i crea amb ell un codi HTML que retorna com String
    private void carregaXMLdelaXarxa(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;

        //Creem una instància de l'analitzador i una llista de noticies
        StackOverflowXmlParser analitzador = new StackOverflowXmlParser();
        List<Noticia> entrades = null;

        try {
            //Obrim la connexio
            stream = obreConnexioHTTP(urlString);

            //Obtenim la llista d'entrades a partir de l'stream de dades
            entrades = analitzador.analitza(stream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //Tanquem l'stream una vegada hem terminat de treballar amb ell
            if (stream != null) {
                stream.close();
            }
        }

        //Si tenim noticies
        if (entrades != null) {
            // Guardem les noticies a la llista de noticies
            int i = 0;
            String atoi = "";
            bd.obre();

            // Buidem la BD a lo béstia! per poder enmagatzemar noticies noves.
            for (int j = 0; j < 10; j++) {
                atoi = atoi.valueOf(j);
                bd.esborrarNoticia(atoi);
            }

            for (Noticia noticia : entrades) {
                noticia.setId(i);
                noticia.setThumbnailPath(Environment.getExternalStorageDirectory().toString()+"/imatge"+noticia.getId()+".jpg");
                llistaEntrades.add(noticia);

                //Inserim un altre
                bd.inserirNoticia(noticia.getId().toString(), noticia.getTitol().toString(), noticia.getResum().toString(),
                        noticia.getEnllac().toString(), noticia.getAutor().toString(), noticia.getCategories().toString(),
                        noticia.getDataPublicacio().toString());

                //Tanquem la BD
                i++;

                new TascaDescarrega().execute(noticia);
            }
            bd.tanca();
        }
    }

    //Obre una connexió HTTP a partir d'un URL i retorna un InputStream
    private InputStream obreConnexioHTTP(String adrecaURL) throws IOException {
        InputStream in = null;        //Buffer de recepció
        int resposta = -1;            //Resposta de la connexió

        //Obtenim un URL a partir de l'String proporcionat
        URL url = new URL(adrecaURL);

        //Obtenim una nova connexió al recurs referenciat per la URL
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

        try {
            // Preparem la petició

            httpConn.setReadTimeout(10000);            //Timeout de lectura en milisegons
            httpConn.setConnectTimeout(15000);        //Timeout de connexió en milisegons
            httpConn.setRequestMethod("GET");        //Petició al servidor
            httpConn.setDoInput(true);                //Si la connexió permet entrada

            //Es connecta al recurs.
            httpConn.connect();

            //Obtenim el codi de resposta obtingut del servidor remot HTTP
            resposta = httpConn.getResponseCode();

            //Comprovem si el servidor ens ha retornat un codi de resposta OK,
            //que correspon a que el contingut s'ha descarregat correctament
            if (resposta == HttpURLConnection.HTTP_OK) {
                //Obtenim un Input stream per llegir del servidor
                //in = new BufferedInputStream(httpConn.getInputStream());
                in = httpConn.getInputStream();
            }
        } catch (Exception ex) {
            //Hi ha hagut un problema al connectar
            throw new IOException("Error connectant");
        }

        //Retornem el flux de dades
        return in;
    }


    public void onStart() {
        super.onStart();

        llistaEntrades.clear();

        //Tornem a actualitzar l'estat de la xarxa
        actualitzaEstatXarxa();
    }

    @Override
    public void onClick(View v) {
        if (v.equals(refresh)) {
            llistaEntrades.clear();
            actualitzaEstatXarxa();
        }

        if (v.equals(search)) {
            if (searchBar.getVisibility() == View.GONE) {
                searchBar.setVisibility(View.VISIBLE);
            }
            else {
                searchBar.setVisibility(View.GONE);
            }
        }
    }

    //Implementació d'AsyncTask per descarregar el feed XML de stackoverflow.com
    private class DescarregaXmlTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            rView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                //Carreguem l'XML
                carregaXMLdelaXarxa(urls[0]);
            } catch (IOException e) {
                System.out.println(getResources().getString(R.string.connection_error));
            } catch (XmlPullParserException e) {
                System.out.println(getResources().getString(R.string.xml_error));
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            rView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            emplenarRV();
        }
    }

    public void emplenarRV() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        Adaptador adapter = new Adaptador(getBaseContext(), llistaEntrades);
        adapter.setList(llistaEntrades);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter.notifyDataSetChanged();
    }

    private class TascaDescarrega extends AsyncTask<Noticia, Integer, Void> {
        // Tasca que fa les operacions de xarxa, no es pot manipular l'UI des d'aquí
        @Override
        protected Void doInBackground(Noticia... noticia) {
            try {
                Noticia noticiaAux = noticia[0];
                String pathImage = noticiaAux.getThumbnailPath();
                String urlThumbnail = noticiaAux.getThumbnail();
                int count, descarregat = 0;
                byte[] bufferImatge = new byte[1024];
                URL imatge = new URL(urlThumbnail);
                HttpURLConnection connection = (HttpURLConnection) imatge.openConnection();
                int totalImatge= connection.getContentLength();

                // Creem l'input i un buffer on anirem llegint la informació
                InputStream inputstream = (InputStream) imatge.getContent();
                OutputStream outputstream = new FileOutputStream(pathImage);

                // Mentre hi hagi informació que llegir
                while ((count = inputstream.read(bufferImatge)) != -1) {
                    // Acumulem tot el que ha llegit
                    descarregat += count;

                    // Calculem el percentatge respecte el total i ho enviem a publishProgress
                    publishProgress(((descarregat * 100) / totalImatge));

                    // Guardem al disc el que hem descarregat
                    outputstream.write(bufferImatge, 0, count);
                }
                // Tanquem els "stream"
                inputstream.close();
                outputstream.close();
            } catch (IOException exception) {
                System.out.println("ERROR: Alguna cosa no ha anat bé!");
                return null;
            }
            // No passem cap informació al onPostExecute
            return null;
        }
    }
}