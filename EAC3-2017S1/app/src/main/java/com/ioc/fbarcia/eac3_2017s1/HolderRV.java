package com.ioc.fbarcia.eac3_2017s1;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

import static com.ioc.fbarcia.eac3_2017s1.R.id.tipusFitxer;
import static com.ioc.fbarcia.eac3_2017s1.R.id.nomFitxer;
import static com.ioc.fbarcia.eac3_2017s1.R.layout.media;

/**
 * Created by fbarcia on 07/11/2017.
 */

public class HolderRV extends RecyclerView.Adapter<HolderRV.ElMeuViewHolder> {
    private ArrayList<Media> items;
    private Context context;
    DBTools bd;

    //Creem el constructor
    public HolderRV(Context context, ArrayList<Media> items) {
        this.context = context;
        this.items = items;
    }

    public void setList(ArrayList<Media> llistaMedia) {
        this.items = llistaMedia;
        this.notifyDataSetChanged();
    }

    public Context getContext() {
        return this.context;
    }

    //Crea noves files (l'invoca el layout manager). Aquí fem referència al layout media.xml
    @Override
    public HolderRV.ElMeuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(media, null);
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
        Media media = items.get(position);
        String nomFitxer = media.getNom();

        Integer tipusMedia = media.getTipusMedia();

        viewHolder.vName.setText(nomFitxer);

        if (tipusMedia == 0)
            viewHolder.vIcon.setImageResource(android.R.drawable.ic_menu_gallery);
        else
            viewHolder.vIcon.setImageResource(android.R.drawable.ic_menu_slideshow);
    }

    //Definim el nostre ViewHolder, és a dir, un element de la llista en qüestió
    public class ElMeuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        protected TextView vName;
        protected ImageView vIcon;

        public ElMeuViewHolder(View v) {
            super(v);
            //El referenciem al layout
            vName = (TextView)v.findViewById(nomFitxer);
            vIcon = (ImageView)v.findViewById(tipusFitxer);
            v.setOnClickListener(this);
            v.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //Gracies a la posició del item sabrem quin item hem de tractar.
            int position = getAdapterPosition();
            Media media = items.get(position);

            //Cridem a la activity del item amb un intent
            Intent intent = new Intent(context, MediaActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            //Pasem l'item multimedia actual al bundle per comunicar-nos amb l'altre activity
            Bundle extras = new Bundle();
            extras.putSerializable(MediaActivity.ITEM_PASSAT, media);

            //Pasem el bundle al intent i arranquem la activity
            intent.putExtras(extras);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            //Eliminem l'item seleccionat de la llista de items
            int position = getAdapterPosition();

            bd = new DBTools(getContext());
            bd.obre();
            bd.esborrarItem(items.get(position).getId());
            bd.tanca();

            items.remove(position);
            notifyItemRemoved(position);

            return true;
        }
    }
}
