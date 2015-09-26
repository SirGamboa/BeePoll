package haramara.cicese.beepoll;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;

import haramara.cicese.beepoll.db.dbPreguntas;
import haramara.cicese.beepoll.db.dbRespuestas;
import haramara.cicese.beepoll.db.rcCompleted;
import haramara.cicese.beepoll.db.rcConfig;
import haramara.cicese.beepoll.db.rcDataEnc;
import haramara.cicese.beepoll.db.rcEncuestas;
import haramara.cicese.beepoll.db.rcOpciones;
import haramara.cicese.beepoll.db.rcPreguntas;
import haramara.cicese.beepoll.db.rcRespuestas;

/**
 * Created by diseno on 9/21/15. for BeePoll
 */
public class survey_activity extends AppCompatActivity {
    // para cargar dinámicamente los reactivos y opciones a responder
    private RelativeLayout llQ;
    private LinearLayout noImgRL;
    private RadioGroup rgResp;
    private final String[] sborrador = new String[]{"0","0","0","0","0"};
    private String   id_Encuestado;
    private String idEncuestador;
    private String title_Encuesta;
    private String sid_encuesta="";
    private String id_val;
    private int loop = 1;
    private int indexRespID = 1;
    private int tlPreguntas = 0;
    private int id_encuesta=0;
    private int tipo=0;
    private int idPregTipo=0;
    private rcEncuestas rcEnc;
    private rcPreguntas rcPreg;
    private rcRespuestas rcResp;
    private rcCompleted rcCom;
    private rcOpciones rcOpc;
    private rcDataEnc rcDEnc;
    private ContentValues cvPregs;
    private dbPreguntas dbPregs;
    private File directory;
    private final String TAG = this.getClass().getName();
    private boolean TAG_FirstTime = true;
    private boolean borradores = false;
    private boolean firstTimeBorrador = true;
    // Botones de navegación
    private ActionBar ab;
    Toolbar toolbar;
    private FloatingActionButton bNext;
    private EditText etRespuesta;
    private TextView tvPregunta;
    private TextView tvOpcs;
    private RadioGroup rgOpciones;
    ImageView ivPregs;
    View inflate;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.survey_layout);
//        add name to Toolbar
        setToolbar();
        // genera los modelos en las variables.
        rcEncuestas rcEnc = new rcEncuestas(this);
        rcPreg = new rcPreguntas(this);
        rcResp = new rcRespuestas(this);
        rcCom = new rcCompleted(this);
        rcConfig rcCon = new rcConfig(this);
        rcOpc = new rcOpciones(this);
        rcDEnc = new rcDataEnc(this);
        cvPregs = new ContentValues();
        TextView tvID = (TextView) findViewById(R.id.tvPreg);
        TextView tvEName = (TextView) findViewById(R.id.titleEncuesta);
        noImgRL = (LinearLayout) findViewById(R.id.RLAdd);
        llQ = (RelativeLayout) findViewById(R.id.llQuest);
        rgResp = (RadioGroup) findViewById(R.id.rgResp);
        ivPregs = (ImageView) findViewById(R.id.ivPregunta);
        try {
            rcPreg.open();
            rcResp.open();
            rcCom.open();
            rcEnc.open();
            rcCon.open();
            rcDEnc.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        bNext = (FloatingActionButton) findViewById(R.id.btFAB);


        /* Recibe datos de las vistas que mandan a llamar a surveyActivity
        * Viene de Borradores
        * */
        if( getIntent().hasExtra("borrador")){
            borradores = true;
            id_Encuestado = getIntent().getStringExtra("id_edo");
            Log.i("INTENT", id_Encuestado);
            sid_encuesta = getIntent().getStringExtra("id_enc");
            id_encuesta = Integer.valueOf(sid_encuesta);
            Log.i("INTENT", sid_encuesta);
            title_Encuesta = rcEnc.getEncName(sid_encuesta);

        }
        // Viene de Encuestas
        if(getIntent().hasExtra("encuesta")) {
            sid_encuesta = getIntent().getStringExtra("id_encuesta");
            id_encuesta = Integer.valueOf(sid_encuesta);
            title_Encuesta = getIntent().getStringExtra("titulo_encuesta");
            Log.i("INTENT", title_Encuesta);
            id_Encuestado = getIntent().getStringExtra("id_encuestado");
            Log.i("INTENT", id_Encuestado);
            idEncuestador = getIntent().getStringExtra("id_encuestador");
        }
        tvID.setText(Html.fromHtml(id_Encuestado));
        tvEName.setText(Html.fromHtml(title_Encuesta));
        Log.i(TAG, " " + id_encuesta);
        //metodo para cargar la última pregunta registrada
        if(borradores){
            loop = rcCom.getLastQ(sid_encuesta, id_Encuestado);
            idEncuestador = rcCon.readID();
            sborrador[0] = id_Encuestado;
            sborrador[1] = String.valueOf(id_encuesta);
            sborrador[2] = String.valueOf(loop);
            sborrador[3] = "1";
            sborrador[4] = idEncuestador;
            rcCom.deleteBorrador(sborrador);

            rcCon.close();

        }
        tlPreguntas = rcPreg.preguntasTotal(sid_encuesta);
        //checar si existen preguntas disponibles
        // revisar el ciclo para leer preguntas, hacer el insert de datos recibidos y checar que al finalizar las pregunas disponibles, salir de la ventana con éxito.
        if(TAG_FirstTime){
            //checar si ya respondió la encuesta
            if(rcCom.Checkdraft(id_Encuestado, id_encuesta, idEncuestador) && !borradores){
                Toast.makeText(getApplicationContext(), "El usuario : "+id_Encuestado+" tiene una encuesta en Borradores ", Toast.LENGTH_SHORT).show();
                finish();
            }
            if( rcResp.check(id_Encuestado, id_encuesta, tlPreguntas)){
                Toast.makeText(getApplicationContext(), "Ya has respondido la encuesta", Toast.LENGTH_SHORT).show();
                finish();
            }else {
                TAG_FirstTime = false;
            }
            cvPregs = rcPreg.readPreguntas(id_encuesta, loop);
            if(cvPregs.getAsString("descripcion").isEmpty())
            {
//                tvIns.setText(Html.fromHtml("Sin descripción"));
            }else
//                tvIns.setText(Html.fromHtml(cvPregs.getAsString("descripcion")));
             Log.i(TAG, String.valueOf(Html.fromHtml(cvPregs.getAsString("descripcion"))));
            tipo = Integer.valueOf(cvPregs.getAsString(dbPreguntas.COLUMN_NAME_TIPO));
            try {
                addResponse(tipo);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            checkBorrador(id_encuesta, loop, id_Encuestado);
        }



        bNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i(TAG, "NEXT BTN");
                if (saveResponse(idPregTipo)) {
                    loop = nextSurvey();
//                    tvIns.setText(Html.fromHtml(cvPregs.getAsString("descripcion")));
                    Log.i(TAG, String.valueOf(Html.fromHtml(cvPregs.getAsString("descripcion"))));

                    try {
                        if (loop <= tlPreguntas)
                            addResponse(tipo);
                        //  autoDraft(sborrador);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    if (loop > tlPreguntas) {
                        try {
                            rcCom.open();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        deleteBorrador(sborrador);
                        rcCom.deleteEdo(id_Encuestado);
                        endView(0);//
                    }
                }

            }
        });
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

    private void autoDraft(String[] sborrador) {
        try {
            rcCom.open();
            if(rcCom.updateDraft(sborrador) > 0){
                Log.i(TAG,"AutoSaved");
            } //else { saveDraft();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        rcCom.close();
    }

    private void saveDraft(String[] sborrador) {
        try {
            rcCom.open();
            if(rcCom.Checkdraft(sborrador)) { //
//                rcCom.draft(sborrador);
                int tm = Integer.valueOf(sborrador[2]);
                sborrador[2] = String.valueOf(tm - 1); //
                rcCom.updateDraft(sborrador);
                Toast.makeText(getApplicationContext(), "Se guardo en Borradores", Toast.LENGTH_SHORT).show();
            }else {
                rcCom.updateDraft(sborrador);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        rcCom.close();
    }

    private void checkBorrador(int ienc, int ipreg, String iedor) {
        // Revisa si existe borrador y agrega a el arreglo la posición, respuesta y la pregunta.
        sborrador[0] = iedor;
        sborrador[1] = String.valueOf(ienc);
        sborrador[2] = String.valueOf(ipreg);
        sborrador[3] = "1";
        sborrador[4] = idEncuestador;
        if(firstTimeBorrador) {
            firstTimeBorrador = false;
            saveDraft(sborrador);
        }

    }


    private void deleteBorrador(String[] sborrador) {
//        al finalizar, limpia el arreglo del borrador
        for(int i=0; i<4; i++){
            sborrador[i] = "0";
        }
    }

    private boolean saveResponse(@SuppressWarnings("UnusedParameters") int idTipo)  {
        /*
        Método para guardar en la DB local del dispositivo las respuestas a los reactivos de la encuesta
        verificando por el tipo de reactivo ( pregunta abierta, opción múltiple, valoración, escala de Likert
         */
        boolean r = false;
        int index = 0;
        ContentValues cv = new ContentValues();
        TextView tvResp = (TextView) findViewById(R.id.etResp);
        rgResp = (RadioGroup) findViewById(R.id.rgResp);
        if(rgResp != null)
            index =  rgResp.getChildCount();
//        else index = 0;
        RadioButton[] rb = new RadioButton[index];
        String tvText, rbText;
        /*
        Caso Pregunta Abierta
         */
        if (tvResp != null){
            tvText = tvResp.getText().toString();
            if(!tvText.isEmpty()) {
                cv.put(dbRespuestas.COLUMN_NAME_ENCUESTA_ID, String.valueOf(id_encuesta));
                cv.put(dbRespuestas.COLUMN_NAME_ENCUESTADO_ID, String.valueOf(id_Encuestado));
                cv.put(dbRespuestas.COLUMN_NAME_RESPUESTA, tvText);
                cv.put(dbRespuestas.COLUMN_NAME_PREGUNTA_ID, String.valueOf(indexRespID));
                cv.put(dbRespuestas.COLUMN_NAME_IDPREGTIPO, cvPregs.getAsString("idPregTipo"));
                cv.put(dbRespuestas.COLUMN_NAME_STATUS, "1");
                cv.put(dbRespuestas.COLUMN_NAME_ENCUESTADOR_ID, idEncuestador);
                sborrador[0] = String.valueOf(id_Encuestado);
                sborrador[1] = String.valueOf(id_encuesta);
                sborrador[2] = String.valueOf(indexRespID); //indexRespID
                sborrador[3] = "1";
                sborrador[4] = idEncuestador;
//                rcResp.addRespuestas(cv);
                tvResp.setText("");
                r = true;
            }else { Toast.makeText(getApplicationContext(), "Debes de escribir  una respuesta",Toast.LENGTH_SHORT).show();}
        }
        /*
        Caso Preguntas Likert, Opción múltiple, Valoración ( Radio Buttons )
         */
        if (index > 0){  /// rgResp != null
            for(int i=0; i<index; i++) {
                rb[i] = new RadioButton(getApplicationContext());
                rb[i].setId(i);
                int temp = i+1;
                rb[i].setText(""+temp);
            }

            int inxbtn = rgResp.getCheckedRadioButtonId();
            if(inxbtn > 0) {
                rbText = rb[inxbtn-1].getText().toString();
                cv.put(dbRespuestas.COLUMN_NAME_ENCUESTA_ID, String.valueOf(id_encuesta));
                cv.put(dbRespuestas.COLUMN_NAME_ENCUESTADO_ID, String.valueOf(id_Encuestado));
                cv.put(dbRespuestas.COLUMN_NAME_RESPUESTA,rbText);
                cv.put(dbRespuestas.COLUMN_NAME_IDPREGTIPO, cvPregs.getAsString("idPregTipo"));
                cv.put(dbRespuestas.COLUMN_NAME_PREGUNTA_ID, String.valueOf(indexRespID));
                cv.put(dbRespuestas.COLUMN_NAME_IDVAL, id_val);
                cv.put(dbRespuestas.COLUMN_NAME_STATUS, "1");
                cv.put(dbRespuestas.COLUMN_NAME_ENCUESTADOR_ID, idEncuestador);
                sborrador[0] = String.valueOf(id_Encuestado);
                sborrador[1] = String.valueOf(id_encuesta);
                sborrador[2] = String.valueOf(indexRespID);
                sborrador[3] = "1";
                sborrador[4] = idEncuestador;

//                rcResp.addRespuestas(cv);
                rgResp.clearCheck();
                r = true;
            }else{ Toast.makeText(getApplicationContext(), "Debes de seleccionar una opción",Toast.LENGTH_SHORT).show(); }
        }
        try {
            rcCom.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        rcCom.updateDraft(sborrador);
        rcCom.close();
        rcResp.addRespuestas(cv);
//        Regresa verdadero cuando se haya hecho correctamente el registro de la DB
        return r;
    }

//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void addResponse(int tipo) throws SQLException {
        /*
        Método para agregar a la vista la pregunta y su respuesta, de TextEdit o RadioButtons
         */
        String descItemC3,  descItem;
        String[] descItems;
        File f;
        int indx;
        RadioGroup vgRadioGroup; //ViewGroup Radio Buttons
        LinearLayout.LayoutParams params; // params for Pregunta
        if(cvPregs.getAsString("idImage").toString().length() > 5){
//            imgRL.setVisibility(View.VISIBLE);
//            noImgRL.setVisibility(View.INVISIBLE);

            ivPregs.setVisibility(View.VISIBLE);
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            directory = cw.getDir("images",getApplicationContext().MODE_PRIVATE);
            f = new File(directory,cvPregs.getAsString("idImage"));
            Bitmap b = null;
            try {
                b = BitmapFactory.decodeStream(new FileInputStream(f));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if(b != null)
                ivPregs.setImageBitmap(b);
            else
                ivPregs.setBackgroundColor(Color.LTGRAY);

        }else{
//            noImgRL.setVisibility(View.VISIBLE);
//            imgRL.setVisibility(View.INVISIBLE);
            ivPregs.setVisibility(View.GONE);

        }
        llQ = (RelativeLayout) findViewById(R.id.llQuest);
        llQ.removeAllViews();

        Log.i(TAG,"TIPO: "+tipo);
        Log.i(TAG,"LOOP: "+loop);
        switch (tipo){
            case 0: // son preguntas abiertas
                params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT)
                    ;
//                //EditText preguntas abiertas
                etRespuesta = new EditText(getApplicationContext());
                tvPregunta = (TextView) findViewById(R.id.tvPreg);
                etRespuesta.setId(R.id.etResp);
                etRespuesta.setLayoutParams(params);
//                tvPregunta.setId(R.id.tvPreg);
                tvPregunta.setText(cvPregs.getAsString("descripcion"));
                etRespuesta.setHint(" Escribir Respuesta ");
                etRespuesta.setInputType(InputType.TYPE_CLASS_TEXT);
                etRespuesta.setBackgroundColor(Color.WHITE);
                etRespuesta.setTextColor(Color.BLACK);
                llQ.addView(etRespuesta);

                break;
            case 3: // Valoración
                /*
                En este caso, la escala de Likert son varias preguntas dentro de una pregunta
                haciendo que las opciones a responder, sean las preguntas en sí.
                Cargo pregunta por pregunta a la vista ( las opciones a valorar) hasta encontrar
                otro tipo de reactivo ( abierta, de valoración u opción múltiple )
                 */

                vgRadioGroup =  new RadioGroup(this);// findViewById(R.id.rgResp);
                vgRadioGroup.setId(R.id.rgResp);
                LinearLayout.LayoutParams paramsRGC3 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                paramsRGC3.setLayoutDirection(LinearLayout.HORIZONTAL);
                rgResp = new RadioGroup(getApplicationContext());
                rgResp.setGravity(View.TEXT_ALIGNMENT_CENTER);
                rgResp.setHorizontalGravity(View.TEXT_ALIGNMENT_CENTER);
                rgResp = new RadioGroup(getApplicationContext());
                rgResp = (RadioGroup) findViewById(R.id.rgResp);//.addView(llQ);
//                vgRadioGroup = (ViewGroup) findViewById(R.id.rgResp);
                vgRadioGroup.setLayoutParams(paramsRGC3);
                //leer las preguntas multiples case 3 Valoracion
                rcPreg.open();
                String[] dataC3;
                String infoC3 = rcPreg.getPregunta(loop, id_encuesta);
                descItemC3 = rcPreg.getPregVal(loop, id_encuesta);
                dataC3 = infoC3.split(":");
                int indxC3 = Integer.valueOf(dataC3[1]);
                id_val = dataC3[2];
                tvOpcs.setLines(indxC3);
                RadioButton rbResp;
                if(indxC3 > 0) {

                    for (int i = 1; i <= indxC3; i++) {
                        rbResp = new RadioButton(getApplicationContext());
                        //noinspection ResourceType
                        rbResp.setId(i);
                        rbResp.setText("" + i);
                        rbResp.setTextColor(Color.BLACK);
                        //llQ.addView(rbResp);
                        vgRadioGroup.addView(rbResp);
//                        descItemC3 = "Valoración de 1 hasta " + i + "<br>";

//                        descItemC3 = tvOpcs.getText().toString();
                    }
//                    rcOpc.open();
//                    descItemC3 = rcOpc.getRespVal(cvPregs.getAsInteger("idPregTipo"), id_encuesta);
//                    rcOpc.close();
                    tvOpcs.append(descItemC3);
                    tvOpcs.setVisibility(View.VISIBLE);
                    tvOpcs.setText(Html.fromHtml(descItemC3));                }
                break;

            default: //preguntas opcion multiple
                tvPregunta = (TextView) findViewById(R.id.tvPreg);
                tvPregunta.setText(cvPregs.getAsString("descripcion"));
                vgRadioGroup =  new RadioGroup(this);// findViewById(R.id.rgResp);
                vgRadioGroup.setId(R.id.rgResp);

                LinearLayout.LayoutParams paramsRG = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                paramsRG.setLayoutDirection(LinearLayout.HORIZONTAL);
                rgResp = new RadioGroup(getApplicationContext());
                rgResp.setGravity(View.TEXT_ALIGNMENT_CENTER);
                rgResp.setHorizontalGravity(View.TEXT_ALIGNMENT_CENTER);
//                rgResp = (RadioGroup) findViewById(R.id.respuesta_opcional);
                rgResp = new RadioGroup(getApplicationContext());
                rgResp = (RadioGroup) findViewById(R.id.rgResp);//.addView(llQ);
                vgRadioGroup.setLayoutParams(paramsRG);
//                leer las opciones multiples
                try {
                    rcOpc.open();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                indx = rcOpc.getLenght(cvPregs.getAsInteger("idPregTipo"), id_encuesta);
                //noinspection UnusedAssignment
                descItems = new String[indx];
                descItem = "";
                //leer desc de cada option
                descItems = rcOpc.getDesc(cvPregs.getAsInteger("idPregTipo"), id_encuesta);
                rcOpc.close();
                RadioButton rbResp1;
                for (int i = 1; i <= indx; i++) {
                    rbResp1 = new RadioButton(getApplicationContext());
                    rbResp1.setId(i);
                    rbResp1.setText(i + "- " + descItems[i - 1]);
                    rbResp1.setTextColor(Color.BLACK);
                    vgRadioGroup.addView(rbResp1);
                }
                llQ.addView(vgRadioGroup);
            break;

        }

    }

    private int nextSurvey() {
        //agregar datos a tvID, tvEncuesta, tvPregunta.
        loop++;
        if( loop > tlPreguntas ){
            Toast.makeText(getApplicationContext(),"No hay Pregunta disponible", Toast.LENGTH_SHORT).show();
            rcResp.updateSurvey(id_encuesta, id_Encuestado);
            int i = rcDEnc.readNumResp(id_encuesta, Integer.valueOf(idEncuestador));
            i++;
            if(rcDEnc.check(i, id_encuesta, idEncuestador))
                rcDEnc.updateNumResp(i, id_encuesta,idEncuestador);
            else
                rcDEnc.addNumResp(i, id_encuesta, idEncuestador);
            if( loop>= tlPreguntas)
                rcDEnc.clearDraft(id_encuesta, idEncuestador);

            //update enviar borradores survey
        }
        cvPregs = rcPreg.readPreguntas(id_encuesta, loop);

        tipo = Integer.valueOf(cvPregs.getAsString(dbPreguntas.COLUMN_NAME_TIPO));
        idPregTipo = cvPregs.getAsInteger(dbPreguntas.COLUMN_NAME_IDPREGTIPO);
        indexRespID = loop;
        Log.i(TAG, "Auto-Draft");
        sborrador[0] = String.valueOf(id_Encuestado);
        sborrador[1] = String.valueOf(id_encuesta);
        sborrador[2] = String.valueOf(indexRespID);
        sborrador[3] = "1";
        sborrador[4] = idEncuestador;
        return loop;
    }

    private void endView(int info) {
        Intent dataResult = new Intent();
        dataResult.putExtra("Return", info);
        dataResult.putExtra("clave",ab.getTitle().toString());
        switch(info){
            default:
                setResult(RESULT_OK,dataResult);
                break;
            case 1:
                setResult(88,dataResult);
                break;
        }
        rcPreg.close();
        rcResp.close();
        rcDEnc.close();
        rcCom.close();
        finish();
    } //end view

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // do something on back.
            String datoAlerta = borradores ? "Debes de terminar el borrador": "¡¡Debes terminar la encuesta, o guardarla (Borrador)!!";
            AlertDialog.Builder exitApp = new AlertDialog.Builder(this);
            exitApp.setMessage(datoAlerta);
            exitApp.setPositiveButton("OK",null);
            exitApp.create();
            exitApp.show();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_survey, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.survey_del:
                Log.i(TAG,"Borrando");
                return true;
            case R.id.survey_draft:
                Log.i(TAG," Drafts ");
                saveDraft(sborrador);
                endView(1);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "Sleep");
        sborrador[0] = String.valueOf(id_Encuestado);
        sborrador[1] = String.valueOf(id_encuesta);
        sborrador[2] = String.valueOf(indexRespID);
        sborrador[3] = "1";
        sborrador[4] = idEncuestador;
//        try {
//            rcCom.open();
//            if(rcCom.Checkdraft(sborrador)) { //
//                rcCom.draft(sborrador);
//                Toast.makeText(getApplicationContext(), "Se guardo en Borradores", Toast.LENGTH_SHORT).show();
//            }else {
//                rcCom.updateDraft(sborrador);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        rcCom.close();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Resume");
    }

}
