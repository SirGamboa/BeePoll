package haramara.cicese.beepoll;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.text.Html;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;

import haramara.cicese.beepoll.db.dbEncuestas;
import haramara.cicese.beepoll.db.dbOpciones;
import haramara.cicese.beepoll.db.dbPreguntas;
//import haramara.cicese.beepoll.db.dbRelacion;
import haramara.cicese.beepoll.db.rcConfig;
import haramara.cicese.beepoll.db.rcEncuestas;
import haramara.cicese.beepoll.db.rcOpciones;
import haramara.cicese.beepoll.db.rcPreguntas;
import haramara.cicese.beepoll.db.rcRelacion;

/**
 * Created by diseno on 9/14/15. for BeePoll
 */
public class wsData {

private static final String TAG_ID          = "id";
private static final String TAG_NOMBRE      = "nombre";
private static final String TAG_DESC        = "descripcion";
private static final String TAG_INST        = "instrucciones";
private static final String TAG_ETIQ        = "etiqueta";
private static final String TAG_EDO        = "encuestado";
private static final String TAG_EDOR        = "encuestador";
private static final String TAG_PREGUNTAS   = "preguntas";
private static final String TAG_PREG_ID     = "id";
private static final String TAG_PREG_IDVAL     = "idVal";
private static final String TAG_PREG_IDPREGTIPO     = "idPregTipo";
private static final String TAG_PREG_ENC    = "encuesta_id";
private static final String TAG_PREG_DESC   = "descripcion";
private static final String TAG_PREG_TIPO   = "tipo";
private static final String TAG_PREG_PESO   = "peso";
private static final String TAG_PREG_VAL    ="preg_valoracion";
private static final String TAG_PREG_IMAGE    ="idImage";
private static final String TAG_RESP        = "respuestas";
private static final String TAG_RESP_ID     = "id";
private static final String TAG_RESP_PREG   = "pregunta_id";
private static final String TAG_RESP_DESC   = "descripcion";
private static final String TAG_RESP_VAL = "preg_valoracion";
private static final String TAG_RESP_IDVAL = "idVal";
private static final String TAG_RESP_PESO   = "peso";
private static final String TAG_PREGUNTA = "pregunta";
private dbEncuestas dbEnc;
private dbPreguntas dbPreg;
private dbOpciones dbOpc;
private rcConfig rcCon;
//private dbRelacion dbRel;
String[] aPhotos;
private final String addrs = "http://idi.cicese.mx/surbeeweb/uploads/pregunta/";
private final String path = "/mnt/sdcard/beepoll/images/";
private static final String targetURL = "http://idi.cicese.mx/surbeeweb/webService/";
File directory;
//    targetURL = "http://idi.cicese.mx/surbeeweb-ut3/webService/";
//    static final String targetURL = "http://idi.cicese.mx/surbeeweb-demo/webService/";
private rcRelacion rcRel;
Cursor cursor;
// = new ContentValues();
private final String TAG = "WSSearch";
    //  public String targetURL = "http://idi.cicese.mx/surbeeweb-ut3/webService"; ///wsUser.php?user=" + user;


    public void relationEncEdores(String user, Context context) throws IOException {

        String[] encs;

        String targetURLRel;
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
        HttpURLConnection con = null;
        try {
//            targetURL ="http://10.0.2.2/HTML/surbee/webService/wsUser.php?user="+user.toString();
            rcCon = new rcConfig(context);
            try {
                rcCon.open();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            int idEncuestador = Integer.valueOf(rcCon.readID());
            rcCon.close();
            targetURLRel =  targetURL + "wsRelation.php?user="+user;
            Log.i(TAG, targetURLRel);
            URL url = new URL(targetURLRel);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int response = con.getResponseCode();
            switch(response){
                case 500:
                    Toast.makeText(context, "Error servidor 500", Toast.LENGTH_SHORT).show();
                    //finish();
                    break;
                default:
                    break;
            }
            Log.i(TAG, "Antes de...");
            //leer datos web service ( info : info : info)
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            //noinspection ConstantConditions
            if (br != null) {
                String payload = br.readLine(); // add localdb
                Log.i(TAG, payload);
                rcRel = new rcRelacion(context);
                try {
                    rcRel.open();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                encs = payload.split(">");
                //noinspection ForLoopReplaceableByForEach
                for(int i=0; i< encs.length; i++){
                    if(!rcRel.checkUpdate(encs[i], idEncuestador))
                        rcRel.addRel(encs[i], idEncuestador);
                }
                rcRel.close();

            }
        }catch (MalformedURLException e) {
            // TO DO Auto-generated catch block
            e.printStackTrace();
        } finally{
            if(con != null){
                con.disconnect();
            }
        }

    }

    public boolean readUserWS(String user, Context context) throws IOException{
        @SuppressWarnings("UnusedAssignment") boolean returnValue = false;
        String[] name_mail = new String[]{"Email","Name"};

        String targetURLrUser;
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
        HttpURLConnection con = null;
        try {
//            targetURL ="http://10.0.2.2/HTML/surbee/webService/wsUser.php?user="+user.toString();
            targetURLrUser = targetURL + "wsUser.php?user=" + user;
            Log.i(TAG, targetURLrUser);
            URL url = new URL(targetURLrUser);
            //  Log.i(TAG, url.toString());
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int er = con.getResponseCode();
            switch(er){
                case 400:
                    Log.i(TAG,"400 OK");
                    Toast.makeText(context, "Error 400 del servidor, archivo no encontrado", Toast.LENGTH_SHORT).show();
                    break;
                case 404:
                    Log.i(TAG,"400 OK");
                    Toast.makeText(context, "Error 404 del servidor, archivo no encontrado", Toast.LENGTH_SHORT).show();
                    break;
                case 200:
                    Log.i(TAG, "Antes de...");
                    //leer datos web service ( info : info : info)
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                    //noinspection ConstantConditions
                    if (br != null){
                        String payload = br.readLine(); // add localdb
                        name_mail = payload.split(":");
                        if(Integer.valueOf(name_mail[0]) > 0){
                            rcCon = new rcConfig(context);
                            try {
                                rcCon.open();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            ContentValues cv = new ContentValues();
//                            name_mail = payload.split(":");
                            cv.put("ID", name_mail[0]);
                            cv.put("NAME",name_mail[1]);
                            cv.put("USER", user);
                            if(rcCon.addUser(cv)) returnValue = true;
                            rcCon.close();
                        }else{
                            Toast.makeText(context, "No hay conexión", Toast.LENGTH_SHORT).show();
                            returnValue = false;
                        }
                    }
                    Log.i(TAG,"200 OK");
                    break;
                case 500:
                    Log.i(TAG,"500 OK");
                    Toast.makeText(context, "Error 500 del servidor", Toast.LENGTH_SHORT).show();
                    break;
            }
        }catch (MalformedURLException e) {
            // TO DO Auto-generated catch block
            e.printStackTrace();
        } finally{
            if(con != null){
                con.disconnect();
            }
        }
        return returnValue;

    }

    @SuppressWarnings({"unused", "ConstantConditions"})
    public boolean webServiceSearch(String user, Context context)  throws IOException, JSONException, SQLException {
        ContentValues cvEnc, cvPregs, cvOpcs;
        rcEncuestas rcEnc = new rcEncuestas(context);
        rcPreguntas rcPreg = new rcPreguntas(context);
        rcOpciones rcOpc = new rcOpciones(context);
        rcOpc.open();
        rcEnc.open();
        rcPreg.open();
        boolean succx = true;

        String targetURLws;
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
        //coneccion webservice
        HttpURLConnection con = null;
        try {
//            targetURL = "http://10.0.2.2/html/surbee/webService/wsLoadJson.php?correo=" + user.toString();
            targetURLws = targetURL + "wsLoadJSON.php?correo=" + user;
            Log.i(TAG, targetURLws);
            URL url = new URL(targetURLws);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            Log.i(TAG,"Antes de...");
            //leer datos web service ( info : info : info)
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
            //noinspection ConstantConditions
            if(br != null){
                String payload = br.readLine();
//                Log.i("PAYLOAD", payload);
                br.close();
//                Log.i(TAG, "Antes de JSON");
                String id , nombre , desc , inst , etiq , encuestado , preguntas , PregVal, IDVal;
                String PregID , PregEnc , PregDesc , PregTipo , edor , PregPeso, RespVal , PregIMAGE;
                String RespID , RespPreg , RespDesc , RespPeso , RespIDEnc , pregunta = null;
                String UserMail ; int PregidPregTipo ;
                rcCon = new rcConfig(context);
                rcCon.open();
                UserMail = rcCon.readUserMail();
                rcCon.close();

                if(payload.equals("ERX0")){
                    Log.i(TAG,"ERROR X0");
                    Toast.makeText(context, "ERROR EN WS!, no existe el usuario: " + UserMail, Toast.LENGTH_SHORT).show();
                    succx = false;
                }else if(payload.equals("[]")){
                    Log.i(TAG,"ERROR X0");
                    Toast.makeText(context, "ERROR EN WS!, el usuario: " + UserMail + " No tiene encuestas asignadas", Toast.LENGTH_SHORT).show();
                }


                JSONArray jsArr = new JSONArray(payload); // arreglo []
                Log.i(TAG, "Antes del FOR JSDetail");
                for(int i=0; i< jsArr.length(); i++){
                    JSONObject jsDetails = jsArr.getJSONObject(i);

                    cvEnc = new ContentValues();
                    id = jsDetails.getString(TAG_ID); // id
                    edor = jsDetails.getString(TAG_EDOR); //encuestador
                    nombre = jsDetails.getString(TAG_NOMBRE); // nombre
                    desc = jsDetails.getString(TAG_DESC); // descropcion
                    inst = jsDetails.getString(TAG_INST); // instrucciones
                    etiq = jsDetails.getString(TAG_ETIQ); // etiquetas
                    encuestado = jsDetails.getString(TAG_EDO); //Encuestado (######b8)
                    preguntas = jsDetails.getString(TAG_PREGUNTAS); // preguntas

                    /* CV Data */

                    cvEnc.put("id", Integer.valueOf(id));
                    cvEnc.put(dbEnc.COLUMN_NAME_IDENCUESTA, Integer.valueOf(id)); // de 00000000000n a n;
                    cvEnc.put(dbEnc.COLUMN_NAME_IDEDO, edor);
                    cvEnc.put(dbEnc.COLUMN_NAME_NOMBRE, String.valueOf(Html.fromHtml(nombre)));
                    cvEnc.put(dbEnc.COLUMN_NAME_DESCRIPCION, String.valueOf(Html.fromHtml(desc)));
                    cvEnc.put(dbEnc.COLUMN_NAME_INSTRUCCIONES, String.valueOf(Html.fromHtml(inst)));
                    cvEnc.put(dbEnc.COLUMN_NAME_ETIQUETA, String.valueOf(Html.fromHtml(etiq)));
                    cvEnc.put(dbEnc.COLUMN_NAME_EDO, encuestado);
//                    Log.i("CV Encuestas ->OK: ", cvEnc.toString());
////--------------------------
//                    Log.i("PREGUNTAS Obj", preguntas);
                    JSONArray jsA0 = new JSONArray(preguntas);
                    Log.i("JSA0",jsA0.toString());
                    for(int j=0; j<jsA0.length(); j++) {
                        JSONObject jsDObject = jsA0.getJSONObject(j);
                        pregunta = jsDObject.getString(TAG_PREGUNTA);
                        //leer el arreglo
//                        Log.i("PREGUNTA->", pregunta);
                    }
                    //------- segundo ciclo para agarrar las preguntas
//                    Log.i("REGUNTAS","2do ciclo para preguntas");
                    Log.i("PREGUNTAS", pregunta);
                    if(pregunta != null){ //tiene INFO Payload Preguntas
                        Log.i("ADDED", cvEnc.toString());
                        rcRel = new rcRelacion(context);
                        rcRel.open();
                        if(!rcRel.checkUpdate(id,Integer.valueOf(edor)))
                            rcEnc.addEncuesta(cvEnc);
                        rcRel.close();
                        cvPregs = new ContentValues();
                        JSONArray jsA = new JSONArray(pregunta);
                        Log.i(TAG_PREGUNTA, jsA.toString());

                        for(int j = 0; j < jsA.length(); j++) {
                            JSONObject jsObj = jsA.getJSONObject(j);
                            PregID = jsObj.getString(TAG_PREG_ID);
                            IDVal = jsObj.getString(TAG_PREG_IDVAL);
                            PregEnc = jsObj.getString(TAG_PREG_ENC);
                            PregDesc = jsObj.getString(TAG_PREG_DESC);
                            if(jsObj.getString(TAG_PREG_IMAGE) != null){
                                PregIMAGE = jsObj.getString(TAG_PREG_IMAGE);
                                cvPregs.put(dbPreg.COLUMN_NAME_IMAGE,PregIMAGE);
                            }

                            if (jsObj.getString(TAG_PREG_IDPREGTIPO) != null){
                                PregidPregTipo = jsObj.getInt(TAG_PREG_IDPREGTIPO);
                                cvPregs.put(dbPreg.COLUMN_NAME_IDPREGTIPO,PregidPregTipo);
                            }
                            PregTipo = jsObj.getString(TAG_PREG_TIPO);
                            if(jsObj.getString(TAG_PREG_PESO)!= null){
                                PregPeso = jsObj.getString(TAG_PREG_PESO);
                                cvPregs.put(dbPreg.COLUMN_NAME_PESO, PregPeso);
                            }
                            cvPregs.put(dbPreg.COLUMN_NAME_PREG_ID,Integer.valueOf(PregID));
                            cvPregs.put(dbPreg.COLUMN_NAME_PREG_IDVAL,Integer.valueOf(IDVal));
                            cvPregs.put(dbPreg.COLUMN_NAME_IDENCUESTA,Integer.valueOf(PregEnc));
                            cvPregs.put(dbPreg.COLUMN_NAME_DESCRIPCION, PregDesc);
//                            if(jsObj.getString(TAG_PREG_VAL)!= null) {
                            PregVal = jsObj.getString(TAG_PREG_VAL);
                            cvPregs.put(dbPreg.COLUMN_NAME_PREGVAL, PregVal);
//                            }
                            cvPregs.put(dbPreg.COLUMN_NAME_TIPO, PregTipo);
//                            cvPregs.put(dbPreg.COLUMN_NAME_IMAGE, PregIMAGE);
//                            Log.i("CV Preguntas ->",cvPregs.toString());
                            if( !rcPreg.checkPreguntas(cvPregs)  )
                                rcPreg.addPreguntas(cvPregs);
//                            Log.i("ADDED",cvPregs.toString());

                            if(Integer.valueOf(PregTipo) >= 1 && Integer.valueOf(PregTipo) < 3){ //es pregunta de opcion
                                cvOpcs = new ContentValues();
                                Log.i(TAG+"Tipo0->", PregTipo);
                                String objRespuesta = jsObj.getString(TAG_RESP);
                                Log.i(TAG, objRespuesta);
                                JSONArray jsResp = new JSONArray(objRespuesta);
                                for(int k = 0; k<jsResp.length(); k++){
                                    JSONObject jsRespObj = jsResp.getJSONObject(k);
                                    RespID = jsRespObj.getString(TAG_RESP_ID);
                                    RespIDEnc = id; //jsRespObj.getString(TAG_RESP_IDENCUESTA);
                                    RespPreg = jsRespObj.getString(TAG_RESP_PREG);
                                    RespDesc = jsRespObj.getString(TAG_RESP_DESC);
                                    RespPeso = jsRespObj.getString(TAG_RESP_PESO);
                                    Log.i(TAG, "A CHECAR");
                                    if(jsRespObj.getString(TAG_RESP_VAL).length() > 1){
                                        RespVal = jsRespObj.getString(TAG_RESP_VAL);
                                        cvOpcs.put(dbOpc.COLUMN_NAME_PREGVAL, RespVal);
                                    }
                                    if(jsRespObj.getString(TAG_RESP_IDVAL).length() > 1){
                                        IDVal = jsRespObj.getString(TAG_RESP_IDVAL);
                                        cvOpcs.put(dbOpc.COLUMN_NAME_IDVAL, IDVal);
                                    }
                                    Log.i(TAG,IDVal);
                                    cvOpcs.put(dbOpc.COLUMN_NAME_ID, Integer.valueOf(RespID));
                                    cvOpcs.put(dbOpc.COLUMN_NAME_IDENCUESTA, Integer.valueOf(RespIDEnc));
                                    cvOpcs.put(dbOpc.COLUMN_NAME_IDPREGUNTA,Integer.valueOf(RespPreg));
                                    cvOpcs.put(dbOpc.COLUMN_NAME_DESCRIPCION, RespDesc);
                                    cvOpcs.put(dbOpc.COLUMN_NAME_PESO, RespPeso);
                                    Log.i("CV OpcPreguntas", cvOpcs.toString());
                                    //       if(!rcOpc.checkOpciones(cvOpcs))
                                    rcOpc.addOpciones(cvOpcs);

                                }

                            } //end if tipo !=0
                        } //end fo/
                    }

                }
            }

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            if(con != null){
                con.disconnect();
            }
        }
        //---- Load Image

        rcEnc.close();
        rcPreg.close();
        rcOpc.close();

        return succx;

    }

    public void loadImage(Context context) throws IOException, SQLException {
        int response;
        URL url = null;
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
        HttpURLConnection con = null;
        int j = 0;// aPhotos.length;
        rcPreguntas rcPregimage = new rcPreguntas(context);
        rcPregimage.open();
        aPhotos = rcPregimage.getPhotos();
        rcPregimage.close();
        Log.i(TAG,aPhotos.length+" items");
        while (j < aPhotos.length) {


            url = new URL(addrs + aPhotos[j]);
            Log.i(TAG, url.toString());
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            response = con.getResponseCode();
            switch (response) {
                case 200:
                    Log.i(TAG, "Cargando imagen");
                    Bitmap bmp = null;
                    BufferedInputStream bis = new BufferedInputStream(con.getInputStream());
//                    Bitmap bitmap = BitmapFactory.decodeStream(bis);
                    bmp = BitmapFactory.decodeStream(bis);
                    ContextWrapper cw = new ContextWrapper(context);
                    directory = cw.getDir("images",context.MODE_PRIVATE);
                    File path = new File(directory,aPhotos[j]);
                    FileOutputStream imageFile = new FileOutputStream(path);
                    //openFileOutput(aPhotos[j], getApplicationContext().MODE_PRIVATE);
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, imageFile);
                    Log.i(TAG,"File saved :"+aPhotos[j]+ " on: "+path);
                    imageFile.close();
                    Toast.makeText(context,"File '"+aPhotos[j]+"' downloaded",Toast.LENGTH_SHORT).show();
//                    if (bmp != null) {
//                        try {
//                            Log.i(TAG, bmp.toString());
//                            iView.setImageBitmap(bmp);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        Log.i(TAG, "Error bitmap");
//                    }
                    //finish();
                    break;
                default:
                    break;
            }
            j++;
        }

    }
}
