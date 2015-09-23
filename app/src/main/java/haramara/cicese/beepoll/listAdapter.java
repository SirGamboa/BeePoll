package haramara.cicese.beepoll;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;

import haramara.cicese.beepoll.db.rcCompleted;
import haramara.cicese.beepoll.db.rcConfig;
import haramara.cicese.beepoll.db.rcDataEnc;
import haramara.cicese.beepoll.db.rcEncuestado;
import haramara.cicese.beepoll.db.rcEncuestas;
import haramara.cicese.beepoll.db.rcRespuestas;

/**
 * Created by diseno on 9/19/15. for BeePoll
 */
public class listAdapter extends BaseAdapter {
    private String [] sTitles;
    private String [] sSubTitles;
    private String [] sdate;
    private String [] sEncId;
    private String[] sDateSended;
    private final Context context;
    ViewGroup parent;
    private int[]  imageTrash;
    private int[] imageEdit;
    private int[] imageSend;
    private int[] imageDraft;
//    private final String wsPost = "http://idi.cicese.mx/surbeeweb/webService/ws.php";
//    String wsPost = "http://idi.cicese.mx/surbeeweb-ut3/webService/ws.php";
//    String wsPost = "http://idi.cicese.mx/surbeeweb-demo/webService/ws.php";
    private final String wsPost = "http://10.0.2.2/html/surbee/webService/ws.php";


    private final int REQUEST_CODE = 0;
    private boolean deleteIcon = false;
    private final String TAG = "ListAdapter";
    private int updated = 0;//, pos;
    private static LayoutInflater inflater=null;
    public listAdapter(Context mContext,
                       String[] titles,
                       String[] EndID,
                       String[] subTitles,
                       String[] subTitlesDate,
                       String[] dateSend,
                       int[] iTrash,
                       int[] iDraft,
                       int[] iSend,
                       int[] iEdit) {
        // TODO Auto-generated constructor stub
        sTitles=titles;
        sEncId=EndID;
        sSubTitles=subTitles;
        sdate = subTitlesDate;
        sDateSended = dateSend;
        imageSend=iSend;
        imageTrash=iTrash;
        imageDraft=iDraft;
        imageEdit = iEdit;
        context=mContext;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateResults(String[] titles,
                              String[] EndID,
                              String[] subTitles,
                              String[] subTitlesDate,
                              String[] dateSend,
                              int[] iTrash,
                              int[] iDraft,
                              int[] iSend,
                              int[] iEdit){
        sTitles=titles;
        sEncId=EndID;
        sSubTitles=subTitles;
        sdate = subTitlesDate;
        sDateSended = dateSend;
        imageSend=iSend;
        imageTrash=iTrash;
        imageDraft=iDraft;
        imageEdit = iEdit;
        notifyDataSetChanged();
        //    this.clearList();



    }
    public void clearList(){
        sTitles = null;
        sSubTitles=null;
        sdate = null;
        sDateSended = null;
        imageDraft=null;
        imageSend=null;
        imageTrash=null;
        imageEdit = null;
        notifyDataSetChanged();

    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return sTitles.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView tvT;
        TextView tvST;
        TextView tvDt;
        TextView tvID;
        TextView tvDS;
        ImageView send;
        ImageView trash;
        ImageView draft;
        ImageView edit;
//        Switch sw;
    }
    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        // pos = position-1;
        rowView = inflater.inflate(R.layout.listviews_listview, null);
        holder.tvT=(TextView) rowView.findViewById(R.id.id_encuestado);
        holder.tvST=(TextView) rowView.findViewById(R.id.nombre_encuestado);
        holder.tvDt=(TextView) rowView.findViewById(R.id.ultima_fecha);
        holder.tvID = (TextView) rowView.findViewById(R.id.idEnc);
        holder.tvDS = (TextView) rowView.findViewById(R.id.tvDateSend);
        holder.send=(ImageView) rowView.findViewById(R.id.continue_btn);
        holder.trash=(ImageView) rowView.findViewById(R.id.erase_btn);
        holder.draft=(ImageView) rowView.findViewById(R.id.erase_draft);
        holder.edit=(ImageView) rowView.findViewById(R.id.edit_btn);
        holder.tvT.setText(sTitles[position]);
        holder.tvST.setText(sSubTitles[position]);
        holder.tvDt.setText(sdate[position]);
        holder.tvID.setText(sEncId[position]);
        holder.tvDS.setText(sDateSended[position]);
        if(imageEdit[position] != 0){
            holder.edit.setVisibility(View.VISIBLE);
            holder.edit.setImageResource(imageEdit[position]);
            //holder.send.setVisibility(View.INVISIBLE);
        }
        if(imageSend[position] != 0){
            holder.send.setVisibility(View.VISIBLE);
            holder.send.setImageResource(imageSend[position]);
            //holder.edit.setVisibility(View.INVISIBLE);
        }
        if(imageTrash[position] != 0){
            holder.trash.setVisibility(View.VISIBLE);
            holder.trash.setImageResource(imageTrash[position]);
            //holder.edit.setVisibility(View.INVISIBLE);
        }
        if(imageDraft[position] != 0){
            holder.draft.setVisibility(View.VISIBLE);
            holder.draft.setImageResource(imageTrash[position]);
            //holder.edit.setVisibility(View.INVISIBLE);
        }

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i(TAG, "You Clicked " + sTitles[position]);
                Log.i(TAG, "Position: " + position);
                Log.i(TAG, "Pos: " + position);
                //   Toast.makeText(context, "You Clicked " + sTitles[position], Toast.LENGTH_LONG).show();

            }
        });
        holder.draft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //  Toast.makeText(context, "Daft to delete no action", Toast.LENGTH_SHORT).show();
                dialogo(sTitles[position], Integer.valueOf(sEncId[position]), position, false);
            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { /* Boton para enviar datos a Encuesta */
                rcEncuestas rcEnc;
                rcEnc = new rcEncuestas(context);
                try {
                    rcEnc.open();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (rcEnc.getEncs() > 0) {
                    sendActivity(position);
                    sTitles[position] = "";
                    sEncId[position] = "";
                    sSubTitles[position] = "";//+ dateSend.toString();
                    imageTrash[position] = 0;
                    imageDraft[position] = 0;
                    imageEdit[position] = 0;
                    imageSend[position] = 0;
                    sdate[position] = ""; //shhmmss.toString();
                    updateResults(sTitles, sEncId, sSubTitles, sdate, sDateSended, imageTrash, imageDraft, imageSend, imageEdit);
                } else
                    Toast.makeText(context, "Debes de sincronizar las encuestas primero", Toast.LENGTH_SHORT).show();
                rcEnc.close();
            }
        });
        holder.trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "" + position);
                dialogo(sTitles[position], Integer.valueOf(sEncId[position]), position, true);
            }
        });
        holder.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //metodo enviar
                Log.i(TAG,"SEND DATA");

                try {
                    if (sendData(sTitles[position], Integer.valueOf(sEncId[position]))) {
                        sTitles[position] = "";
                        sSubTitles[position] = "";//+ dateSend.toString();
                        imageTrash[position] = 0;
                        imageEdit[position] = 0;
                        imageDraft[position] = 0;
                        imageSend[position] = 0;
                        sdate[position] = ""; //shhmmss.toString();
                        updateResults(sTitles, sEncId, sSubTitles, sdate, sDateSended, imageTrash, imageDraft, imageSend, imageEdit);
                    }

                    updateList(position, v, parent);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        return rowView;
    }

    private void delete(String s, int idEncuesta, boolean sendView ) {
        rcRespuestas rcRes = new rcRespuestas(context);
        rcCompleted rcCom = new rcCompleted(context);
        rcEncuestas rcEnc = new rcEncuestas(context);
        rcDataEnc rcDEnc = new rcDataEnc(context);
        rcConfig rcCon = new rcConfig(context);

        try {
            rcRes.open();
            rcCom.open();
            rcEnc.open();
            rcCon.open();
            rcDEnc.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if( rcCom.deleteEdo(s) > 0)
            Log.i(TAG,"Eliminado de Borradores");
        if(sendView) {
            int idEn = Integer.valueOf(rcCon.readID());
            int cnt = rcDEnc.readNumResp(idEncuesta, idEn);
            cnt--;
            rcDEnc.updateNumResp(cnt, idEncuesta, String.valueOf(idEn));
        }
        rcRes.status(s, idEncuesta);
        rcRes.close();
        rcCon.close();
        rcCom.close();
        rcEnc.close();
        updateResults(sTitles, sEncId,sSubTitles, sdate,  sDateSended, imageTrash, imageDraft, imageSend, imageEdit);

    }

    private void dialogo(final String s,  final int idEncuesta, final int position, final boolean sView) {
        //final boolean[] value = {false};

        // 1. Instantiate an AlertDialog.Builder with its constructor
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage("BORRAR")
                .setTitle("Se Borrará: "+ s);
        // 3. Get the AlertDialog from create()
        builder.setPositiveButton("¡ SÍ !", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                sTitles[position] = null;
                sSubTitles[position]= null ;//+ dateSend.toString();
                imageTrash[position] = 0;
                imageDraft[position] = 0;
                imageEdit[position] = 0;
                imageSend[position]= 0;
                sdate[position]= null;
                sDateSended[position]= null;
                delete(s, idEncuesta, sView); //deleteIcon = true;
            }   });
        builder.setNegativeButton("¡ NO !", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteIcon = false;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        // return value[0];
    }


    private void sendActivity(int position) {
        Log.i(TAG,position+"");
//        Intent iBorrador = new Intent(context,surveyActivity.class);
//        iBorrador.putExtra("borrador",true);
//        iBorrador.putExtra("id_edo",sTitles[position]);
//        iBorrador.putExtra("id_enc",sEncId[position]);
//        context.startActivity(iBorrador);
        //metodo borrar
    }


    private void updateList(int position,View view, ViewGroup parent ) {
        @SuppressWarnings("UnusedAssignment") View v = getView(position, view, parent);
    }

    public boolean sendData(String sTitle, int idEnc)throws IOException {
        @SuppressWarnings("UnusedAssignment")

        boolean flag = false;
        //--- http URL Connection
        URL url = new URL(wsPost);
        Log.i(TAG, url.toString());
//
//        HttpClient cliente;
//        HttpPost httppost;
//        HttpResponse respuesta;
//        @SuppressWarnings("UnusedAssignment") InputStream is;
        String jsString ="";
//        String idEdor;
//        List<NameValuePair> parametros = new ArrayList<>();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
        rcRespuestas rcRes;
        rcConfig rcCon;
        rcDataEnc rcDEnc;
        rcEncuestado rcEdo;
        rcCon = new rcConfig(context);
        rcRes = new rcRespuestas(context);
        rcEdo = new rcEncuestado(context);
        rcDEnc = new rcDataEnc(context);
        try { rcCon.open();
            rcDEnc.open();
            rcEdo.open();
            rcRes.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        boolean status = false;
//        cliente = new DefaultHttpClient();
//        // http://idi.cicese.mx/surbeeweb/
//        httppost = new HttpPost(wsPost); //data.php | check
//        httppost = new HttpPost("http://idi.cicese.mx/surbeeweb-ut3/webService/ws.php"); //data.php | check
//        httppost = new HttpPost("http://idi.cicese.mx/surbeeweb-demo/webService/ws.php"); //data.php | check
        String id_Enc, id_Preg, id_Endo, resp, idPregTipo, dataEdo, idVal, SendData = null;
        //noinspection UnusedAssignment
//        idEdor = rcCon.readID(); //id encuestador
        Cursor c = rcRes.readAns(sTitle, String.valueOf(idEnc));
        // agregar encuesta------------------------------------------------------------****
        String Cid = rcCon.readID();
        JSONObject object = new JSONObject();
        JSONObject jObjct = new JSONObject();
        c.moveToFirst();
//        StringEntity se = null;
        HttpURLConnection clienteURL = null;
        String DatoTest = "id='01'&name='rodolfo'";
        OutputStreamWriter wr = null;
        try {
            clienteURL = (HttpURLConnection) url.openConnection();
            clienteURL.setDoOutput(true);
            clienteURL.setFixedLengthStreamingMode(DatoTest.length());
            clienteURL.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");


//            OutputStreamWriter wr = new OutputStreamWriter(clienteURL.getOutputStream());
//            String valueto = "id=" + URLEncoder.encode(jsString, "UTF-8");
//            Log.i(TAG, valueto);
//            wr.write(valueto);
//            wr.flush();
//            wr.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        finally{
            if(clienteURL != null)
                clienteURL.disconnect();
        }
int i= 1;
//        while(!c.isAfterLast()){
            id_Endo = "idEdo="+ c.getString(0);
            idPregTipo = "idPregTipo="+ c.getString(1);
            id_Enc = "idEnc="+ c.getString(2);
            id_Preg = "idPreg="+ c.getString(3);
            resp = "resp="+c.getString(4);
            if(c.getString(5)!= null){ idVal = "idVal="+c.getString(5); }
            else{ idVal = "idVal=0"; }
            // agregar Datos del encuestado.
            dataEdo = "dataEdo=nodata";//+rcEdo.getData(id_Endo);

            SendData = id_Enc+"&"+idPregTipo+"&"+id_Endo+"&"+id_Preg+"&"+resp+"&"+idVal+"&"+dataEdo;

//
//            try {
//                object.put("idEdo", id_Endo);
//                object.put("idPregTipo",idPregTipo);
//                object.put("idEnc", id_Enc);
//                object.put("idPreg", id_Preg);
//                object.put("idVal", idVal);
//                object.put("Resp", resp);
//                object.put("dataEdo", dataEdo);
//                object.put("IDUSER", Cid);
//
//                jsString = jsString.concat(object.toString());
//                if(!c.isLast()) {
//                    jsString = jsString.concat("-");
//                }
//                se = new StringEntity(jsString);

//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
            System.out.println(SendData);
//            System.out.println(se);
            Log.i("SE JSON", String.valueOf(SendData));
            status = true;
            c.moveToNext();

//            String valueto = "id=" + URLEncoder.encode(jsString, "UTF-8");
//            Log.i(TAG, valueto);
          //  String DatoTest = "id='01'&name='rodolfo'";
            wr = new OutputStreamWriter(clienteURL.getOutputStream());
            wr.write(DatoTest);
            wr.flush();
            wr.close();
            Log.i("SEND-HTTP", "" + i);
            i++;

//        } // end while

        c.close();
//        HttpURLConnection clienteURL = null;
//        try {
//            clienteURL = (HttpURLConnection) url.openConnection();
//            clienteURL.setDoOutput(true);
//            clienteURL.setFixedLengthStreamingMode(jsString.length());
//            clienteURL.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//
//            OutputStreamWriter wr = new OutputStreamWriter(clienteURL.getOutputStream());
//            String valueto = "id=" + URLEncoder.encode(jsString, "UTF-8");
//            Log.i(TAG, valueto);
//            wr.write(valueto);
//            wr.flush();
//            wr.close();
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//        finally{
            if(clienteURL != null)
                clienteURL.disconnect();
//        }
        Log.i("ADDED JSON SEND", jsString);

//        if(status) {

//            System.out.println(clienteURL.getResponseMessage());
//            int HTTPresp = clienteURL.getResponseCode();
//            Log.i("HTTP", HTTPresp + "");
//
//            if (HTTPresp == HttpURLConnection.HTTP_OK) {
//                Log.i(TAG, " RESPONSE OK");
//
//            } else {
//                Log.i(TAG, "No connected");
//            }

//            switch( clienteURL.getResponseCode()){
//                case 200:
//                    Log.i("HTTPOK","200");
//                    flag = true ;
//                    if(rcRes.status(sTitle, idEnc)) {
//
////                        delete(sTitle, idEnc, true);
//                        updated = 1;
//                    }
//                    Log.i(TAG,"Actualizado "+updated);
////                    int l = rcDEnc.readNumSend(idEnc, Integer.valueOf(Cid));
////                    l++;
////                    rcDEnc.updateNumSend(l, idEnc, Cid);
//                    Toast.makeText(context,"Encuesta:  "+sTitle+" Enviada",Toast.LENGTH_SHORT).show();
//                    break;
//                case 404:
//                    Log.i("HTTP","404 Not Found (HTTP/1.0 - RFC 1945)");
//                    flag = false;
//                    break;
//                case 500:
//                    Toast.makeText(context,"ERROR 500, error en el Servidor", Toast.LENGTH_LONG).show();
//                    flag = false;
//                    break;
//            }

//        }

        rcRes.close();
        rcCon.close();
        return flag;
    }


}
