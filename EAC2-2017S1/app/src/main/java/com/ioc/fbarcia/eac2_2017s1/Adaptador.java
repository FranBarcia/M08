package com.ioc.fbarcia.eac2_2017s1;

/**
 * Created by fbarcia on 20/10/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

import static com.ioc.fbarcia.eac2_2017s1.R.id.image;
import static com.ioc.fbarcia.eac2_2017s1.R.id.title;
import static com.ioc.fbarcia.eac2_2017s1.R.layout.noticia;

public class Adaptador extends RecyclerView.Adapter<Adaptador.ElMeuViewHolder> {
    private ArrayList<Noticia> items;
    private Context context;



    //Creem el constructor
    public Adaptador(Context context, ArrayList<Noticia> items) {
        this.context = context;
        this.items = items;
    }

    public void setList(ArrayList<Noticia> llistaNoticies) {
        this.items = llistaNoticies;
        this.notifyDataSetChanged();
    }

    //Crea noves files (l'invoca el layout manager). Aquí fem referència al layout noticia.xml
    @Override
    public Adaptador.ElMeuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(noticia, null);
        // create ViewHolder
        ElMeuViewHolder viewHolder = new ElMeuViewHolder(itemLayoutView);
        return viewHolder;
    }
    //Retorna la quantitat de les dades
    @Override
    public int getItemCount() {
        return items.size();
    }

    //Carreguem els widgets amb les dades (l'invoca el layout manager)
    @Override
    public void onBindViewHolder(ElMeuViewHolder viewHolder, int position) {
        boolean imatgeDisponible;

        Noticia noticia = items.get(position);
        String title = noticia.getTitol();
        /*
        String desc = noticia.getResum();
        String link = noticia.getEnllac();
        String author = noticia.getAutor();
        String cathegories = noticia.getCategories();
        String pubDate = noticia.getDataPublicacio();
        String thumbnail = noticia.getThumbnail();
        */

        imatgeDisponible = Drawable.createFromPath(noticia.getThumbnailPath()) != null;

        viewHolder.vTitle.setText(title);

        if (imatgeDisponible)
            viewHolder.vThumbnail.setImageDrawable(Drawable.createFromPath(noticia.getThumbnailPath()));
        else
            viewHolder.vThumbnail.setImageResource(android.R.drawable.ic_menu_report_image);
    }

    //Definim el nostre ViewHolder, és a dir, un element de la llista en qüestió
    public class ElMeuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        protected TextView vTitle;
        protected ImageView vThumbnail;

        public ElMeuViewHolder(View v) {
            super(v);
            //El referenciem al layout
            vTitle = (TextView)v.findViewById(title);
            vThumbnail = (ImageView)v.findViewById(image);
            v.setOnClickListener(this);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //Gracies a la posició del item sabrem quina noticia hem de tractar.
            int position = getAdapterPosition();
            Noticia noticia = items.get(position);

            //Cridem a la activity de la noticia amb un intent
            Intent intent = new Intent(context, NoticiaActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            //Pasem la noticia actual al bundle per comunicar-nos amb l'altre activity
            Bundle extras = new Bundle();
            extras.putSerializable(NoticiaActivity.ITEM_PASSAT, noticia);

            //Pasem el bundle al intent i arranquem la activity
            intent.putExtras(extras);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            //Eliminem l'item seleccionat de la llista de noticies
            int position = getAdapterPosition();
            items.remove(position);
            notifyItemRemoved(position);
            return true;
        }
    }
}