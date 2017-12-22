package com.ioc.fbarcia.eac3_2017s1;

/**
 * Created by fbarcia on 07/11/2017.
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
    public static final String NOM = "nom";
    public static final String UBICACIO = "ubicacio";
    public static final String TIPUSMEDIA = "tipusMedia";
    public static final String LATITUD = "latitud";
    public static final String LONGITUD = "longitud";

    public static final String TAG = "DBInterface";

    public static final String BD_NOM = "BDMultimedia";
    public static final String BD_TAULA = "multimedia";
    public static final int VERSIO = 1;

    public static final String BD_CREATE =
            "create table " + BD_TAULA + "( " + ID + " integer primary key, " +
                    NOM + " text not null, " + UBICACIO + " text not null, " + TIPUSMEDIA + " integer not null, "
                    + LATITUD + " text not null, " + LONGITUD + " text not null);";

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

    //Insereix un item
    public long inserirItem (Integer id, String name, String path, Integer type, Double latitude, Double length) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(ID, id);
        initialValues.put(NOM, name);
        initialValues.put(UBICACIO, path);
        initialValues.put(TIPUSMEDIA, type);
        initialValues.put(LATITUD, latitude.toString());
        initialValues.put(LONGITUD, length.toString());

        return bd.insert(BD_TAULA, null, initialValues);
    }

    //Esborrar un item
    public boolean esborrarItem(Integer id) {
        return bd.delete(BD_TAULA, ID + " = " + id, null) > 0;
    }

    //Retorna l'últim id
    public Integer obtenirUltimId() throws SQLException {
        Integer id;
        Cursor mCursor = bd.query(BD_TAULA, new String[]{"MAX(_id)"}, ID, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
            id = mCursor.getInt(0);
            id++;
        } else id = 0;

        return id;
    }

    //Retornar tots els items
    public Cursor obtenirTotsItems() {
        return bd.query(BD_TAULA, new String[]{ID, NOM, UBICACIO, TIPUSMEDIA, LATITUD, LONGITUD}, null, null, null, null, null, null);
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
            Log.w(TAG, R.string.updateDB + VersioAntiga + " a " + VersioNova + ". Destruirà totes les dades");
            db.execSQL("DROP TABLE IF EXISTS " + BD_TAULA);

            onCreate(db);
        }
    }

}
