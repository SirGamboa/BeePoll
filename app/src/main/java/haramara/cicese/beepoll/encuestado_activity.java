package haramara.cicese.beepoll;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import haramara.cicese.beepoll.db.dbEncuestado;
import haramara.cicese.beepoll.db.rcEncuestado;

/**
 * Created by diseno on 9/21/15. for BeePoll
 */
public class encuestado_activity extends AppCompatActivity{
    private dbEncuestado database;
    private rcEncuestado rcEnc;
    private LinearLayout ll;
    private int dUser = 0;
    private int s=-1;
    private final String TAG = "AddENCUESTADO";
    private Toolbar toolbar;
    ActionBar ab;

    private FloatingActionButton next;
    private Button btDate;
    private TextView tvApellido;
    private TextView tvNombres;
    private TextView tvedoCivil;
    private TextView rgLabel;
    private EditText etApellidos_text;
    private EditText etnombres_text;
    private EditText etedoCivilText;
    private RadioButton rbmasc;
    private RadioButton rbfem;
    private String nombres="NN";
    private String apellidos="LN LN";
    private String fnac="00002015";
    private String edocivil="?";
    private final String sexo= "?";
    private String clave;
    private int flagCheck = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //----- variables y declaraciones
        setContentView(R.layout.encuestado_layout);
        ll = (LinearLayout) findViewById(R.id.create_user_content);
        setToolbar();
        Intent i = getIntent();
        dUser = i.getExtras().getInt("edo");
        btDate = (Button) findViewById(R.id.btDate);
        tvApellido = (TextView) findViewById(R.id.apellidos);
        tvNombres = (TextView) findViewById(R.id.nombres);
//        TextView tvFechaLabel = (TextView) findViewById(R.id.fechaLabel);
        tvedoCivil = (TextView) findViewById(R.id.edoCivil);
        //--- EditText
        etApellidos_text = (EditText) findViewById(R.id.apellidos_text);
        etnombres_text = (EditText) findViewById(R.id.nombres_text);
        etedoCivilText = (EditText) findViewById(R.id.edoCivilText);
        //--- RGs
        rgLabel = (TextView) findViewById(R.id.rg_label);
        rbmasc = (RadioButton) findViewById(R.id.masc);
        rbfem = (RadioButton) findViewById(R.id.fem);
        ab.setTitle("Nuevo Encuestado");
        rcEnc = new rcEncuestado(getApplicationContext());
        try {
            rcEnc.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /*
        Dependiendo de la encuesta, se pueden pedir los datos del encuestado, o dejarlo anónimo.
        El método ShowUserInputs, permite agregar en la vista de AgregarEncuestado los datos
        que se soliciten, tales como Nombre, Edad, sexo, fecha de nacimiento, estado civil.
         */
        showUserInputs(dUser);
        btDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fdatePicker();

            }
        });
        next = (FloatingActionButton) findViewById(R.id.btFAB);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Time tm = new Time();
                tm.setToNow();
                tm.format("HHmmss");
                // String shhmmss
                fnac  = tm.toString().substring(9,15);
                // apellidos = "AP PM"; nombres = "NM"; edocivil = "EC";
                RadioGroup rg = (RadioGroup) findViewById(R.id.rg_container);
                int id_checked;// = rg.getCheckedRadioButtonId();
                if(etApellidos_text.getVisibility() == View.VISIBLE){
                    flagCheck ++;
                    apellidos = etApellidos_text.getText().toString();
                    if(!apellidos.isEmpty()) flagCheck --;
                }
                if(etnombres_text.getVisibility()==View.VISIBLE){
                    flagCheck ++;
                    nombres = etnombres_text.getText().toString();
                    if(!nombres.isEmpty() ) flagCheck --;
                }
                if(etedoCivilText.getVisibility()==View.VISIBLE){
                    flagCheck ++;
                    edocivil = etedoCivilText.getText().toString();
                    if(!edocivil.isEmpty()) flagCheck --;
                }
                if(btDate.getVisibility()==View.VISIBLE){
                    flagCheck  ++;
                    fnac = btDate.getText().toString();
                    if(!fnac.isEmpty()) flagCheck --;
                }
                if(rgLabel.getVisibility()== View.VISIBLE){
                    flagCheck ++;
                    id_checked = rg.getCheckedRadioButtonId();
                    View sexo_op = findViewById(id_checked);
                    int selected_id = rg.indexOfChild(sexo_op);
//                    Button rb_checked = (Button) rg.getChildAt(selected_id);
                    if(id_checked > 0) {
                        s =selected_id;
                        flagCheck --;
                    }

                }

                if(flagCheck ==0){
                    String[] apps ;
                    apps = apellidos.split(" ");
                    int id = 1;
                    if (!rcEnc.checkEncuestado()) { // check if user created
                        id = rcEnc.getID();
                    }

                    if(dUser != 1) {
                        clave = rcEnc.GenClave(nombres, apellidos, fnac, s);
                    }
                    else {
                        String[] formato = new String[]{"MM-dd-HH:mm" };
                        SimpleDateFormat df = new SimpleDateFormat(String.valueOf(formato[0]), Locale.US);
                        clave = "an"+ df.format(new Date(0));
                        Log.i(TAG, clave);
                    }
                    int ind = apps.length;
                    ContentValues cv = new ContentValues();
                    cv.put(database.COLUMN_NAME_CLAVE, clave);
                    cv.put(database.COLUMN_NAME_NOMBRES, nombres);
                    cv.put(database.COLUMN_NAME_APELLIDO_PATERNO, apps[0]);
                    if(ind > 1)
                        cv.put(database.COLUMN_NAME_APELLIDO_MATERNO, apps[1]);
                    else cv.put(database.COLUMN_NAME_APELLIDO_MATERNO, "LN");
                    cv.put(database.COLUMN_NAME_FECHA_NACIMIENTO, fnac);
                    cv.put(database.COLUMN_NAME_SEXO, sexo);
                    rcEnc.addEncuestado(cv);

                    Intent i = new Intent();
                    i.putExtra("id_encuestado", id);
                    i.putExtra("clave", clave);
                    setResult(99, i); //
                    rcEnc.close();
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Falta datos por llenar", Toast.LENGTH_SHORT).show();
                    flagCheck = 0;
                }

            }


        });
    }
//-----

    /*
    método para cargar en la vista los widgets de texto, y radio buttons, dependiendo del tipo de información
    que se le solicita a cada encuesta creada.
    cargando elemento en el layout de Encuestas.
    Tipo encuesta: 111110 (Nombre, sexo, estado civil, apellido, fecha nacimiento)
    Tipo encuesta: 1 (anónimo)
     */
    private void showUserInputs(int dUser) {

        tvNombres.setVisibility(View.INVISIBLE);
        etnombres_text.setVisibility(View.INVISIBLE);
        rgLabel.setVisibility(View.INVISIBLE);
        rbmasc.setVisibility(View.VISIBLE);
        rbfem.setVisibility(View.VISIBLE);
        tvedoCivil.setVisibility(View.INVISIBLE);
        etedoCivilText.setVisibility(View.INVISIBLE);
        btDate.setVisibility(View.INVISIBLE);
        tvApellido.setVisibility(View.INVISIBLE);
        etApellidos_text.setVisibility(View.INVISIBLE);
        Time tm = new Time();
        tm.setToNow();
        tm.format("HHmmss");
//                String[] sTime = tm.toString().split("T",6);
        String shhmmss = tm.toString().substring(9,15);
        ll.removeAllViews();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
                ;

        String Text="Favor de ingresar los datos que se piden.";
        switch(dUser){
            case 1:
                Text = "Se generará un usuario Anónimo";
                int id = 1;
                if (!rcEnc.checkEncuestado()) { // check if user created
                    id = rcEnc.getID();
                }
                clave = "an"+ shhmmss;
                Log.i(TAG, clave);
                ContentValues cv = new ContentValues();
                cv.put(database.COLUMN_NAME_CLAVE, clave);
                cv.put(database.COLUMN_NAME_NOMBRES, "Anonimo");
                cv.put(database.COLUMN_NAME_APELLIDO_PATERNO, "");
                cv.put(database.COLUMN_NAME_APELLIDO_MATERNO, "");
                cv.put(database.COLUMN_NAME_FECHA_NACIMIENTO, "");
                cv.put(database.COLUMN_NAME_SEXO, "");
                rcEnc.addEncuestado(cv);

                Intent i = new Intent();
                i.putExtra("id_encuestado", id);
                i.putExtra("clave", clave);
                setResult(55, i);
                rcEnc.close();
                finish();
                break;
            case 10: //dob
                btDate = new Button(getApplicationContext());
                btDate.setId(R.id.btDate);
                btDate.setLayoutParams(params);
                btDate.setText("Fecha Nacimiento");
                btDate.setVisibility(View.VISIBLE);
                ll.addView(btDate);
                break;
            case 100: //apellido
                tvApellido = new TextView(getApplicationContext());
                tvApellido.setId(R.id.apellidos);
                tvApellido.setLayoutParams(params);
                tvApellido.setVisibility(View.VISIBLE);
                tvApellido.setText("Apellido:");
                tvApellido.setTextColor(getResources().getColor(R.color.black));
                etApellidos_text = new EditText(getApplicationContext());
                etApellidos_text.setId(R.id.apellidos_text);
                etApellidos_text.setVisibility(View.VISIBLE);
                etApellidos_text.setBackgroundColor(getResources().getColor(R.color.white));
                etApellidos_text.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvApellido);
                ll.addView(etApellidos_text);
                break;
            case 1000: //edo Civil
                tvedoCivil = new TextView(getApplicationContext());
                tvedoCivil.setId(R.id.edoCivil);
                tvedoCivil.setLayoutParams(params);
                tvedoCivil.setText("Estado Civil:");
                tvedoCivil.setTextColor(getResources().getColor(R.color.black));
                tvedoCivil.setVisibility(View.VISIBLE);
                etedoCivilText = new EditText(getApplicationContext());
                etedoCivilText.setId(R.id.edoCivilText);
                etedoCivilText.setLayoutParams(params);
                etedoCivilText.setBackgroundColor(getResources().getColor(R.color.white));
                etedoCivilText.setVisibility(View.VISIBLE);
                etedoCivilText.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvedoCivil);
                ll.addView(etedoCivilText);
                break;
            case 10000: //sexo
                //---- RG Label
                rgLabel = new TextView(getApplicationContext());
                rgLabel.setId(R.id.rg_label);
                rgLabel.setText("Sexo:");
                rgLabel.setVisibility(View.VISIBLE);
                rgLabel.setTextColor(getResources().getColor(R.color.black));
                ll.addView(rgLabel);
                rbmasc = new RadioButton(getApplicationContext());
                rbmasc.setId(R.id.masc);
                rbmasc.setText("Masculino");
                rbmasc.setTextColor(getResources().getColor(R.color.black));
                rbmasc.setVisibility(View.VISIBLE);
                rbfem = new RadioButton(getApplicationContext());
                rbfem.setId(R.id.fem);
                rbfem.setText("Femenino");
                rbfem.setVisibility(View.VISIBLE);
                rbfem.setTextColor(getResources().getColor(R.color.black));
                RadioGroup rgroup = new RadioGroup(getApplicationContext());
                rgroup.setId(R.id.rg_container);
                rgroup.addView(rbmasc);
                rgroup.addView(rbfem);
                ll.addView(rgroup);

                break;
            case 100000: //nombre
                tvNombres = new TextView(getApplicationContext());
                tvNombres.setId(R.id.nombres);
                tvNombres.setLayoutParams(params);
                tvNombres.setVisibility(View.VISIBLE);
                tvNombres.setText("Nombre(s):");
                tvNombres.setTextColor(getResources().getColor(R.color.black));
                etnombres_text = new EditText(getApplicationContext());
                etnombres_text.setId(R.id.nombres_text);
                etnombres_text.setVisibility(View.VISIBLE);
                etnombres_text.setBackgroundColor(getResources().getColor(R.color.white));
                etnombres_text.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvNombres);
                ll.addView(etnombres_text);
//                etnombres_text.setVisibility(View.VISIBLE);
                break;
            case 110: // Apellido, fecha Nac
                btDate = new Button(getApplicationContext());
                btDate.setId(R.id.btDate);
                btDate.setLayoutParams(params);
                btDate.setText("Fecha Nacimiento");
                btDate.setVisibility(View.VISIBLE);
                ll.addView(btDate);
                tvApellido = new TextView(getApplicationContext());
                tvApellido.setId(R.id.apellidos);
                tvApellido.setLayoutParams(params);
                tvApellido.setVisibility(View.VISIBLE);
                tvApellido.setTextColor(getResources().getColor(R.color.black));
                tvApellido.setText("Apellido:");
                etApellidos_text = new EditText(getApplicationContext());
                etApellidos_text.setId(R.id.apellidos_text);
                etApellidos_text.setVisibility(View.VISIBLE);
                etApellidos_text.setBackgroundColor(getResources().getColor(R.color.white));
                etApellidos_text.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvApellido);
                ll.addView(etApellidos_text);
                break;
            case 1010:// edo civil, fecha Nac
                btDate = new Button(getApplicationContext());
                btDate.setId(R.id.btDate);
                btDate.setLayoutParams(params);
                btDate.setText("Fecha Nacimiento");
                btDate.setVisibility(View.VISIBLE);
                ll.addView(btDate);
                tvedoCivil = new TextView(getApplicationContext());
                tvedoCivil.setId(R.id.edoCivil);
                tvedoCivil.setLayoutParams(params);
                tvedoCivil.setText("Estado Civil:");
                tvedoCivil.setVisibility(View.VISIBLE);
                tvedoCivil.setTextColor(getResources().getColor(R.color.black));
                etedoCivilText = new EditText(getApplicationContext());
                etedoCivilText.setId(R.id.edoCivilText);
                etedoCivilText.setLayoutParams(params);
                etedoCivilText.setBackgroundColor(getResources().getColor(R.color.white));
                etedoCivilText.setTextColor(getResources().getColor(R.color.black));
                etedoCivilText.setVisibility(View.VISIBLE);
                ll.addView(tvedoCivil);
                ll.addView(etedoCivilText);
                break;
            case 1100:// edo civil, apellido
                tvedoCivil = new TextView(getApplicationContext());
                tvedoCivil.setId(R.id.edoCivil);
                tvedoCivil.setLayoutParams(params);
                tvedoCivil.setText("Estado Civil:");
                tvedoCivil.setVisibility(View.VISIBLE);
                tvedoCivil.setTextColor(getResources().getColor(R.color.black));
                etedoCivilText = new EditText(getApplicationContext());
                etedoCivilText.setId(R.id.edoCivilText);
                etedoCivilText.setLayoutParams(params);
                etedoCivilText.setBackgroundColor(getResources().getColor(R.color.white));
                etedoCivilText.setTextColor(getResources().getColor(R.color.black));
                etedoCivilText.setVisibility(View.VISIBLE);
                ll.addView(tvedoCivil);
                ll.addView(etedoCivilText);
                tvApellido = new TextView(getApplicationContext());
                tvApellido.setId(R.id.apellidos);
                tvApellido.setLayoutParams(params);
                tvApellido.setVisibility(View.VISIBLE);
                tvApellido.setTextColor(getResources().getColor(R.color.black));
                tvApellido.setText("Apellido:");
                etApellidos_text = new EditText(getApplicationContext());
                etApellidos_text.setId(R.id.apellidos_text);
                etApellidos_text.setVisibility(View.VISIBLE);
                etApellidos_text.setBackgroundColor(getResources().getColor(R.color.white));
                etApellidos_text.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvApellido);
                ll.addView(etApellidos_text);
                break;
            case 1110: // edo civil, apellido, fecha Nac

                //----Apellidos
                tvApellido = new TextView(getApplicationContext());
                tvApellido.setId(R.id.apellidos);
                tvApellido.setLayoutParams(params);
                tvApellido.setVisibility(View.VISIBLE);
                tvApellido.setText("Apellido:");
                tvApellido.setTextColor(getResources().getColor(R.color.black));
                etApellidos_text = new EditText(getApplicationContext());
                etApellidos_text.setId(R.id.apellidos_text);
                etApellidos_text.setVisibility(View.VISIBLE);
                etApellidos_text.setBackgroundColor(getResources().getColor(R.color.white));
                etApellidos_text.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvApellido);
                ll.addView(etApellidos_text);

                //---- Edo Civil
                tvedoCivil = new TextView(getApplicationContext());
                tvedoCivil.setId(R.id.edoCivil);
                tvedoCivil.setLayoutParams(params);
                tvedoCivil.setText("Estado Civil:");
                tvedoCivil.setTextColor(getResources().getColor(R.color.black));
                tvedoCivil.setVisibility(View.VISIBLE);
                etedoCivilText = new EditText(getApplicationContext());
                etedoCivilText.setId(R.id.edoCivilText);
                etedoCivilText.setLayoutParams(params);
                etedoCivilText.setVisibility(View.VISIBLE);
                etedoCivilText.setBackgroundColor(getResources().getColor(R.color.white));
                etedoCivilText.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvedoCivil);
                ll.addView(etedoCivilText);
                //--- Fecha Nac
                btDate = new Button(getApplicationContext());
                btDate.setId(R.id.btDate);
                btDate.setLayoutParams(params);
                btDate.setText("Fecha Nacimiento");
                btDate.setVisibility(View.VISIBLE);
                ll.addView(btDate);
                break;
            case 10010: // sexo, fecha Nac

                //---- RG Label
                rgLabel = new TextView(getApplicationContext());
                rgLabel.setId(R.id.rg_label);
                rgLabel.setText("Sexo:");
                rgLabel.setVisibility(View.VISIBLE);
                rgLabel.setTextColor(getResources().getColor(R.color.black));
                ll.addView(rgLabel);
                rbmasc = new RadioButton(getApplicationContext());
                rbmasc.setId(R.id.masc);
                rbmasc.setText("Masculino");
                rbmasc.setTextColor(getResources().getColor(R.color.black));
                rbmasc.setVisibility(View.VISIBLE);
                rbfem = new RadioButton(getApplicationContext());
                rbfem.setId(R.id.fem);
                rbfem.setText("Femenino");
                rbfem.setVisibility(View.VISIBLE);
                rbfem.setTextColor(getResources().getColor(R.color.black));
                rgroup = new RadioGroup(getApplicationContext());
                rgroup.setId(R.id.rg_container);
                rgroup.addView(rbmasc);
                rgroup.addView(rbfem);
                ll.addView(rgroup);

                //--- Fecha Nac
                btDate = new Button(getApplicationContext());
                btDate.setId(R.id.btDate);
                btDate.setLayoutParams(params);
                btDate.setText("Fecha Nacimiento");
                btDate.setVisibility(View.VISIBLE);
                ll.addView(btDate);
                break;
            case 10100: //sexo, apellido

                //----Apellidos
                tvApellido = new TextView(getApplicationContext());
                tvApellido.setId(R.id.apellidos);
                tvApellido.setLayoutParams(params);
                tvApellido.setVisibility(View.VISIBLE);
                tvApellido.setText("Apellido:");
                tvApellido.setTextColor(getResources().getColor(R.color.black));
                etApellidos_text = new EditText(getApplicationContext());
                etApellidos_text.setId(R.id.apellidos_text);
                etApellidos_text.setVisibility(View.VISIBLE);
                etApellidos_text.setBackgroundColor(getResources().getColor(R.color.white));
                etApellidos_text.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvApellido);
                ll.addView(etApellidos_text);
                //---- RG Label
                rgLabel = new TextView(getApplicationContext());
                rgLabel.setId(R.id.rg_label);
                rgLabel.setText("Sexo:");
                rgLabel.setVisibility(View.VISIBLE);
                rgLabel.setTextColor(getResources().getColor(R.color.black));
                ll.addView(rgLabel);
                rbmasc = new RadioButton(getApplicationContext());
                rbmasc.setId(R.id.masc);
                rbmasc.setText("Masculino");
                rbmasc.setTextColor(getResources().getColor(R.color.black));
                rbmasc.setVisibility(View.VISIBLE);
                rbfem = new RadioButton(getApplicationContext());
                rbfem.setId(R.id.fem);
                rbfem.setText("Femenino");
                rbfem.setVisibility(View.VISIBLE);
                rbfem.setTextColor(getResources().getColor(R.color.black));
                rgroup = new RadioGroup(getApplicationContext());
                rgroup.setId(R.id.rg_container);
                rgroup.addView(rbmasc);
                rgroup.addView(rbfem);
                ll.addView(rgroup);

                break;
            case 10110: // fecha nac, apellido, sexo
                //----Apellidos
                tvApellido = new TextView(getApplicationContext());
                tvApellido.setId(R.id.apellidos);
                tvApellido.setLayoutParams(params);
                tvApellido.setVisibility(View.VISIBLE);
                tvApellido.setText("Apellido:");
                tvApellido.setTextColor(getResources().getColor(R.color.black));
                etApellidos_text = new EditText(getApplicationContext());
                etApellidos_text.setId(R.id.apellidos_text);
                etApellidos_text.setVisibility(View.VISIBLE);
                etApellidos_text.setBackgroundColor(getResources().getColor(R.color.white));
                etApellidos_text.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvApellido);
                ll.addView(etApellidos_text);
                //---- RG Label
                rgLabel = new TextView(getApplicationContext());
                rgLabel.setId(R.id.rg_label);
                rgLabel.setText("Sexo:");
                rgLabel.setVisibility(View.VISIBLE);
                rgLabel.setTextColor(getResources().getColor(R.color.black));
                ll.addView(rgLabel);
                rbmasc = new RadioButton(getApplicationContext());
                rbmasc.setId(R.id.masc);
                rbmasc.setText("Masculino");
                rbmasc.setTextColor(getResources().getColor(R.color.black));
                rbmasc.setVisibility(View.VISIBLE);
                rbfem = new RadioButton(getApplicationContext());
                rbfem.setId(R.id.fem);
                rbfem.setText("Femenino");
                rbfem.setVisibility(View.VISIBLE);
                rbfem.setTextColor(getResources().getColor(R.color.black));
                rgroup = new RadioGroup(getApplicationContext());
                rgroup.setId(R.id.rg_container);
                rgroup.addView(rbmasc);
                rgroup.addView(rbfem);
                ll.addView(rgroup);

                //--- Fecha Nac
                btDate = new Button(getApplicationContext());
                btDate.setId(R.id.btDate);
                btDate.setLayoutParams(params);
                btDate.setText("Fecha Nacimiento");
                btDate.setVisibility(View.VISIBLE);
                ll.addView(btDate);
                break;
            case 11000:// sexo, edo civil

                //---- RG Label
                rgLabel = new TextView(getApplicationContext());
                rgLabel.setId(R.id.rg_label);
                rgLabel.setText("Sexo:");
                rgLabel.setVisibility(View.VISIBLE);
                rgLabel.setTextColor(getResources().getColor(R.color.black));
                ll.addView(rgLabel);
                rbmasc = new RadioButton(getApplicationContext());
                rbmasc.setId(R.id.masc);
                rbmasc.setText("Masculino");
                rbmasc.setTextColor(getResources().getColor(R.color.black));
                rbmasc.setVisibility(View.VISIBLE);
                rbfem = new RadioButton(getApplicationContext());
                rbfem.setId(R.id.fem);
                rbfem.setText("Femenino");
                rbfem.setVisibility(View.VISIBLE);
                rbfem.setTextColor(getResources().getColor(R.color.black));
                rgroup = new RadioGroup(getApplicationContext());
                rgroup.setId(R.id.rg_container);
                rgroup.addView(rbmasc);
                rgroup.addView(rbfem);
                ll.addView(rgroup);
                //---- Edo Civil
                tvedoCivil = new TextView(getApplicationContext());
                tvedoCivil.setId(R.id.edoCivil);
                tvedoCivil.setLayoutParams(params);
                tvedoCivil.setText("Estado Civil:");
                tvedoCivil.setTextColor(getResources().getColor(R.color.black));
                tvedoCivil.setVisibility(View.VISIBLE);
                etedoCivilText = new EditText(getApplicationContext());
                etedoCivilText.setId(R.id.edoCivilText);
                etedoCivilText.setLayoutParams(params);
                etedoCivilText.setVisibility(View.VISIBLE);
                etedoCivilText.setBackgroundColor(getResources().getColor(R.color.white));
                etedoCivilText.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvedoCivil);
                ll.addView(etedoCivilText);

                break;
            case 11010:// sexo, edo civil, dob

                //---- RG Label
                rgLabel = new TextView(getApplicationContext());
                rgLabel.setId(R.id.rg_label);
                rgLabel.setText("Sexo:");
                rgLabel.setVisibility(View.VISIBLE);
                rgLabel.setTextColor(getResources().getColor(R.color.black));
                ll.addView(rgLabel);
                rbmasc = new RadioButton(getApplicationContext());
                rbmasc.setId(R.id.masc);
                rbmasc.setText("Masculino");
                rbmasc.setTextColor(getResources().getColor(R.color.black));
                rbmasc.setVisibility(View.VISIBLE);
                rbfem = new RadioButton(getApplicationContext());
                rbfem.setId(R.id.fem);
                rbfem.setText("Femenino");
                rbfem.setVisibility(View.VISIBLE);
                rbfem.setTextColor(getResources().getColor(R.color.black));
                rgroup = new RadioGroup(getApplicationContext());
                rgroup.setId(R.id.rg_container);
                rgroup.addView(rbmasc);
                rgroup.addView(rbfem);
                ll.addView(rgroup);
                //---- Edo Civil
                tvedoCivil = new TextView(getApplicationContext());
                tvedoCivil.setId(R.id.edoCivil);
                tvedoCivil.setLayoutParams(params);
                tvedoCivil.setText("Estado Civil:");
                tvedoCivil.setTextColor(getResources().getColor(R.color.black));
                tvedoCivil.setVisibility(View.VISIBLE);
                etedoCivilText = new EditText(getApplicationContext());
                etedoCivilText.setId(R.id.edoCivilText);
                etedoCivilText.setLayoutParams(params);
                etedoCivilText.setVisibility(View.VISIBLE);
                etedoCivilText.setBackgroundColor(getResources().getColor(R.color.white));
                etedoCivilText.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvedoCivil);
                ll.addView(etedoCivilText);
                //--- Fecha Nac
                btDate = new Button(getApplicationContext());
                btDate.setId(R.id.btDate);
                btDate.setLayoutParams(params);
                btDate.setText("Fecha Nacimiento");
                btDate.setVisibility(View.VISIBLE);
                ll.addView(btDate);
                break;
            case 11100:// sexo, edo civil, apellido
                rgLabel = new TextView(getApplicationContext());
                rgLabel.setId(R.id.rg_label);
                rgLabel.setText("Sexo:");
                rgLabel.setVisibility(View.VISIBLE);
                rgLabel.setTextColor(getResources().getColor(R.color.black));
                ll.addView(rgLabel);
                rbmasc = new RadioButton(getApplicationContext());
                rbmasc.setId(R.id.masc);
                rbmasc.setText("Masculino");
                rbmasc.setVisibility(View.VISIBLE);
                rbmasc.setTextColor(getResources().getColor(R.color.black));
                rbfem = new RadioButton(getApplicationContext());
                rbfem.setId(R.id.fem);
                rbfem.setText("Femenino");
                rbfem.setVisibility(View.VISIBLE);
                rbfem.setTextColor(getResources().getColor(R.color.black));
                ll.addView(rbmasc);
                ll.addView(rbfem);
                tvedoCivil = new TextView(getApplicationContext());
                tvedoCivil.setId(R.id.edoCivil);
                tvedoCivil.setLayoutParams(params);
                tvedoCivil.setTextColor(getResources().getColor(R.color.black));
                tvedoCivil.setText("Estado Civil:");
                tvedoCivil.setVisibility(View.VISIBLE);
                etedoCivilText = new EditText(getApplicationContext());
                etedoCivilText.setId(R.id.edoCivilText);
                etedoCivilText.setLayoutParams(params);
                etedoCivilText.setBackgroundColor(getResources().getColor(R.color.white));
                etedoCivilText.setTextColor(getResources().getColor(R.color.black));
                etedoCivilText.setVisibility(View.VISIBLE);
                ll.addView(tvedoCivil);
                ll.addView(etedoCivilText);
                tvApellido = new TextView(getApplicationContext());
                tvApellido.setId(R.id.apellidos);
                tvApellido.setLayoutParams(params);
                tvApellido.setVisibility(View.VISIBLE);
                tvApellido.setTextColor(getResources().getColor(R.color.black));
                tvApellido.setText("Apellido:");
                etApellidos_text = new EditText(getApplicationContext());
                etApellidos_text.setId(R.id.apellidos_text);
                etApellidos_text.setVisibility(View.VISIBLE);
                etApellidos_text.setBackgroundColor(getResources().getColor(R.color.white));
                etApellidos_text.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvApellido);
                ll.addView(etApellidos_text);
                break;
            case 11110://sexo, edo civil, apellido, dob
                rgLabel = new TextView(getApplicationContext());
                rgLabel.setId(R.id.rg_label);
                rgLabel.setText("Sexo:");
                rgLabel.setVisibility(View.VISIBLE);
                rgLabel.setTextColor(getResources().getColor(R.color.black));
                ll.addView(rgLabel);
                rbmasc = new RadioButton(getApplicationContext());
                rbmasc.setId(R.id.masc);
                rbmasc.setText("Masculino");
                rbmasc.setTextColor(getResources().getColor(R.color.black));
                rbmasc.setVisibility(View.VISIBLE);
                rbfem = new RadioButton(getApplicationContext());
                rbfem.setId(R.id.fem);
                rbfem.setText("Femenino");
                rbfem.setVisibility(View.VISIBLE);
                rbfem.setTextColor(getResources().getColor(R.color.black));
                rgroup = new RadioGroup(getApplicationContext());
                rgroup.setId(R.id.rg_container);
                rgroup.addView(rbmasc);
                rgroup.addView(rbfem);
                ll.addView(rgroup);
                tvedoCivil = new TextView(getApplicationContext());
                tvedoCivil.setId(R.id.edoCivil);
                tvedoCivil.setLayoutParams(params);
                tvedoCivil.setText("Estado Civil:");
                tvedoCivil.setTextColor(getResources().getColor(R.color.black));
                tvedoCivil.setVisibility(View.VISIBLE);
                etedoCivilText = new EditText(getApplicationContext());
                etedoCivilText.setId(R.id.edoCivilText);
                etedoCivilText.setLayoutParams(params);
                etedoCivilText.setBackgroundColor(getResources().getColor(R.color.white));
                etedoCivilText.setTextColor(getResources().getColor(R.color.black));
                etedoCivilText.setVisibility(View.VISIBLE);
                ll.addView(tvedoCivil);
                ll.addView(etedoCivilText);
                tvApellido = new TextView(getApplicationContext());
                tvApellido.setId(R.id.apellidos);
                tvApellido.setLayoutParams(params);
                tvApellido.setTextColor(getResources().getColor(R.color.black));
                tvApellido.setVisibility(View.VISIBLE);
                tvApellido.setText("Apellido:");
                etApellidos_text = new EditText(getApplicationContext());
                etApellidos_text.setId(R.id.apellidos_text);
                etApellidos_text.setVisibility(View.VISIBLE);
                etApellidos_text.setBackgroundColor(getResources().getColor(R.color.white));
                etApellidos_text.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvApellido);
                ll.addView(etApellidos_text);
                btDate = new Button(getApplicationContext());
                btDate.setId(R.id.btDate);
                btDate.setLayoutParams(params);
                btDate.setText("Fecha Nacimiento");
                btDate.setVisibility(View.VISIBLE);
                ll.addView(btDate);
                break;
            case 100010://nombre, dob
                tvNombres = new TextView(getApplicationContext());
                tvNombres.setId(R.id.nombres);
                tvNombres.setLayoutParams(params);
                tvNombres.setTextColor(getResources().getColor(R.color.black));
                tvNombres.setVisibility(View.VISIBLE);
                tvNombres.setText("Nombre(s):");
                etnombres_text = new EditText(getApplicationContext());
                etnombres_text.setId(R.id.nombres_text);
                etnombres_text.setVisibility(View.VISIBLE);
                etnombres_text.setBackgroundColor(getResources().getColor(R.color.white));
                etnombres_text.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvNombres);
                ll.addView(etnombres_text);
                btDate = new Button(getApplicationContext());
                btDate.setId(R.id.btDate);
                btDate.setLayoutParams(params);
                btDate.setText("Fecha Nacimiento");
                btDate.setVisibility(View.VISIBLE);
                ll.addView(btDate);
                break;
            case 100100: //nombre, ape
                tvApellido = new TextView(getApplicationContext());
                tvApellido.setId(R.id.apellidos);
                tvApellido.setLayoutParams(params);
                tvApellido.setTextColor(getResources().getColor(R.color.black));
                tvApellido.setVisibility(View.VISIBLE);
                tvApellido.setText("Apellido:");
                etApellidos_text = new EditText(getApplicationContext());
                etApellidos_text.setId(R.id.apellidos_text);
                etApellidos_text.setVisibility(View.VISIBLE);
                etApellidos_text.setBackgroundColor(getResources().getColor(R.color.white));
                etApellidos_text.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvApellido);
                ll.addView(etApellidos_text);
                tvNombres = new TextView(getApplicationContext());
                tvNombres.setId(R.id.nombres);
                tvNombres.setLayoutParams(params);
                tvNombres.setTextColor(getResources().getColor(R.color.black));
                tvNombres.setVisibility(View.VISIBLE);
                tvNombres.setText("Nombre(s):");
                etnombres_text = new EditText(getApplicationContext());
                etnombres_text.setId(R.id.nombres_text);
                etnombres_text.setVisibility(View.VISIBLE);
                etnombres_text.setBackgroundColor(getResources().getColor(R.color.white));
                etnombres_text.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvNombres);
                ll.addView(etnombres_text);
                break;
            case 100110: //nombre, apellido, dob
                tvApellido = new TextView(getApplicationContext());
                tvApellido.setId(R.id.apellidos);
                tvApellido.setLayoutParams(params);
                tvApellido.setVisibility(View.VISIBLE);
                tvApellido.setTextColor(getResources().getColor(R.color.black));
                tvApellido.setText("Apellido:");
                etApellidos_text = new EditText(getApplicationContext());
                etApellidos_text.setId(R.id.apellidos_text);
                etApellidos_text.setVisibility(View.VISIBLE);
                etApellidos_text.setBackgroundColor(getResources().getColor(R.color.white));
                etApellidos_text.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvApellido);
                ll.addView(etApellidos_text);
                tvNombres = new TextView(getApplicationContext());
                tvNombres.setId(R.id.nombres);
                tvNombres.setLayoutParams(params);
                tvNombres.setTextColor(getResources().getColor(R.color.black));
                tvNombres.setVisibility(View.VISIBLE);
                tvNombres.setText("Nombre(s):");
                etnombres_text = new EditText(getApplicationContext());
                etnombres_text.setId(R.id.nombres_text);
                etnombres_text.setBackgroundColor(getResources().getColor(R.color.white));
                etnombres_text.setTextColor(getResources().getColor(R.color.black));
                etnombres_text.setVisibility(View.VISIBLE);
                ll.addView(tvNombres);
                ll.addView(etnombres_text);
                btDate = new Button(getApplicationContext());
                btDate.setId(R.id.btDate);
                btDate.setLayoutParams(params);
                btDate.setText("Fecha Nacimiento");
                btDate.setVisibility(View.VISIBLE);
                ll.addView(btDate);
                break;
            case 101000://nombre edo civil
                tvNombres = new TextView(getApplicationContext());
                tvNombres.setId(R.id.nombres);
                tvNombres.setLayoutParams(params);
                tvNombres.setVisibility(View.VISIBLE);
                tvNombres.setText("Nombre(s):");
                tvNombres.setTextColor(getResources().getColor(R.color.black));
                etnombres_text = new EditText(getApplicationContext());
                etnombres_text.setId(R.id.nombres_text);
                etnombres_text.setVisibility(View.VISIBLE);
                etnombres_text.setBackgroundColor(getResources().getColor(R.color.white));
                etnombres_text.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvNombres);
                ll.addView(etnombres_text);
                tvedoCivil = new TextView(getApplicationContext());
                tvedoCivil.setId(R.id.edoCivil);
                tvedoCivil.setLayoutParams(params);
                tvedoCivil.setText("Estado Civil:");
                tvedoCivil.setTextColor(getResources().getColor(R.color.black));
                tvedoCivil.setVisibility(View.VISIBLE);
                etedoCivilText = new EditText(getApplicationContext());
                etedoCivilText.setId(R.id.edoCivilText);
                etedoCivilText.setLayoutParams(params);
                etedoCivilText.setBackgroundColor(getResources().getColor(R.color.white));
                etedoCivilText.setTextColor(getResources().getColor(R.color.black));
                etedoCivilText.setVisibility(View.VISIBLE);
                ll.addView(tvedoCivil);
                ll.addView(etedoCivilText);
                break;
            case 101010: //nombre, edo civil, dob
                tvNombres = new TextView(getApplicationContext());
                tvNombres.setId(R.id.nombres);
                tvNombres.setLayoutParams(params);
                tvNombres.setTextColor(getResources().getColor(R.color.black));
                tvNombres.setVisibility(View.VISIBLE);
                tvNombres.setText("Nombre(s):");
                etnombres_text = new EditText(getApplicationContext());
                etnombres_text.setId(R.id.nombres_text);
                etnombres_text.setVisibility(View.VISIBLE);
                etnombres_text.setBackgroundColor(getResources().getColor(R.color.white));
                etnombres_text.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvNombres);
                ll.addView(etnombres_text);
                tvedoCivil = new TextView(getApplicationContext());
                tvedoCivil.setId(R.id.edoCivil);
                tvedoCivil.setLayoutParams(params);
                tvedoCivil.setText("Estado Civil:");
                tvedoCivil.setTextColor(getResources().getColor(R.color.black));
                tvedoCivil.setVisibility(View.VISIBLE);
                etedoCivilText = new EditText(getApplicationContext());
                etedoCivilText.setId(R.id.edoCivilText);
                etedoCivilText.setLayoutParams(params);
                etedoCivilText.setVisibility(View.VISIBLE);
                etedoCivilText.setBackgroundColor(getResources().getColor(R.color.white));
                etedoCivilText.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvedoCivil);
                ll.addView(etedoCivilText);
                btDate = new Button(getApplicationContext());
                btDate.setId(R.id.btDate);
                btDate.setLayoutParams(params);
                btDate.setText("Fecha Nacimiento");
                btDate.setVisibility(View.VISIBLE);
                ll.addView(btDate);
                break;
            case 101100://Nombre apellido edoCivil
                tvNombres = new TextView(getApplicationContext());
                tvNombres.setId(R.id.nombres);
                tvNombres.setLayoutParams(params);
                tvNombres.setVisibility(View.VISIBLE);
                tvNombres.setTextColor(getResources().getColor(R.color.black));
                tvNombres.setText("Nombre(s):");
                etnombres_text = new EditText(getApplicationContext());
                etnombres_text.setId(R.id.nombres_text);
                etnombres_text.setVisibility(View.VISIBLE);
                etnombres_text.setBackgroundColor(getResources().getColor(R.color.white));
                etnombres_text.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvNombres);
                ll.addView(etnombres_text);
                tvApellido = new TextView(getApplicationContext());
                tvApellido.setId(R.id.apellidos);
                tvApellido.setLayoutParams(params);
                tvApellido.setVisibility(View.VISIBLE);
                tvApellido.setTextColor(getResources().getColor(R.color.black));
                tvApellido.setText("Apellido:");
                etApellidos_text = new EditText(getApplicationContext());
                etApellidos_text.setId(R.id.apellidos_text);
                etApellidos_text.setVisibility(View.VISIBLE);
                etApellidos_text.setBackgroundColor(getResources().getColor(R.color.white));
                etApellidos_text.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvApellido);
                ll.addView(etApellidos_text);
                tvedoCivil = new TextView(getApplicationContext());
                tvedoCivil.setId(R.id.edoCivil);
                tvedoCivil.setLayoutParams(params);
                tvedoCivil.setText("Estado Civil:");
                tvedoCivil.setTextColor(getResources().getColor(R.color.black));
                tvedoCivil.setVisibility(View.VISIBLE);
                etedoCivilText = new EditText(getApplicationContext());
                etedoCivilText.setId(R.id.edoCivilText);
                etedoCivilText.setLayoutParams(params);
                etedoCivilText.setBackgroundColor(getResources().getColor(R.color.white));
                etedoCivilText.setTextColor(getResources().getColor(R.color.black));
                etedoCivilText.setVisibility(View.VISIBLE);
                ll.addView(tvedoCivil);
                ll.addView(etedoCivilText);
                break;
            case 101110://nombre, edo civil, apellido, dob
                tvNombres = new TextView(getApplicationContext());
                tvNombres.setId(R.id.nombres);
                tvNombres.setLayoutParams(params);
                tvNombres.setVisibility(View.VISIBLE);
                tvNombres.setText("Nombre(s):");
                tvNombres.setTextColor(getResources().getColor(R.color.black));
                etnombres_text = new EditText(getApplicationContext());
                etnombres_text.setId(R.id.nombres_text);
                etnombres_text.setVisibility(View.VISIBLE);
                etnombres_text.setBackgroundColor(getResources().getColor(R.color.white));
                etnombres_text.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvNombres);
                ll.addView(etnombres_text);
                tvApellido = new TextView(getApplicationContext());
                tvApellido.setId(R.id.apellidos);
                tvApellido.setLayoutParams(params);
                tvApellido.setTextColor(getResources().getColor(R.color.black));
                tvApellido.setVisibility(View.VISIBLE);
                tvApellido.setText("Apellido:");
                etApellidos_text = new EditText(getApplicationContext());
                etApellidos_text.setId(R.id.apellidos_text);
                etApellidos_text.setBackgroundColor(getResources().getColor(R.color.white));
                etApellidos_text.setTextColor(getResources().getColor(R.color.black));
                etApellidos_text.setVisibility(View.VISIBLE);
                ll.addView(tvApellido);
                ll.addView(etApellidos_text);
                tvedoCivil = new TextView(getApplicationContext());
                tvedoCivil.setId(R.id.edoCivil);
                tvedoCivil.setLayoutParams(params);
                tvedoCivil.setText("Estado Civil:");
                tvedoCivil.setTextColor(getResources().getColor(R.color.black));
                tvedoCivil.setVisibility(View.VISIBLE);
                etedoCivilText = new EditText(getApplicationContext());
                etedoCivilText.setId(R.id.edoCivilText);
                etedoCivilText.setLayoutParams(params);
                etedoCivilText.setVisibility(View.VISIBLE);
                etedoCivilText.setBackgroundColor(getResources().getColor(R.color.white));
                etedoCivilText.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvedoCivil);
                ll.addView(etedoCivilText);
                btDate = new Button(getApplicationContext());
                btDate.setId(R.id.btDate);
                btDate.setLayoutParams(params);
                btDate.setText("Fecha Nacimiento");
                btDate.setVisibility(View.VISIBLE);
                ll.addView(btDate);
                break;
            case 110000: //nombre, sexo
                tvNombres = new TextView(getApplicationContext());
                tvNombres.setId(R.id.nombres);
                tvNombres.setLayoutParams(params);
                tvNombres.setVisibility(View.VISIBLE);
                tvNombres.setText("Nombre(s):");
                tvNombres.setTextColor(getResources().getColor(R.color.black));
                etnombres_text = new EditText(getApplicationContext());
                etnombres_text.setId(R.id.nombres_text);
                etnombres_text.setVisibility(View.VISIBLE);
                etnombres_text.setBackgroundColor(getResources().getColor(R.color.white));
                etnombres_text.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvNombres);
                ll.addView(etnombres_text);
                rgLabel = new TextView(getApplicationContext());
                rgLabel.setId(R.id.rg_label);
                rgLabel.setText("Sexo:");
                rgLabel.setVisibility(View.VISIBLE);
                rgLabel.setTextColor(getResources().getColor(R.color.black));
                ll.addView(rgLabel);
                rbmasc = new RadioButton(getApplicationContext());
                rbmasc.setId(R.id.masc);
                rbmasc.setText("Masculino");
                rbmasc.setTextColor(getResources().getColor(R.color.black));
                rbmasc.setVisibility(View.VISIBLE);
                rbfem = new RadioButton(getApplicationContext());
                rbfem.setId(R.id.fem);
                rbfem.setText("Femenino");
                rbfem.setVisibility(View.VISIBLE);
                rbfem.setTextColor(getResources().getColor(R.color.black));
                rgroup = new RadioGroup(getApplicationContext());
                rgroup.setId(R.id.rg_container);
                rgroup.addView(rbmasc);
                rgroup.addView(rbfem);
                ll.addView(rgroup);
                break;
            case 110010://nombre, sexo, dob
                tvNombres = new TextView(getApplicationContext());
                tvNombres.setId(R.id.nombres);
                tvNombres.setLayoutParams(params);
                tvNombres.setVisibility(View.VISIBLE);
                tvNombres.setText("Nombre(s):");
                tvNombres.setTextColor(getResources().getColor(R.color.black));
                etnombres_text = new EditText(getApplicationContext());
                etnombres_text.setId(R.id.nombres_text);
                etnombres_text.setVisibility(View.VISIBLE);
                etnombres_text.setBackgroundColor(getResources().getColor(R.color.white));
                etnombres_text.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvNombres);
                ll.addView(etnombres_text);
                rgLabel = new TextView(getApplicationContext());
                rgLabel.setId(R.id.rg_label);
                rgLabel.setText("Sexo:");
                rgLabel.setVisibility(View.VISIBLE);
                rgLabel.setTextColor(getResources().getColor(R.color.black));
                ll.addView(rgLabel);
                rbmasc = new RadioButton(getApplicationContext());
                rbmasc.setId(R.id.masc);
                rbmasc.setText("Masculino");
                rbmasc.setTextColor(getResources().getColor(R.color.black));
                rbmasc.setVisibility(View.VISIBLE);
                rbfem = new RadioButton(getApplicationContext());
                rbfem.setId(R.id.fem);
                rbfem.setText("Femenino");
                rbfem.setVisibility(View.VISIBLE);
                rbfem.setTextColor(getResources().getColor(R.color.black));
                rgroup = new RadioGroup(getApplicationContext());
                rgroup.setId(R.id.rg_container);
                rgroup.addView(rbmasc);
                rgroup.addView(rbfem);
                ll.addView(rgroup);
                btDate = new Button(getApplicationContext());
                btDate.setId(R.id.btDate);
                btDate.setLayoutParams(params);
                btDate.setText("Fecha Nacimiento");
                btDate.setVisibility(View.VISIBLE);
                ll.addView(btDate);
                break;
            case 110100: //nombre, sexo, apellido
                tvNombres = new TextView(getApplicationContext());
                tvNombres.setId(R.id.nombres);
                tvNombres.setLayoutParams(params);
                tvNombres.setVisibility(View.VISIBLE);
                tvNombres.setText("Nombre(s):");
                tvNombres.setTextColor(getResources().getColor(R.color.black));
                etnombres_text = new EditText(getApplicationContext());
                etnombres_text.setId(R.id.nombres_text);
                etnombres_text.setVisibility(View.VISIBLE);
                etnombres_text.setBackgroundColor(getResources().getColor(R.color.white));
                etnombres_text.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvNombres);
                ll.addView(etnombres_text);
                tvApellido = new TextView(getApplicationContext());
                tvApellido.setId(R.id.apellidos);
                tvApellido.setLayoutParams(params);
                tvApellido.setVisibility(View.VISIBLE);
                tvApellido.setText("Apellido:");
                tvApellido.setTextColor(getResources().getColor(R.color.black));
                etApellidos_text = new EditText(getApplicationContext());
                etApellidos_text.setId(R.id.apellidos_text);
                etApellidos_text.setVisibility(View.VISIBLE);
                etApellidos_text.setBackgroundColor(getResources().getColor(R.color.white));
                etApellidos_text.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvApellido);
                ll.addView(etApellidos_text);
                rgLabel = new TextView(getApplicationContext());
                rgLabel.setId(R.id.rg_label);
                rgLabel.setText("Sexo:");
                rgLabel.setVisibility(View.VISIBLE);
                rgLabel.setTextColor(getResources().getColor(R.color.black));
                ll.addView(rgLabel);
                rbmasc = new RadioButton(getApplicationContext());
                rbmasc.setId(R.id.masc);
                rbmasc.setText("Masculino");
                rbmasc.setTextColor(getResources().getColor(R.color.black));
                rbmasc.setVisibility(View.VISIBLE);
                rbfem = new RadioButton(getApplicationContext());
                rbfem.setId(R.id.fem);
                rbfem.setText("Femenino");
                rbfem.setVisibility(View.VISIBLE);
                rbfem.setTextColor(getResources().getColor(R.color.black));
                rgroup = new RadioGroup(getApplicationContext());
                rgroup.setId(R.id.rg_container);
                rgroup.addView(rbmasc);
                rgroup.addView(rbfem);
                ll.addView(rgroup);
            case 110110: //nombre, sexo, apellido, dob
                tvNombres = new TextView(getApplicationContext());
                tvNombres.setId(R.id.nombres);
                tvNombres.setLayoutParams(params);
                tvNombres.setVisibility(View.VISIBLE);
                tvNombres.setText("Nombre(s):");
                tvNombres.setTextColor(getResources().getColor(R.color.black));
                etnombres_text = new EditText(getApplicationContext());
                etnombres_text.setId(R.id.nombres_text);
                etnombres_text.setVisibility(View.VISIBLE);
                etnombres_text.setBackgroundColor(getResources().getColor(R.color.white));
                etnombres_text.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvNombres);
                ll.addView(etnombres_text);
                tvApellido = new TextView(getApplicationContext());
                tvApellido.setId(R.id.apellidos);
                tvApellido.setLayoutParams(params);
                tvApellido.setVisibility(View.VISIBLE);
                tvApellido.setTextColor(getResources().getColor(R.color.black));
                tvApellido.setText("Apellido:");
                etApellidos_text = new EditText(getApplicationContext());
                etApellidos_text.setId(R.id.apellidos_text);
                etApellidos_text.setVisibility(View.VISIBLE);
                etApellidos_text.setBackgroundColor(getResources().getColor(R.color.white));
                etApellidos_text.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvApellido);
                ll.addView(etApellidos_text);
                rgLabel = new TextView(getApplicationContext());
                rgLabel.setId(R.id.rg_label);
                rgLabel.setText("Sexo:");
                rgLabel.setVisibility(View.VISIBLE);
                rgLabel.setTextColor(getResources().getColor(R.color.black));
                ll.addView(rgLabel);
                rbmasc = new RadioButton(getApplicationContext());
                rbmasc.setId(R.id.masc);
                rbmasc.setText("Masculino");
                rbmasc.setTextColor(getResources().getColor(R.color.black));
                rbmasc.setVisibility(View.VISIBLE);
                rbfem = new RadioButton(getApplicationContext());
                rbfem.setId(R.id.fem);
                rbfem.setText("Femenino");
                rbfem.setVisibility(View.VISIBLE);
                rbfem.setTextColor(getResources().getColor(R.color.black));
                rgroup = new RadioGroup(getApplicationContext());
                rgroup.setId(R.id.rg_container);
                rgroup.addView(rbmasc);
                rgroup.addView(rbfem);
                ll.addView(rgroup);
                btDate = new Button(getApplicationContext());
                btDate.setId(R.id.btDate);
                btDate.setLayoutParams(params);
                btDate.setText("Fecha Nacimiento");
                btDate.setVisibility(View.VISIBLE);
                ll.addView(btDate);
                break;
            case 111000: //nombre, sexo, edo civil
                //---- Nombre
                tvNombres = new TextView(getApplicationContext());
                tvNombres.setId(R.id.nombres);
                tvNombres.setLayoutParams(params);
                tvNombres.setVisibility(View.VISIBLE);
                tvNombres.setText("Nombre(s):");
                tvNombres.setTextColor(getResources().getColor(R.color.black));
                etnombres_text = new EditText(getApplicationContext());
                etnombres_text.setId(R.id.nombres_text);
                etnombres_text.setVisibility(View.VISIBLE);
                etnombres_text.setBackgroundColor(getResources().getColor(R.color.white));
                etnombres_text.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvNombres);
                ll.addView(etnombres_text);

                //---- RG Label
                rgLabel = new TextView(getApplicationContext());
                rgLabel.setId(R.id.rg_label);
                rgLabel.setText("Sexo:");
                rgLabel.setVisibility(View.VISIBLE);
                rgLabel.setTextColor(getResources().getColor(R.color.black));
                ll.addView(rgLabel);
                rbmasc = new RadioButton(getApplicationContext());
                rbmasc.setId(R.id.masc);
                rbmasc.setText("Masculino");
                rbmasc.setTextColor(getResources().getColor(R.color.black));
                rbmasc.setVisibility(View.VISIBLE);
                rbfem = new RadioButton(getApplicationContext());
                rbfem.setId(R.id.fem);
                rbfem.setText("Femenino");
                rbfem.setVisibility(View.VISIBLE);
                rbfem.setTextColor(getResources().getColor(R.color.black));
                rgroup = new RadioGroup(getApplicationContext());
                rgroup.setId(R.id.rg_container);
                rgroup.addView(rbmasc);
                rgroup.addView(rbfem);
                ll.addView(rgroup);
                //---- Edo Civil
                tvedoCivil = new TextView(getApplicationContext());
                tvedoCivil.setId(R.id.edoCivil);
                tvedoCivil.setLayoutParams(params);
                tvedoCivil.setText("Estado Civil:");
                tvedoCivil.setTextColor(getResources().getColor(R.color.black));
                tvedoCivil.setVisibility(View.VISIBLE);
                etedoCivilText = new EditText(getApplicationContext());
                etedoCivilText.setId(R.id.edoCivilText);
                etedoCivilText.setLayoutParams(params);
                etedoCivilText.setVisibility(View.VISIBLE);
                etedoCivilText.setBackgroundColor(getResources().getColor(R.color.white));
                etedoCivilText.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvedoCivil);
                ll.addView(etedoCivilText);

                break;
            case 111010://nombre, sexo, edo civil, dob
                //---- Nombre
                tvNombres = new TextView(getApplicationContext());
                tvNombres.setId(R.id.nombres);
                tvNombres.setLayoutParams(params);
                tvNombres.setVisibility(View.VISIBLE);
                tvNombres.setText("Nombre(s):");
                tvNombres.setTextColor(getResources().getColor(R.color.black));
                etnombres_text = new EditText(getApplicationContext());
                etnombres_text.setId(R.id.nombres_text);
                etnombres_text.setVisibility(View.VISIBLE);
                etnombres_text.setBackgroundColor(getResources().getColor(R.color.white));
                etnombres_text.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvNombres);
                ll.addView(etnombres_text);
                //---- RG Label
                rgLabel = new TextView(getApplicationContext());
                rgLabel.setId(R.id.rg_label);
                rgLabel.setText("Sexo:");
                rgLabel.setVisibility(View.VISIBLE);
                rgLabel.setTextColor(getResources().getColor(R.color.black));
                ll.addView(rgLabel);
                rbmasc = new RadioButton(getApplicationContext());
                rbmasc.setId(R.id.masc);
                rbmasc.setText("Masculino");
                rbmasc.setTextColor(getResources().getColor(R.color.black));
                rbmasc.setVisibility(View.VISIBLE);
                rbfem = new RadioButton(getApplicationContext());
                rbfem.setId(R.id.fem);
                rbfem.setText("Femenino");
                rbfem.setVisibility(View.VISIBLE);
                rbfem.setTextColor(getResources().getColor(R.color.black));
                rgroup = new RadioGroup(getApplicationContext());
                rgroup.setId(R.id.rg_container);
                rgroup.addView(rbmasc);
                rgroup.addView(rbfem);
                ll.addView(rgroup);
                //---- Edo Civil
                tvedoCivil = new TextView(getApplicationContext());
                tvedoCivil.setId(R.id.edoCivil);
                tvedoCivil.setLayoutParams(params);
                tvedoCivil.setText("Estado Civil:");
                tvedoCivil.setTextColor(getResources().getColor(R.color.black));
                tvedoCivil.setVisibility(View.VISIBLE);
                etedoCivilText = new EditText(getApplicationContext());
                etedoCivilText.setId(R.id.edoCivilText);
                etedoCivilText.setLayoutParams(params);
                etedoCivilText.setVisibility(View.VISIBLE);
                etedoCivilText.setBackgroundColor(getResources().getColor(R.color.white));
                etedoCivilText.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvedoCivil);
                ll.addView(etedoCivilText);
                //--- Fecha Nac
                btDate = new Button(getApplicationContext());
                btDate.setId(R.id.btDate);
                btDate.setLayoutParams(params);
                btDate.setText("Fecha Nacimiento");
                btDate.setVisibility(View.VISIBLE);
                ll.addView(btDate);
                break;
            case 111110:
                //---- Nombre
                tvNombres = new TextView(getApplicationContext());
                tvNombres.setId(R.id.nombres);
                tvNombres.setLayoutParams(params);
                tvNombres.setVisibility(View.VISIBLE);
                tvNombres.setText("Nombre(s):");
                tvNombres.setTextColor(getResources().getColor(R.color.black));
                etnombres_text = new EditText(getApplicationContext());
                etnombres_text.setId(R.id.nombres_text);
                etnombres_text.setVisibility(View.VISIBLE);
                etnombres_text.setBackgroundColor(getResources().getColor(R.color.white));
                etnombres_text.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvNombres);
                ll.addView(etnombres_text);
                //----Apellidos
                tvApellido = new TextView(getApplicationContext());
                tvApellido.setId(R.id.apellidos);
                tvApellido.setLayoutParams(params);
                tvApellido.setVisibility(View.VISIBLE);
                tvApellido.setText("Apellido:");
                tvApellido.setTextColor(getResources().getColor(R.color.black));
                etApellidos_text = new EditText(getApplicationContext());
                etApellidos_text.setId(R.id.apellidos_text);
                etApellidos_text.setVisibility(View.VISIBLE);
                etApellidos_text.setBackgroundColor(getResources().getColor(R.color.white));
                etApellidos_text.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvApellido);
                ll.addView(etApellidos_text);
                //---- RG Label
                rgLabel = new TextView(getApplicationContext());
                rgLabel.setId(R.id.rg_label);
                rgLabel.setText("Sexo:");
                rgLabel.setVisibility(View.VISIBLE);
                rgLabel.setTextColor(getResources().getColor(R.color.black));
                ll.addView(rgLabel);
                rbmasc = new RadioButton(getApplicationContext());
                rbmasc.setId(R.id.masc);
                rbmasc.setText("Masculino");
                rbmasc.setTextColor(getResources().getColor(R.color.black));
                rbmasc.setVisibility(View.VISIBLE);
                rbfem = new RadioButton(getApplicationContext());
                rbfem.setId(R.id.fem);
                rbfem.setText("Femenino");
                rbfem.setVisibility(View.VISIBLE);
                rbfem.setTextColor(getResources().getColor(R.color.black));
                rgroup = new RadioGroup(getApplicationContext());
                rgroup.setId(R.id.rg_container);
                rgroup.addView(rbmasc);
                rgroup.addView(rbfem);
                ll.addView(rgroup);
                //---- Edo Civil
                tvedoCivil = new TextView(getApplicationContext());
                tvedoCivil.setId(R.id.edoCivil);
                tvedoCivil.setLayoutParams(params);
                tvedoCivil.setText("Estado Civil:");
                tvedoCivil.setTextColor(getResources().getColor(R.color.black));
                tvedoCivil.setVisibility(View.VISIBLE);
                etedoCivilText = new EditText(getApplicationContext());
                etedoCivilText.setId(R.id.edoCivilText);
                etedoCivilText.setLayoutParams(params);
                etedoCivilText.setVisibility(View.VISIBLE);
                etedoCivilText.setBackgroundColor(getResources().getColor(R.color.white));
                etedoCivilText.setTextColor(getResources().getColor(R.color.black));
                ll.addView(tvedoCivil);
                ll.addView(etedoCivilText);
                //--- Fecha Nac
                btDate = new Button(getApplicationContext());
                btDate.setId(R.id.btDate);
                btDate.setLayoutParams(params);
                btDate.setText("Fecha Nacimiento");
                btDate.setVisibility(View.VISIBLE);
                ll.addView(btDate);
                break;
            default:
                ll.removeAllViews();

                break;
        }
        Toast.makeText(getApplicationContext(),Text,Toast.LENGTH_SHORT).show();
    }
    private void fdatePicker() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                btDate.setText(year + "-"
                        + (monthOfYear + 1) + "-" + dayOfMonth);
            }
        }, mYear, mMonth, mDay);
        dpd.show();
    }
    //-0-------------

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
            default:
                Log.i(TAG,"BACK");
                Log.i(TAG,"Item: "+item.getItemId());
                onBackPressed();
                return super.onOptionsItemSelected(item);
        }
    }
}
