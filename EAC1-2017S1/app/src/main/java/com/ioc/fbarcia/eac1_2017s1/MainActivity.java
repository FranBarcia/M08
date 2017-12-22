package com.ioc.fbarcia.eac1_2017s1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity{

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    //Declarem les variables per donar funcionalitat als elements de la UI
    ImageButton[] botons = new ImageButton[6];
    EditText editName;
    TextView[] etiquetes = new TextView[13];
    Button botoSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assignem els objetes de la UI a objectes en Java
        editName = (EditText)findViewById(R.id.editName);
        botoSend = (Button)findViewById(R.id.btnSend);

        botons[0] = (ImageButton)findViewById(R.id.btnAdd);
        botons[1] = (ImageButton)findViewById(R.id.btnErase);
        botons[2] = (ImageButton)findViewById(R.id.btnEdit);
        botons[3] = (ImageButton)findViewById(R.id.btnCancel);
        botons[4] = (ImageButton)findViewById(R.id.btnPhone);
        botons[5] = (ImageButton)findViewById(R.id.btnWeb);

        etiquetes[0] = (TextView)findViewById(R.id.txtName);
        etiquetes[1] = (TextView)findViewById(R.id.lblAdresa);
        etiquetes[2] = (TextView)findViewById(R.id.lblCarrer);
        etiquetes[3] = (TextView)findViewById(R.id.txtCarrer);
        etiquetes[4] = (TextView)findViewById(R.id.lblCP);
        etiquetes[5] = (TextView)findViewById(R.id.txtCP);
        etiquetes[6] = (TextView)findViewById(R.id.lblPoblacio);
        etiquetes[7] = (TextView)findViewById(R.id.txtPoblacio);
        etiquetes[8] = (TextView)findViewById(R.id.lblPersonalData);
        etiquetes[9] = (TextView)findViewById(R.id.lblTlf);
        etiquetes[10] = (TextView)findViewById(R.id.txtTlf);
        etiquetes[11] = (TextView)findViewById(R.id.lblWeb);
        etiquetes[12] = (TextView)findViewById(R.id.txtWeb);

        botoSend.setVisibility(View.INVISIBLE);

        //Donem visibilitat o la traiem dels elements que ens interessen
        for (ImageButton unBoto:botons){
            if ((unBoto.getId() != findViewById(R.id.btnAdd).getId()) && (unBoto.getId() != findViewById(R.id.btnErase).getId())){
                unBoto.setVisibility(View.INVISIBLE);
            }
        }

        for (TextView unaEtiqueta:etiquetes){
            if (unaEtiqueta.getId() != findViewById(R.id.txtName).getId()){
                unaEtiqueta.setVisibility(View.INVISIBLE);
            }
        }
    }

    //Carreguem les dades del SharedPreferences per tractarles al tornar a la app
    @Override
    public void onResume(){
        super.onResume();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPref.edit();
        getData();
    }

    //Identifiquem si hem fet clic al botó que voliem i mirem el seu contingut, si es buit, es mostra missatge toast, si no, es mostren uns objectes i s'oculten uns altres
    public void onClickNou (View v) {
        if (editName.getText().length() == 0) {
            Toast.makeText(MainActivity.this, "Heu d'escriure quelcom!", Toast.LENGTH_SHORT).show();
        }
        else {
            for (ImageButton unBoto:botons){
                if (unBoto.getVisibility() == View.VISIBLE){
                    unBoto.setVisibility(View.INVISIBLE);
                }
                else {
                    unBoto.setVisibility(View.VISIBLE);
                }
            }
            for (TextView unaEtiqueta:etiquetes){
                if (unaEtiqueta.getVisibility() == View.INVISIBLE){
                    unaEtiqueta.setVisibility(View.VISIBLE);
                }
            }
            editName.setVisibility(View.INVISIBLE);
            etiquetes[0].setText(editName.getText());
            botoSend.setVisibility(View.VISIBLE);
        }
    }

    //Eliminem el text escrit al EditText inicial
    public void onClickErase (View v) {
        if (editName.getText().length() != 0) {
            editName.setText("");
        }
    }

    //Cridem al SecondActivity
    public void onClickEditar (View v) {
        Intent SecondActivity = new Intent(getApplicationContext(), SecondActivity.class);
        startActivity(SecondActivity);
    }

    //Eliminem totes les dades i tornem a la pantalla inicial
    public void onClickCancel (View v) {
        etiquetes[3].setText("Valor no definit");
        etiquetes[5].setText("Valor no definit");
        etiquetes[7].setText("Valor no definit");
        etiquetes[10].setText("Valor no definit");
        etiquetes[12].setText("Valor no definit");
        onDestroy();
        onCreate(new Bundle());
    }

    //Si existeix, deixem el número de telefon escrit pero no marcat, si no existeix, mostrem missatge amb un toast
    public void onClickPhone (View v) {
        // Abre dial sin marcar, si vacio, toast con error
        if (etiquetes[10].getText().equals("Valor no definit")) {
            Toast.makeText(MainActivity.this, "El camp \"telèfon\" està buit", Toast.LENGTH_SHORT).show();
        } else {
            Intent dialPhone = new Intent(android.content.Intent.ACTION_DIAL, Uri.parse("tel: 654321177"));
            startActivity(dialPhone);
        }
    }

    //Carreguem la web si s'ha especificat, també es controla si te al davant http:// o https://, si no ho te, ho afegim
    public void onClickWeb (View v) {
        // Abre navegador de la web especificada, contemplar http://, si vacio, toast con error
        if (etiquetes[12].getText().equals("Valor no definit")) {
            Toast.makeText(MainActivity.this, "El camp \"pàgina web\" està buit", Toast.LENGTH_SHORT).show();
        }else {
            Intent viewWeb;
            if (!etiquetes[12].getText().toString().startsWith("http://") && !etiquetes[12].getText().toString().startsWith("https://")) {
                viewWeb = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://" + etiquetes[12].getText().toString()));
            } else {
                viewWeb = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(etiquetes[12].getText().toString()));
            }
            startActivity(viewWeb);
        }
    }

    //Validem les dades i mostrem un toast en cas que estiguin totes, en cas contrari, mostrem amb un toast un altre missatge
    public void onClickSend (View v) {
        // if todos los datos, enviar toast con datos enviados, si no, toast con faltan datos
        if ((etiquetes[3].getText().equals("Valor no definit")) ||
                (etiquetes[5].getText().equals("Valor no definit")) ||
                (etiquetes[7].getText().equals("Valor no definit")) ||
                (etiquetes[10].getText().equals("Valor no definit")) ||
                (etiquetes[12].getText().equals("Valor no definit"))) {
            Toast.makeText(MainActivity.this, "Error! Has d'emplenar totes les dades", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Dades enviades!", Toast.LENGTH_SHORT).show();
        }
    }

    //Amb aquest métode conseguim portar les dades de la SecondActivity
    public void getData() {
        String carrer = sharedPref.getString("carrer_clau", "Valor no definit");
        String codiPostal = sharedPref.getString("cp_clau", "Valor no definit");
        String poblacio = sharedPref.getString("poblacio_clau", "Valor no definit");
        String telefon = sharedPref.getString("tlf_clau", "Valor no definit");
        String web = sharedPref.getString("web_clau", "Valor no definit");

        ((TextView)findViewById(R.id.txtCarrer)).setText(carrer);
        ((TextView)findViewById(R.id.txtCP)).setText(codiPostal);
        ((TextView)findViewById(R.id.txtPoblacio)).setText(poblacio);
        ((TextView)findViewById(R.id.txtTlf)).setText(telefon);
        ((TextView)findViewById(R.id.txtWeb)).setText(web);

        editor.commit();
    }
}