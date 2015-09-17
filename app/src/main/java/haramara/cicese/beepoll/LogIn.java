package haramara.cicese.beepoll;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.sql.SQLException;
import java.util.TimerTask;

import haramara.cicese.beepoll.db.rcConfig;
import haramara.cicese.beepoll.db.rcOpciones;
import haramara.cicese.beepoll.db.rcPreguntas;
import haramara.cicese.beepoll.db.rcRelacion;

public class LogIn extends AppCompatActivity {
    private final String TAGTEXT = this.getClass().getName();
    private final String TAG = "LoginActivity";
    private wsData ws;
    //    ContentValues cv;
    private TextView correo_label;
    private EditText correo;
    private Button btn_send;
    private rcConfig rc;
    ImageView imageView2;
    TextView instruccciones;
    private int requestCode;
    private int readEmail;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        instruccciones = (TextView) findViewById(R.id.instruccciones);
        //Image boton ?
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        //noinspection ConstantConditions
//        getSupportActionBar().hide();
        ws = new wsData();
        @SuppressWarnings("UnusedAssignment") Intent i;
        instruccciones.setVisibility(View.INVISIBLE);
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instruccciones.setVisibility(View.VISIBLE);
                mHandler = new Handler();
                mHandler.postDelayed(csRunnable,4000);
//                instruccciones.setVisibility(View.INVISIBLE);

            }
        });

        Log.i(TAG, TAGTEXT);
        //    correo_label = (TextView) findViewById(R.id.email_label);
        correo = (EditText) findViewById(R.id.email_holder);
        btn_send = (Button) findViewById(R.id.button_submit);

        rc = new rcConfig(getApplicationContext());
        readMail();

        if (readEmail > 0) { // existe usuario en db local
            openMainActivity();
        } else {
            correo.setVisibility(View.VISIBLE);
            //correo_label.setVisibility(View.VISIBLE);
        }
        //---- end check user existe db local
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    readMail();
                    if (readEmail > 0) {
                        openMainActivity();
                    } else {
                        if (checkEmail() && ws.readUserWS(correo.getText().toString(), getApplicationContext())) {
                            correo.setText("");
                            openMainActivity();
                        } else
                            Toast.makeText(getApplicationContext(), "No hay usuario registrado", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    Runnable csRunnable = new Runnable() {
        @Override
        public void run() {
            instruccciones.setVisibility(View.INVISIBLE);
        }
    };


    private boolean checkEmail() {
        //
        return correo.getText().toString().contains("@");
    }

    private void readMail() {
        try {
            rc.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        readEmail = rc.readUsuario();
        rc.close();
    }

    private void openMainActivity() {
        Intent i = new Intent(this, main_activity.class);
        startActivityForResult(i, requestCode);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        correo.setText("");
        correo.setVisibility(View.INVISIBLE);
        // correo_label.setVisibility(View.INVISIBLE);
        if(requestCode == RESULT_CANCELED){
            //regrese back button
            correo.setVisibility(View.VISIBLE);
            //  correo_label.setVisibility(View.VISIBLE);
            btn_send.setVisibility(View.VISIBLE);
        }
        if(resultCode == 44){
            rcPreguntas rcPreg = new rcPreguntas(getApplicationContext());
            rcOpciones rcOpcs = new rcOpciones(getApplicationContext());
            rcRelacion rcRel = new rcRelacion(getApplicationContext());
            try {
                rc.open();
                rcPreg.open();
                rcOpcs.open();
                rcRel.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (rc.delUser()) {
                Log.i(TAG, "OK");
                Log.i(TAG, "DB Deleted");
            } else Log.i(TAG, "NO OK");

            rcPreg.deletePregs();
            rcOpcs.deleteOpcs();
            rcRel.deleteRel();

            rc.close();
            rcPreg.close();
            rcOpcs.close();
            rc.close();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_log_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
