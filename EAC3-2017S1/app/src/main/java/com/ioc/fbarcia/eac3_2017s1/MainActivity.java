package com.ioc.fbarcia.eac3_2017s1;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // Constants per saber el tipus de media
    public static final int FOTO = 0;
    public static final int VIDEO = 1;

    // Constants per saber si s'ha de fer foto o video
    private static final int APP_FOTO = 0;
    private static final int APP_VIDEO = 1;

    private static final int PERMISSIONS = 1;
    private Uri identificadorImatge;
    private ArrayList<Media> llistaItems = new ArrayList<Media>();
    DBTools bd;
    LocationManager locationManager;
    Double latitud, longitud;

    ImageButton btnPhoto;
    ImageButton btnVideo;
    RecyclerView rView;

    LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitud = location.getLongitude();
            latitud = location.getLatitude();
            verificarLocalitzacio();
        }

        @Override public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override public void onProviderEnabled(String provider) {  requisits();    }
        @Override public void onProviderDisabled(String provider) { desactivoBotons();  }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bd = new DBTools(this);

        btnPhoto = (FloatingActionButton) findViewById(R.id.btnPhoto);
        btnVideo = (FloatingActionButton) findViewById(R.id.btnVideo);

        btnPhoto.setOnClickListener(this);
        btnVideo.setOnClickListener(this);

        rView = (RecyclerView) findViewById(R.id.rView);

        // Verifiquem els requisits previs
        requisits();
        // I carreguem les noticies de la BD si hi ha
        // El calcularID() retornarà l'id de l'últim element de la BD
        // Si l'id es 0, la BD es buida, si no, hi ha elements
        if (calcularId() != 0) carregarMediaDB();
    }

    public void onClick(View btnPushed) {
        File directoriMultimedia = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/Multimedia");

        // Comprovem si el directori on es guardaran els objectes multimedia existeix, i si no existeix, el creem
        if (comprovarDirectori(directoriMultimedia) == false) {
            File directoriMultmedia = new File(directoriMultimedia.getAbsolutePath());
            directoriMultmedia.mkdir();
        }
        try {
            if (btnPushed.equals(btnPhoto)) {
                ferFoto(directoriMultimedia);
            } else if (btnPushed.equals(btnVideo)) {
                ferVideo(directoriMultimedia);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Boolean comprovarDirectori(File directori) {
        Boolean existeix;

        if (directori.exists()) existeix = true;
        else existeix = false;

        return existeix;
    }

    public void ferFoto(File directori) throws IOException {
        String data = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String extensio = ".jpg";
        String nomImatge = "JPEG_" + data + extensio;

        // Es crea l'intent per l'aplicació de fotos
        Intent fotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Es crea un nou fitxer a l'emmagatzematge extern i se li passa a l'intent
        File foto = new File(directori, nomImatge);
        fotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(foto));
        // Es desa l'identificador de la imatge per recuperar-la quan estigui feta
        identificadorImatge = Uri.fromFile(foto);
        // S'engega l'activitat
        startActivityForResult(fotoIntent, APP_FOTO);
    }

    public void ferVideo(File directori) throws IOException {
        String data = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String extensio = ".mp4";
        String nomImatge = "MP4_" + data + extensio;

        // Es crea l'intent per l'aplicació de fotos
        //Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        // Es crea un nou fitxer a l'emmagatzematge extern i se li passa a l'intent
        File video = new File(directori, nomImatge);
        videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(video));
        // Es desa l'identificador de la imatge per recuperar-la quan estigui feta
        identificadorImatge = Uri.fromFile(video);
        // S'engega l'activitat
        startActivityForResult(videoIntent, APP_VIDEO);
    }

    public void requisits() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Si tenim els permisos necessaris, arranquem el listener del GPS per agafar coordenades
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
         && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
         && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
         && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
         && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
         && ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, locationListenerGPS);
        }
        // Si no, els demanem
        else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.INTERNET}, PERMISSIONS);
        }
        verificarLocalitzacio();
    }

    private void verificarLocalitzacio() {
        // Si tenim coordenades, activem botons, si no, els desactivem
        if (latitud != null && longitud != null) activoBotons();
        else desactivoBotons();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // En cas que fem una foto
            case APP_FOTO:
                // Si s'ha pogut fer correctament
                if (resultCode == Activity.RESULT_OK) {
                    ContentResolver contRes = getContentResolver();
                    contRes.notifyChange(identificadorImatge, null);
                    Integer id = calcularId();

                    // Guardem la foto i la seva info en un objecte tipo Media
                    Media media = new Media();
                    media.setId(id);
                    media.setNom(identificadorImatge.getLastPathSegment());
                    media.setUbicacio(identificadorImatge.getPath());
                    media.setTipusMedia(FOTO);
                    media.setLatitud(latitud);
                    media.setLongitud(longitud);

                    // Afegim aquest objecte Media a la llista d'elements i el fiquem al recyclerView
                    llistaItems.add(0, media);
                    emplenarRV();

                    // Guardem aquest element a la BD
                    bd.obre();
                    bd.inserirItem(media.getId(), media.getNom(), media.getUbicacio(), media.getTipusMedia(), media.getLatitud(), media.getLongitud());
                    bd.tanca();

                } else {
                    Toast.makeText(this, R.string.cancel, Toast.LENGTH_SHORT).show();
                }
                break ;
            // Si es un video
            case APP_VIDEO:
                // I s'ha pogut fet correctament, ho guardem igual que la foto
                if (resultCode == Activity.RESULT_OK) {
                    ContentResolver contRes = getContentResolver();
                    contRes.notifyChange(identificadorImatge, null);
                    Integer id = calcularId();

                    Media media = new Media();
                    media.setId(id);
                    media.setNom(identificadorImatge.getLastPathSegment());
                    media.setUbicacio(identificadorImatge.getPath());
                    media.setTipusMedia(VIDEO);
                    media.setLatitud(latitud);
                    media.setLongitud(longitud);
                    llistaItems.add(0, media);
                    emplenarRV();

                    bd.obre();
                    bd.inserirItem(media.getId(), media.getNom(), media.getUbicacio(), media.getTipusMedia(), media.getLatitud(), media.getLongitud());
                    bd.tanca();
                }
                // Si s'ha cancelat l'acció, es mostra un missatge
                else {
                    Toast.makeText(this, R.string.cancel, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private Integer calcularId() {
        Integer id;

        bd.obre();
        id = bd.obtenirUltimId();
        bd.tanca();

        return id;
    }

    private void carregarMediaDB() {
        bd.obre();

        // Obtenim tots els elements que tenim a la BD
        Cursor mCursor = bd.obtenirTotsItems();

        for(int i = 0; i < mCursor.getCount(); i++) {
            mCursor.moveToPosition(i);
            Integer id = mCursor.getInt(mCursor.getColumnIndex(DBTools.ID));
            String nom = mCursor.getString(mCursor.getColumnIndex(DBTools.NOM));
            String ubicacio = mCursor.getString(mCursor.getColumnIndex(DBTools.UBICACIO));
            Integer tipusMedia = mCursor.getInt(mCursor.getColumnIndex(DBTools.TIPUSMEDIA));
            Double latitud = Double.parseDouble(mCursor.getString(mCursor.getColumnIndex(DBTools.LATITUD)));
            Double longitud = Double.parseDouble(mCursor.getString(mCursor.getColumnIndex(DBTools.LONGITUD)));

            // Els afegim a la llista d'elements
            llistaItems.add(0, new Media(id, nom, ubicacio, tipusMedia, latitud, longitud));
        }
        bd.tanca();

        // I els afegim al recycler
        emplenarRV();
    }

    public void emplenarRV() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        HolderRV adapter = new HolderRV(getBaseContext(), llistaItems);
        adapter.setList(llistaItems);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter.notifyDataSetChanged();
    }

    private void desactivoBotons() {
        btnPhoto.setEnabled(false);
        btnPhoto.setImageAlpha(64);
        btnVideo.setEnabled(false);
        btnVideo.setImageAlpha(64);
    }

    private void activoBotons() {
        btnPhoto.setEnabled(true);
        btnPhoto.setImageAlpha(255);
        btnVideo.setEnabled(true);
        btnVideo.setImageAlpha(255);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            // Es comproven els permisos, si no els tenim, tanquem la aplicació
            case PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                                            && grantResults[1] == PackageManager.PERMISSION_GRANTED
                                            && grantResults[2] == PackageManager.PERMISSION_GRANTED
                                            && grantResults[3] == PackageManager.PERMISSION_GRANTED
                                            && grantResults[4] == PackageManager.PERMISSION_GRANTED
                                            && grantResults[5] == PackageManager.PERMISSION_GRANTED) {
                    // Comprovar localització
                    requisits();
                } else {
                    Toast.makeText(MainActivity.this, R.string.permissionMissing, Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
