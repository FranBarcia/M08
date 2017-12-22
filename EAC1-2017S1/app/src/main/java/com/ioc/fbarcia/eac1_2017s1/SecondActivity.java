package com.ioc.fbarcia.eac1_2017s1;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SecondActivity extends Activity{

    //Declarem les SharedPreferences per poder enviar dades entre activities
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_two);

        //Inicialitzem les SharedPreferences
        Context context = this.getApplicationContext();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPref.edit();
        getData();
    }

    //Guardem dades i tornem al MainActivity
    public void onClickAccept (View v) {
        saveData();
        finish();
    }

    //Cancelem l'acció i tornem al MainActivity
    public void onClickCancel (View v) {
        finish();
    }

    //Guardem les dades que hem introduit al formulari per passar-les al MainActivity
    public void saveData() {
        String carrer = ((EditText)findViewById(R.id.editCarrer)).getText().toString();
        String codiPostal = ((EditText)findViewById(R.id.editCP)).getText().toString();
        String poblacio = ((EditText)findViewById(R.id.editPoblacio)).getText().toString();
        String telefon = ((EditText)findViewById(R.id.editTlf)).getText().toString();
        String web = ((EditText)findViewById(R.id.editWeb)).getText().toString();

        if (carrer.length() == 0) { editor.putString("carrer_clau", "Valor no definit"); }
        else { editor.putString("carrer_clau", carrer); }

        if (codiPostal.length() == 0) { editor.putString("cp_clau", "Valor no definit"); }
        else { editor.putString("cp_clau", codiPostal); }

        if (poblacio.length() == 0) { editor.putString("poblacio_clau", "Valor no definit"); }
        else { editor.putString("poblacio_clau", poblacio); }

        if (telefon.length() == 0) { editor.putString("tlf_clau", "Valor no definit"); }
        else { editor.putString("tlf_clau", telefon); }

        if (web.length() == 0) { editor.putString("web_clau", "Valor no definit"); }
        else { editor.putString("web_clau", web); }

        editor.commit();

        Toast.makeText(getApplicationContext(),"Dades gardades", Toast.LENGTH_SHORT).show();
    }

    //Obtenim les dades de la MainActivity per no perdre-les
    public void getData() {
        String carrer = sharedPref.getString("carrer_clau", "");
        String codiPostal = sharedPref.getString("cp_clau", "");
        String poblacio = sharedPref.getString("poblacio_clau", "");
        String telefon = sharedPref.getString("tlf_clau", "");
        String web = sharedPref.getString("web_clau", "");

        if (carrer.equals("Valor no definit")) { carrer = ""; }
        if (codiPostal.equals("Valor no definit")) { codiPostal = ""; }
        if (poblacio.equals("Valor no definit")) { poblacio = ""; }
        if (telefon.equals("Valor no definit")) { telefon= ""; }
        if (web.equals("Valor no definit")) { web = ""; }

        ((EditText)findViewById(R.id.editCarrer)).setText(carrer);
        ((EditText)findViewById(R.id.editCP)).setText(codiPostal);
        ((EditText)findViewById(R.id.editPoblacio)).setText(poblacio);
        ((EditText)findViewById(R.id.editTlf)).setText(telefon);
        ((EditText)findViewById(R.id.editWeb)).setText(web);

        editor.commit();
    }
}