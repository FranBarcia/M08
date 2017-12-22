package com.ioc.fbarcia.eac2_2017s1;

/**
 * Created by fbarcia on 23/10/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBTools {
    //Constants

    public static final String ID = "_id";
    public static final String TITOL = "titol";
    public static final String ENLLAC = "enllac";
    public static final String RESUM = "resum";
    public static final String AUTOR = "autor";
    public static final String CATEGORIES = "categories";
    public static final String PUBDATA = "pubdata";

    public static final String TAG = "DBInterface";

    public static final String BD_NOM = "BDNoticies";
    public static final String BD_TAULA = "noticies";
    public static final int VERSIO = 1;

    public static final String BD_CREATE =
            "create table " + BD_TAULA + "( " + ID + " integer primary key, " +
            TITOL + " text not null, " + ENLLAC + " text not null, " + RESUM + " text not null, "
            + AUTOR + " text not null, " + CATEGORIES + " text not null, " + PUBDATA + " text not null);";

    private final Context context;
    private BDAux auxiliar;
    private SQLiteDatabase bd;

    public DBTools(Context con) {
        this.context = con;
        auxiliar = new BDAux(context);
    }

    //Obre la BD
    public DBTools obre() throws SQLException {
        bd = auxiliar.getWritableDatabase();
        return this;
    }

    //Tanca la BD
    public void tanca() {
        auxiliar.close();
    }

    //Insereix una noticia
    public long inserirNoticia (String id, String title, String summary, String link, String author, String cathegories, String pubDate) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(ID, Integer.parseInt(id));
        initialValues.put(TITOL, title);
        initialValues.put(RESUM, summary);
        initialValues.put(ENLLAC, link);
        initialValues.put(AUTOR, author);
        initialValues.put(CATEGORIES, cathegories);
        initialValues.put(PUBDATA, pubDate);

        return bd.insert(BD_TAULA, null, initialValues);
    }

    //Esborrae una noticia
    public boolean esborrarNoticia(String IDFila) {
        return bd.delete(BD_TAULA, ID + " = " + Integer.parseInt(IDFila), null) > 0;
    }

    //Retorna una noticia
    public Cursor obtenirNoticia(String IDFila) throws SQLException {
        //Cursor mCursor = bd.query(true, BD_TAULA, new String[]{CLAU_ID, CLAU_NOM, CLAU_EMAIL}, CLAU_ID + " = " + IDFila, null, null, null, null, null);
        Cursor mCursor = bd.query(true, BD_TAULA, new String[]{TITOL, RESUM, ENLLAC, AUTOR, CATEGORIES, PUBDATA}, ID + " = " + Integer.parseInt(IDFila), null, null, null, null, null);

        if (mCursor != null)
            mCursor.moveToFirst();

        return mCursor;

    }

    //Retornar totes les noticies
    public Cursor obtenirTotesNoticies() {
        return bd.query(BD_TAULA, new String[]{ID, TITOL, RESUM, ENLLAC, AUTOR, CATEGORIES, PUBDATA}, null, null, null, null, null, null);
    }

    //Modificar una noticia
    public boolean actualitzarNoticies(String IDFila, String title, String summary, String link, String author, String cathegories, String pubDate) {
        ContentValues args = new ContentValues();
        args.put(ID, Integer.parseInt(IDFila));
        args.put(TITOL, title);
        args.put(RESUM, summary);
        args.put(ENLLAC, link);
        args.put(AUTOR, author);
        args.put(CATEGORIES, cathegories);
        args.put(PUBDATA, pubDate);
        return bd.update(BD_TAULA, args, ID + " = " + Integer.parseInt(IDFila), null) > 0;
    }

    private static class BDAux extends SQLiteOpenHelper {
        BDAux(Context con) {
            super(con, BD_NOM, null, VERSIO);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(BD_CREATE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int VersioAntiga, int VersioNova) {
            Log.w(TAG, "Actualitzant Base de dades de la versió" + VersioAntiga + " a " + VersioNova + ". Destruirà totes les dades");
            db.execSQL("DROP TABLE IF EXISTS " + BD_TAULA);

            onCreate(db);
        }
    }

}
