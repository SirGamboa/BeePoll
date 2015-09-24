package haramara.cicese.beepoll;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import haramara.cicese.beepoll.db.dbEncuestado;
import haramara.cicese.beepoll.db.rcEncuestado;

import static java.security.AccessController.getContext;

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
    private Context context;

    private FloatingActionButton next;
    private Button btDate;
//    private TextView tvApellido;
//    private TextView tvNombres;
//    private TextView tvedoCivil;
//    private TextView rgLabel;
    private EditText etApellidos_text;
    private EditText etnombres_text;
    private EditText etedoCivilText;
    private RadioGroup rgLabel;
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
        ll = (LinearLayout) findViewById(R.id.RLAdd);
        setToolbar();
        Intent i = getIntent();
        dUser = i.getExtras().getInt("edo");
        btDate = (Button) findViewById(R.id.btDate);
//        tvApellido = (TextView) findViewById(R.id.ape);
//        tvNombres = (TextView) findViewById(R.id.nom);
//        TextView tvFechaLabel = (TextView) findViewById(R.id.fechaLabel);
//        tvedoCivil = (TextView) findViewById(R.id.edoCivil);
        //--- EditText
        etApellidos_text = (EditText) findViewById(R.id.ape);
        etnombres_text = (EditText) findViewById(R.id.nom);
        etedoCivilText = (EditText) findViewById(R.id.EstadoCivil);
        //--- RGs
        rgLabel = (RadioGroup) findViewById(R.id.rg_label);
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
                RadioGroup rg = (RadioGroup) findViewById(R.id.rg_label);
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
                   // flagCheck ++;
                   // int id_checked = rg.getCheckedRadioButtonId();
//                    View sexo_op = findViewById(R.id.rg_label);
//                    Button rb_checked = (Button) rg.getChildAt(selected_id);
//                    if(selected_id > 0) {
//                        s =selected_id;
//                        flagCheck --;
//                    }

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
                    cv.put(dbEncuestado.COLUMN_NAME_CLAVE, clave);
                    cv.put(dbEncuestado.COLUMN_NAME_NOMBRES, nombres);
                    cv.put(dbEncuestado.COLUMN_NAME_APELLIDO_PATERNO, apps[0]);
                    if(ind > 1)
                        cv.put(dbEncuestado.COLUMN_NAME_APELLIDO_MATERNO, apps[1]);
                    else cv.put(dbEncuestado.COLUMN_NAME_APELLIDO_MATERNO, "LN");
                    cv.put(dbEncuestado.COLUMN_NAME_FECHA_NACIMIENTO, fnac);
                    cv.put(dbEncuestado.COLUMN_NAME_SEXO, sexo);
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
    @TargetApi(Build.VERSION_CODES.M)
    private void showUserInputs(int dUser) {


        //---- Nombre
        etnombres_text.setVisibility(View.VISIBLE);
        //----Apellidos
        etApellidos_text.setVisibility(View.VISIBLE);
        //---- Edo Civil
        etedoCivilText.setVisibility(View.VISIBLE);
        //--- Fecha Nac
        btDate.setVisibility(View.VISIBLE);
        //---- RG Label
        rgLabel.setVisibility(View.VISIBLE);
        rbmasc.setVisibility(View.VISIBLE);
        rbfem.setVisibility(View.VISIBLE);
        Time tm = new Time();
        tm.setToNow();
        tm.format("HHmmss");
        String shhmmss = tm.toString().substring(9,15);

//        ll.setOrientation(LinearLayout.VERTICAL);
//        ll.removeAllViews();






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
                cv.put(dbEncuestado.COLUMN_NAME_CLAVE, clave);
                cv.put(dbEncuestado.COLUMN_NAME_NOMBRES, "Anonimo");
                cv.put(dbEncuestado.COLUMN_NAME_APELLIDO_PATERNO, "");
                cv.put(dbEncuestado.COLUMN_NAME_APELLIDO_MATERNO, "");
                cv.put(dbEncuestado.COLUMN_NAME_FECHA_NACIMIENTO, "");
                cv.put(dbEncuestado.COLUMN_NAME_SEXO, "");
                rcEnc.addEncuestado(cv);

                Intent i = new Intent();
                i.putExtra("id_encuestado", id);
                i.putExtra("clave", clave);
                setResult(55, i);
                rcEnc.close();
                finish();
                break;
            case 10: //dob
                etnombres_text.setVisibility(View.INVISIBLE);
                rgLabel.setVisibility(View.INVISIBLE);
                rbmasc.setVisibility(View.INVISIBLE);
                rbfem.setVisibility(View.INVISIBLE);
                etedoCivilText.setVisibility(View.INVISIBLE);
                etApellidos_text.setVisibility(View.INVISIBLE);

                break;
            case 100: //apellido
                etnombres_text.setVisibility(View.INVISIBLE);
                rgLabel.setVisibility(View.INVISIBLE);
                rbmasc.setVisibility(View.INVISIBLE);
                rbfem.setVisibility(View.INVISIBLE);
                etedoCivilText.setVisibility(View.INVISIBLE);
                btDate.setVisibility(View.INVISIBLE);
                break;
            case 1000: //edo Civil
                etnombres_text.setVisibility(View.INVISIBLE);
                rgLabel.setVisibility(View.INVISIBLE);
                rbmasc.setVisibility(View.INVISIBLE);
                rbfem.setVisibility(View.INVISIBLE);
                btDate.setVisibility(View.INVISIBLE);
                etApellidos_text.setVisibility(View.INVISIBLE);
                break;
            case 10000: //sexo
                etnombres_text.setVisibility(View.INVISIBLE);
                etedoCivilText.setVisibility(View.INVISIBLE);
                btDate.setVisibility(View.INVISIBLE);
                etApellidos_text.setVisibility(View.INVISIBLE);
                break;
            case 100000: //nombre
                rgLabel.setVisibility(View.INVISIBLE);
                rbmasc.setVisibility(View.INVISIBLE);
                rbfem.setVisibility(View.INVISIBLE);
                etedoCivilText.setVisibility(View.INVISIBLE);
                btDate.setVisibility(View.INVISIBLE);
                etApellidos_text.setVisibility(View.INVISIBLE);
                break;
            case 110: // Apellido, fecha Nac
                etnombres_text.setVisibility(View.INVISIBLE);
                rgLabel.setVisibility(View.INVISIBLE);
                rbmasc.setVisibility(View.INVISIBLE);
                rbfem.setVisibility(View.INVISIBLE);
                etedoCivilText.setVisibility(View.INVISIBLE);
                break;
            case 1010:// edo civil, fecha Nac
                etnombres_text.setVisibility(View.INVISIBLE);
                rgLabel.setVisibility(View.INVISIBLE);
                rbmasc.setVisibility(View.INVISIBLE);
                rbfem.setVisibility(View.INVISIBLE);
                etApellidos_text.setVisibility(View.INVISIBLE);
                break;
            case 1100:// edo civil, apellido
                etnombres_text.setVisibility(View.INVISIBLE);
                rgLabel.setVisibility(View.INVISIBLE);
                rbmasc.setVisibility(View.INVISIBLE);
                rbfem.setVisibility(View.INVISIBLE);
                btDate.setVisibility(View.INVISIBLE);
                break;
            case 1110: // edo civil, apellido, fecha Nac
                etnombres_text.setVisibility(View.INVISIBLE);
                rgLabel.setVisibility(View.INVISIBLE);
                rbmasc.setVisibility(View.INVISIBLE);
                rbfem.setVisibility(View.INVISIBLE);
                break;
            case 10010: // sexo, fecha Nac
                etnombres_text.setVisibility(View.INVISIBLE);
                etedoCivilText.setVisibility(View.INVISIBLE);
                etApellidos_text.setVisibility(View.INVISIBLE);
                break;
            case 10100: //sexo, apellido
                etnombres_text.setVisibility(View.INVISIBLE);
                rgLabel.setVisibility(View.INVISIBLE);
                rbmasc.setVisibility(View.INVISIBLE);
                rbfem.setVisibility(View.INVISIBLE);
                etedoCivilText.setVisibility(View.INVISIBLE);

                break;
            case 10110: // fecha nac, apellido, sexo
                etnombres_text.setVisibility(View.INVISIBLE);
                etedoCivilText.setVisibility(View.INVISIBLE);
                break;
            case 11000:// sexo, edo civil
                etnombres_text.setVisibility(View.INVISIBLE);
                btDate.setVisibility(View.INVISIBLE);
                etApellidos_text.setVisibility(View.INVISIBLE);

                break;
            case 11010:// sexo, edo civil, dob
                etnombres_text.setVisibility(View.INVISIBLE);
                etApellidos_text.setVisibility(View.INVISIBLE);

                break;
            case 11100:// sexo, edo civil, apellido
                etnombres_text.setVisibility(View.INVISIBLE);
                btDate.setVisibility(View.INVISIBLE);
                break;
            case 11110://sexo, edo civil, apellido, dob
                etnombres_text.setVisibility(View.INVISIBLE);

                break;
            case 100010://nombre, dob

                rgLabel.setVisibility(View.INVISIBLE);
                rbmasc.setVisibility(View.INVISIBLE);
                rbfem.setVisibility(View.INVISIBLE);
                etedoCivilText.setVisibility(View.INVISIBLE);
                etApellidos_text.setVisibility(View.INVISIBLE);
                break;
            case 100100: //nombre, ape

                rgLabel.setVisibility(View.INVISIBLE);
                rbmasc.setVisibility(View.INVISIBLE);
                rbfem.setVisibility(View.INVISIBLE);
                etedoCivilText.setVisibility(View.INVISIBLE);
                btDate.setVisibility(View.INVISIBLE);
                break;
            case 100110: //nombre, apellido, dob

                rbmasc.setVisibility(View.INVISIBLE);
                rbfem.setVisibility(View.INVISIBLE);
                etedoCivilText.setVisibility(View.INVISIBLE);
                break;
            case 101000://nombre edo civil
                rgLabel.setVisibility(View.INVISIBLE);
                rbmasc.setVisibility(View.INVISIBLE);
                rbfem.setVisibility(View.INVISIBLE);
                btDate.setVisibility(View.INVISIBLE);
                etApellidos_text.setVisibility(View.INVISIBLE);
                break;
            case 101010: //nombre, edo civil, dob
                rgLabel.setVisibility(View.INVISIBLE);
                rbmasc.setVisibility(View.INVISIBLE);
                rbfem.setVisibility(View.INVISIBLE);
                etApellidos_text.setVisibility(View.INVISIBLE);
                break;
            case 101100://Nombre apellido edoCivil
                rgLabel.setVisibility(View.INVISIBLE);
                rbmasc.setVisibility(View.INVISIBLE);
                rbfem.setVisibility(View.INVISIBLE);
                btDate.setVisibility(View.INVISIBLE);
                break;
            case 101110://nombre, edo civil, apellido, dob
                rgLabel.setVisibility(View.INVISIBLE);
                rbmasc.setVisibility(View.INVISIBLE);
                rbfem.setVisibility(View.INVISIBLE);
                break;
            case 110000: //nombre, sexo
                etedoCivilText.setVisibility(View.INVISIBLE);
                btDate.setVisibility(View.INVISIBLE);
                etApellidos_text.setVisibility(View.INVISIBLE);
                break;
            case 110010://nombre, sexo, dob
                etedoCivilText.setVisibility(View.INVISIBLE);
                etApellidos_text.setVisibility(View.INVISIBLE);
                break;
            case 110100: //nombre, sexo, apellido
                etedoCivilText.setVisibility(View.INVISIBLE);
                btDate.setVisibility(View.INVISIBLE);
                break;
            case 110110: //nombre, sexo, apellido, dob

                etedoCivilText.setVisibility(View.INVISIBLE);
                break;
            case 111000: //nombre, sexo, edo civil
                btDate.setVisibility(View.INVISIBLE);
                etApellidos_text.setVisibility(View.INVISIBLE);
                break;
            case 111010://nombre, sexo, edo civil, dob

                etApellidos_text.setVisibility(View.INVISIBLE);
                break;
            case 111110:
                //---- Nombre
                etnombres_text.setVisibility(View.VISIBLE);
//                //----Apellidos
                etApellidos_text.setVisibility(View.VISIBLE);
                //---- Edo Civil
                etedoCivilText.setVisibility(View.VISIBLE);
//                //--- Fecha Nac
                btDate.setVisibility(View.VISIBLE);
//                //---- RG Label
                rgLabel.setVisibility(View.VISIBLE);
                rbmasc.setVisibility(View.VISIBLE);
                rbfem.setVisibility(View.VISIBLE);
                break;
            default:
                //---- Nombre
                etnombres_text.setVisibility(View.VISIBLE);
//                //----Apellidos
                etApellidos_text.setVisibility(View.VISIBLE);
                //---- Edo Civil
                etedoCivilText.setVisibility(View.VISIBLE);
//                //--- Fecha Nac
                btDate.setVisibility(View.VISIBLE);
//                //---- RG Label
                rgLabel.setVisibility(View.VISIBLE);
                rbmasc.setVisibility(View.VISIBLE);
                rbfem.setVisibility(View.VISIBLE);
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
            ab.setHomeAsUpIndicator(R.mipmap.ic_arrow_back_white_24dp);
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
