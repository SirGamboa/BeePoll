package haramara.cicese.beepoll;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.sql.SQLException;

import haramara.cicese.beepoll.db.rcCompleted;
import haramara.cicese.beepoll.db.rcConfig;
import haramara.cicese.beepoll.db.rcEncuestado;
import haramara.cicese.beepoll.db.rcEncuestas;
import haramara.cicese.beepoll.db.rcRespuestas;

/**
 * Created by diseno on 9/19/15. for BeePoll
 */

public class fListados extends Fragment {
    String TAGD = "BorradoresView";
    String TAGS = "EnviadosView";
    String TAGO = "BandejaView";
    String TAG = "fListados";
    rcCompleted rcCom;
    rcEncuestado rcEdo;
    rcEncuestas rcEncs;
    rcConfig rcCon;
    rcEncuestado rcEnc;
    rcRespuestas rcResp;
    rcEncuestas rcEns;


    ViewGroup root;
    Context context;
    private ListView lv;
    private static String [] sName;
    private static String [] sFullName;
    private static String [] sEnc;
    private static String [] sidEnc;
    private static String [] sEncName;
    private static String [] sDateSend;
    private int[] iDiscard;
    private int[] iSendNow;
    private int[] iEdit;
    private int[] iDraft;
    private boolean firstTime = true;
    private ProgressBar pb;
    public String sTitleEnc;
    public int iIDEnc;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)  {
       root = (ViewGroup) inflater.inflate(R.layout.listas_listview, null);

        context = root.getContext();
        String titletoolbar = (String) getActivity().getTitle();
        rcCom = new rcCompleted(context);
        rcEdo = new rcEncuestado(context);
        rcEncs = new rcEncuestas(context);
        rcCon = new rcConfig(context);
        rcEnc = new rcEncuestado(context);
        rcResp = new rcRespuestas(context);
        rcEns = new rcEncuestas(context);
        rcCon = new rcConfig(context);
        setHasOptionsMenu(true);
        //----
        if(titletoolbar.contentEquals("Borradores")){
            //---- Borradores
            Log.i(TAGD, titletoolbar);
            borradores();

        }
        if(titletoolbar.contentEquals("Enviados")){
            //---- Enviados
            Log.i(TAGS, titletoolbar);
            enviados();

        }
        if(titletoolbar.contentEquals("Bandeja de Salida")){
            //---- Bandeja
            Log.i(TAGO, titletoolbar);
            bandeja();

        }

        //---- Enviados

        //---- Bandeja Salida


        return root;

    }

    private void bandeja() {
        Log.i(TAGO,"Iniciando");
        pb = (ProgressBar) root.findViewById(R.id.pbar);
        int i;
        String wifiName = "NO ID";

        /*
        * Parte de la aplicación, es que solo se conecte vía WiFi y envíe los resultados.
        * */
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isWifiConn; // = networkInfo.isConnected();
        if (networkInfo != null) {
            isWifiConn = networkInfo.isConnected();
            wifiName = networkInfo.getExtraInfo();
        } else isWifiConn = false;
        String TAG = "Enviar Encuestas View";
        Log.d(TAG, "Wifi connected: " + isWifiConn);
        if(wifiName != null )
            Toast.makeText(context, "CONECTADO A WI-FI: " + wifiName, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, "No hay conexión WI-FI, necesitas activar tu WiFi ", Toast.LENGTH_SHORT).show();

        try {
            Log.i(TAGO,"rcsOpen");
            rcEnc.open();
            rcResp.open();
            rcEns.open();
            rcCon.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /*
        * Busco los borradores, y los agrego en un arreglo para cargarlos en la lista
        * Agregando los íconos de borrer y editar en la lista , nombre de la encuesta y el encuestado
        * */
        int index = rcResp.readTtlResp();
        sName = new String[index];
        String[] names = new String[index];
        sFullName = new String[index];
        sidEnc = new String[index];
        sEnc = new String[index];
        sDateSend = new String[index];
        iDiscard = new int[index];
        iDraft = new int[index];
        iSendNow = new int[index];
        iEdit = new int[index];
        String[] values = rcResp.getRespEdos();
        for ( i = 0; i< index; i++) {
            String[] idValues =  values[i].split(":");
            sName[i] = idValues[1];
            names[i] = idValues[0];
            sidEnc[i] = idValues[0];
            sDateSend[i] = "";
            sFullName[i] = rcEnc.getName(idValues[1]);
            iDiscard[i] = R.mipmap.ic_action_discard;
            if(isWifiConn)
                iSendNow[i] = R.mipmap.ic_action_send_now;
            else
                iSendNow[i] = 0;

            iDraft[i] = 0;

            iEdit[i] = 0;
        }
        Log.i(TAGO,"Added Icons");

        for ( i = 0; i< index; i++) {
            String eName = names[i];
            sEnc[i] = rcEns.getEncName(eName);
        }

        lv=(ListView) root.findViewById(R.id.listView);
        if(index > 0) {
            lv.setAdapter(new listAdapter(context,
                    sName,
                    sidEnc,
                    sFullName,
                    sEnc,
                    sDateSend,
                    iDiscard,
                    iDraft,
                    iSendNow,
                    iEdit));
        }
        Log.i(TAGO,"ListView");
        rcEnc.close();
        rcResp.close();
        rcEns.close();
        rcCon.close();

    }

    private void enviados() {
        Log.i(TAGO,"Enviados");
        pb = (ProgressBar) root.findViewById(R.id.pbar);
        int i;
        String wifiName = "NO ID";

        /*
        * Parte de la aplicación, es que solo se conecte vía WiFi y envíe los resultados.
        * */
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isWifiConn; // = networkInfo.isConnected();
        if (networkInfo != null) {
            isWifiConn = networkInfo.isConnected();
            wifiName = networkInfo.getExtraInfo();
        } else isWifiConn = false;
        String TAG = "Enviar Encuestas View";
        Log.d(TAG, "Wifi connected: " + isWifiConn);
        if(wifiName != null )
            Toast.makeText(context, "CONECTADO A WI-FI: " + wifiName, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, "No hay conexión WI-FI, necesitas activar tu WiFi ", Toast.LENGTH_SHORT).show();

        try {
            Log.i(TAGO,"rcsOpen");
            rcEns.open();
            rcCom.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        /*
        * Busco los borradores, y los agrego en un arreglo para cargarlos en la lista
        * Agregando los íconos de borrer y editar en la lista , nombre de la encuesta y el encuestado
        * */
        int index = rcCom.getTtlSend();
        sName = new String[index];
        String[] names = new String[index];
        String[] fullname = new String[index];
        sFullName = new String[index];
        sidEnc = new String[index];
        sDateSend = new String[index];
        sEnc = new String[index];
        iDiscard = new int[index];
        iDraft = new int[index];
        iSendNow = new int[index];
        iEdit = new int[index];
        String[] values = rcCom.readDraftEdos();
        for ( i = 0; i< index; i++) {
            String[] idValues =  values[i].split(":");
            sName[i] = idValues[1];
            names[i] = idValues[1];
            sidEnc[i] = idValues[0];
            sDateSend[i] = "Enviado el ";
            sFullName[i] =  rcEns.getEncNameEnviados(idValues[1]);
            iDiscard[i] = 0;
            iSendNow[i] = 0;
            iDraft[i] = 0;
            iEdit[i] = 0;
        }
        Log.i(TAGO, "Added Icons");

        for ( i = 0; i< index; i++) {
            String eName = names[i];
            sEnc[i] = rcEns.getEncName(eName);
        }

        lv=(ListView) root.findViewById(R.id.listView);
        if(index > 0) {
            lv.setAdapter(new listAdapter(context,
                    sName,
                    sidEnc,
                    sFullName,
                    sEnc,
                    sDateSend,
                    iDiscard,
                    iDraft,
                    iSendNow,
                    iEdit));
        }
        Log.i(TAGO, "ListView");
        rcCom.close();
    }

    private void borradores() {
        Log.i(TAGD,"Iniciando");

        try {
            rcEdo.open();
            rcEncs.open();
            rcCom.open();
            rcCon.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        /*
        * Busco los borradores, y los agrego en un arreglo para cargarlos en la lista
        * Agregando los íconos de borrer y editar en la lista , nombre de la encuesta y el encuestado
        * */
        int index = rcCom.readDrafts(rcCon.readID());
        sName = new String[index];
        sEncName = new String[index];
        sFullName = new String[index];
        sDateSend = new String[index];
        iDiscard = new int[index];
        iDraft = new int[index];
        iSendNow = new int[index];
        sidEnc = new String[index];
        iEdit = new int[index];
        String[] NameTest = rcCom.readDraftEdos();

        for (int i = 0; i< index; i++) {
            String[] values =  NameTest[i].split(":");
            sName[i] = values[0];
            sidEnc[i] = values[1];
            sFullName[i] = rcEdo.getName(values[0]);
            sEncName[i] = rcEncs.getEncName(values[1]);
            iDiscard[i] = 0;
            sDateSend[i] = "";
            iDraft[i] =  R.mipmap.ic_action_discard;
            iSendNow[i] = 0;
            iEdit[i] = R.mipmap.ic_action_edit;
        }
        Log.i(TAGD,"Icons added");



        rcEdo.close();
        rcEncs.close();
        rcCom.close();
        rcCon.close();

        ListView lv = (ListView) root.findViewById(R.id.listView);
        if(index > 0) {
            lv.setAdapter(new listAdapter(context,
                    sName,
                    sidEnc,
                    sFullName,
                    sEncName,
                    sDateSend,
                    iDiscard,
                    iDraft,
                    iSendNow,
                    iEdit));
        }else
        {Toast.makeText(context,"No hay encuestas guardadas",Toast.LENGTH_SHORT).show(); }
        Log.i(TAGD,"ListView");

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu, inflater);

        if(getActivity().getTitle().toString().contentEquals("Borradores")){
            inflater.inflate(R.menu.menu_borradores, menu);
        }
        if(getActivity().getTitle().toString().contentEquals("Enviados")){
            inflater.inflate(R.menu.menu_flistados, menu);
        }
        if(getActivity().getTitle().toString().contentEquals("Bandeja de Salida")){
            inflater.inflate(R.menu.menu_flistados, menu);
        }


       // inflater.inflate(R.menu.menu_encuestas, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_sync:
                if(getActivity().getTitle().toString().contentEquals("Borradores")){
                    item.setVisible(false);
                }
                Log.i(TAGD, "Clicked SendAll");
            case R.id.sendAll:
                Log.i(TAGD, "Clicked SendAll");
//                break;
            case R.id.deleteAll:
                Log.i(TAGD, "CLICKED Delete All");
//                break;
            case R.id.delAll:
                Log.i(TAGD, "CLICKED Delete All");

            default:
                return super.onOptionsItemSelected(item);
        }
//        return super.onOptionsItemSelected(item);
    }

}
