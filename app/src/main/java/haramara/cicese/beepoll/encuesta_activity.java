package haramara.cicese.beepoll;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;

import haramara.cicese.beepoll.db.rcConfig;
import haramara.cicese.beepoll.db.rcDataEnc;
import haramara.cicese.beepoll.db.rcEncuestado;
import haramara.cicese.beepoll.db.rcEncuestas;
import haramara.cicese.beepoll.db.rcRespuestas;

/**
 * Created by diseno on 9/18/15. for BeePoll
 */
public class encuesta_activity extends AppCompatActivity{
    private final String TAG = this.getClass().getName();
    Toolbar toolbar;
    int requestCode;
    private rcEncuestas rcEnc;
    private rcEncuestado rcEdo;
    private rcDataEnc rcDEnc;
    private int edoEdor = 1;
    private String idEdo = "";
    private String idEdor = "";
    private String desc = "";
    private String inst = "";
    private String sidEnc = "0";
    private String nom="";
    private final int REQUEST_CODE = 0;
    private Intent dataResult;
    private TextView tvEnc ;
    private TextView tvIDUser ;
    private TextView numSend;
    private TextView numApl;
    private String EncInfo;
    private ActionBar ab;
    private FloatingActionButton btnNext;
    private TextView titleEnc;
    private TextView infoEnc;
    private TextView subInfoEnc;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //----- variables y declaraciones
        setContentView(R.layout.encuestas_layout);
        setToolbar();
        FloatingActionButton fabNext = (FloatingActionButton) findViewById(R.id.btFAB);
//        fabNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.i(TAG, "Presionado");
//                Intent i = new Intent(getApplicationContext(), survey_activity.class);
//                startActivityForResult(i, requestCode);
//            }
//        });
        //----
        rcEnc = new rcEncuestas(getApplicationContext());
        rcEdo = new rcEncuestado((getApplicationContext()));
        rcRespuestas rcResp = new rcRespuestas(getApplicationContext());
        rcConfig rcCon = new rcConfig(getApplicationContext());
        rcDEnc = new rcDataEnc(getApplicationContext());
        Intent iReceived = getIntent();
        // Views Widgets
        titleEnc = (TextView) findViewById(R.id.titleEncuesta);
        infoEnc = (TextView) findViewById(R.id.infoEncuesta);
        subInfoEnc  = (TextView) findViewById(R.id.subInfoEncuesta);
//        TextView tvOpcs = (TextView) findViewById(R.id.tvOpciones);
//        btnNext= (FloatingActionButton)  findViewById(R.id.btFAB);

        //-----
        //load Intent info ( Encuesta)
        EncInfo =   iReceived.getStringExtra("ENC");
        try {
            rcCon.open();
            rcEdo.open();
            rcResp.open();
            idEdor = rcCon.readID();
            cargaInfoEncuesta(EncInfo);

        } catch (SQLException e) {
            e.printStackTrace();
        }

       /** Esto con opciones del toolbar **/
//        btnCon.setOnClickListener(new View.OnClickListener() { //oculta btns y nums, despliega next, cancel
//            @Override
//            public void onClick(View v) {
//                //busca encuestado
//                tr1.setVisibility(View.INVISIBLE);
//                tr2.setVisibility(View.INVISIBLE);
//                btnCon.setVisibility(View.INVISIBLE);
//                btnMenu.setVisibility(View.INVISIBLE);
//
//                if (checkNewUser()) {
//                    tvIDUser.setText(rcEdo.getClave());
//                }
//                if (rcEdo.checkEncuestado()) {
//                    idEdo = rcEdo.getClave();
//                    tvEnc.setText(Html.fromHtml(nom));
//                    tvIDUser.setText(idEdo);
//                }
////
//                rcEdo.close();
//                btnNext.setVisibility(View.VISIBLE);
//                btBack.setVisibility(View.VISIBLE);
//                tvEnc.setText(Html.fromHtml(EncInfo));
//            }
//        }); //end btn Next

        try {
            cargaInfoEncuesta(EncInfo);
            rcEnc.open();
            if(rcEnc.getEncs() > 0){
                ab.setTitle(rcEdo.getClave());
            }else
                ab.setTitle("Nuevo Encuestado");
            rcEnc.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        infoEnc.setText(Html.fromHtml(desc));



        //carga encuesta
        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(getApplicationContext(),"Carga encuestas",Toast.LENGTH_SHORT).show();
                idEdo = ab.getTitle().toString(); //tvIDUser.getText().toString();
                dataResult = new Intent(getApplicationContext(), survey_activity.class);
                dataResult.putExtra("encuesta",true);
                dataResult.putExtra("id_encuesta", sidEnc);
                dataResult.putExtra("titulo_encuesta", nom);
                dataResult.putExtra("id_encuestado", idEdo);
                dataResult.putExtra("id_encuestador",idEdor);
                startActivityForResult(dataResult, REQUEST_CODE);
//                setResult(RESULT_CANCELED, dataResult);
                //              finish();
            }
        });

//        btBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dataResult = new Intent();
//                dataResult.putExtra("Return", "0");
//                setResult(RESULT_CANCELED, dataResult);
//                finish();
//            }
//        });

    }

    /*
   Método privado para cargar la información de la encuesta en la vista actual, cargando el número
   de encuestas respondidas y el número de encuestas ya enviadas.
    */
    private void cargaInfoEncuesta(String encInfo) throws SQLException {
        ContentValues info ;
        rcEnc.open();
        rcDEnc.open();
        info = rcEnc.getInfoEncs(encInfo);
        if(info.size()>0) {
            nom = info.getAsString("nombre");
            desc = info.getAsString("descripcion");
            inst = info.getAsString("instrucciones");
            sidEnc = info.getAsString("id_encuesta");
//            int idEnc = Integer.valueOf(sidEnc);
            edoEdor = info.getAsInteger("datos_edo");
        }else{ desc = inst = "Sin información"; }
//        numSend.setText(String.valueOf(rcDEnc.readNumSend(Integer.valueOf(sidEnc), Integer.valueOf(idEdor))));
//        numApl.setText(String.valueOf(rcDEnc.readNumResp(Integer.valueOf(sidEnc), Integer.valueOf(idEdor))));
        rcEnc.close();
        rcDEnc.close();
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ab = getSupportActionBar();
        if (ab != null) {
            // Poner ícono del drawer toggle
//            ab.setHomeButtonEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeAsUpIndicator(R.mipmap.ic_action_discard);
            ab.setHomeButtonEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_encuestado, menu);
        return true; //super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addEncuestado:
                Log.i(TAG,"Agregar Encuestado");
                addEncuestado();
                return true;


            default:
                Log.i(TAG,"BACK");
                Log.i(TAG,"Item: "+item.getItemId());
                onBackPressed();
                return super.onOptionsItemSelected(item);
        }
    }

    private void addEncuestado() {
        Log.i(TAG,"Agregando usuario");
        Toast.makeText(getApplicationContext(),"Generando clave",Toast.LENGTH_SHORT).show();

                if (rcEdo.checkEncuestado()) {
                    ab.setTitle(rcEdo.getClave());
                    titleEnc.setText(Html.fromHtml(nom));
                }
//
        Intent iAddUser = new Intent(getApplicationContext(), encuestado_activity.class);
        iAddUser.putExtra("edo", edoEdor);
        startActivityForResult(iAddUser, REQUEST_CODE);
        try {
            rcEdo.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ab.setTitle(rcEdo.getClave());
        rcEdo.close();
    }



    public void onBackPressed(){
        super.onBackPressed();
        Log.i(TAG, "Back PRessed");
    }

    /*
   Método para recibir los resultados al término de cada actividad ejecutada
   Recibiendo los códigos de
   88 -> regresa de Draft
   55 -> regresa de AddEncuestado
   44, 77 -> termina la actividad de Encuestas y termina la actividad de encuestaActivity

    */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null){
            Log.i(TAG,"NULL");
        }else
        if (!data.hasExtra("Return"))
            Log.i("BCAN", "Boton Cancel");
        else
        if(data.hasExtra("clave")) Log.i(TAG,"Creando Usuario Anonimo");

        if (resultCode == 0 && requestCode == REQUEST_CODE) {
            Log.i(TAG,"Cancele botón survey activity");
        }
        switch(resultCode){
            case 88: // viene de Draft
//                btnNext.setVisibility(View.INVISIBLE);
                break;
            case 99: //viene de Add User
                ab.setTitle(data != null ? data.getExtras().getString("clave") : "");
//                tvIDUser.setText(data != null ? data.getExtras().getString("clave") : "");
                break;
            case 55:// viene de AskNewUser (NO)
                ab.setTitle(data != null ? data.getExtras().getString("clave") : "");
//                tvIDUser.setText(data != null ? data.getExtras().getString("clave") : "");
                break;
            case 44: //regresar
            case 77:
                finish();
                break;
            case RESULT_OK:
                ab.setTitle(data != null ? data.getExtras().getString("clave") : "");
//                tvIDUser.setText(data != null ? data.getExtras().getString("clave") : "");
//                btnNext.setVisibility(View.INVISIBLE);
                break;
        }

    }
}
