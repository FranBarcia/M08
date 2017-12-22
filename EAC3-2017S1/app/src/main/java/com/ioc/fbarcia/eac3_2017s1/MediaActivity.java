package com.ioc.fbarcia.eac3_2017s1;

import android.app.ActionBar;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by fbarcia on 07/11/2017.
 */

public class MediaActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String ITEM_PASSAT = MediaActivity.class.getCanonicalName() + ".ITEM";
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_complet);

        LinearLayout fotoLayout = (LinearLayout)findViewById(R.id.fotoLayout);
        LinearLayout videoLayout = (LinearLayout)findViewById(R.id.videoLayout);
        ImageView visualitzarFoto = (ImageView)findViewById(R.id.fotoView);
        VideoView visualitzarVideo = (VideoView)findViewById(R.id.videoView);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);

        //Rebem l'item de l'altre activity
        Bundle extras = getIntent().getExtras();
        Media media = (Media)extras.getSerializable(ITEM_PASSAT);

        // Si el multimedia passat es una imatge
        if (media.getTipusMedia() == MainActivity.FOTO) {
            videoLayout.setVisibility(View.INVISIBLE);
            fotoLayout.setVisibility(View.VISIBLE);
            visualitzarFoto.setImageURI(Uri.parse(media.getUbicacio()));
        // Si es un vídeo
        } else if (media.getTipusMedia() == MainActivity.VIDEO) {
            fotoLayout.setVisibility(View.INVISIBLE);
            videoLayout.setVisibility(View.VISIBLE);
            visualitzarVideo.setVideoURI(Uri.parse(media.getUbicacio()));
            visualitzarVideo.setMediaController(new MediaController(this));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Bundle extras = getIntent().getExtras();
        Media media = (Media)extras.getSerializable(ITEM_PASSAT);
        mMap = googleMap;

        // Add a marker in 'You are here' and move the camera
        LatLng localitzacio = new LatLng(media.getLatitud(), media.getLongitud());
        mMap.addMarker(new MarkerOptions().position(localitzacio).title("Ets aquí"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(localitzacio));
    }
}
