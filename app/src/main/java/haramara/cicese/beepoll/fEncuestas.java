package haramara.cicese.beepoll;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.Toast;


import org.json.JSONException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import haramara.cicese.beepoll.db.rcConfig;
import haramara.cicese.beepoll.db.rcEncuestas;
import haramara.cicese.beepoll.db.rcRelacion;

import  android.app.Activity.*;

/**
 * Created by diseno on 9/18/15. for BeePoll
 */
public class fEncuestas extends Fragment {
    private ViewGroup root;
    private static final String TAG = "FragEncuestas";
    //---
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    private final int REQUEST_CODE = 0;
    private Context cntx;
    private wsData ws;
    private rcConfig rcCon;
    private rcEncuestas rcEnc;
    private String email; private String IDUser="0";
    private boolean flagEncs = false;
    private boolean flagEdo = false;
    ContentValues cv;
    private ProgressBar pb;
    //---
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        root = (ViewGroup) inflater.inflate(R.layout.encuestas_expandiblelistview, null);
        cntx = container.getContext();
        Log.i(TAG, "Entre onCreateView Encuestas Portada");
        email = "";
        pb = (ProgressBar) root.findViewById(R.id.pbar);
        pb.setVisibility(View.INVISIBLE);
        setHasOptionsMenu(true);
        //--- leer existe encuestas para usuario
        try {
            rcCon = new rcConfig(cntx.getApplicationContext());
            rcEnc = new rcEncuestas(cntx.getApplicationContext());
            rcCon.open();
            rcEnc.open();
            if(rcCon.checkUserExist()) {
                flagEncs = true;
                IDUser = rcCon.readID();
                email = rcCon.readUserMail();
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }


        if(flagEncs){
            //checar si existen encuestas
            if(rcEnc.getListEncsLenght(IDUser)> 0) {
                rcEnc.close();
//                ListEncuestas();

            }
            Log.i(TAG, "FlagEncuestas ListEncuestas");
        }
        rcEnc.close();
//        //--- Button
//        Button btnSync = (Button) root.findViewById(R.id.btnSync);
//        btnSync.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Log.i(TAG, "CLICKED SYNC!");
//                //borrar encuestas
////                deleteDatos();
//                //--- WS Load JSON API
//                ws = new wsData();
//                if(flagEncs) {
//                    pb.setVisibility(View.VISIBLE);
//                    pb.setProgress(50);
//                    //noinspection unchecked
//                    new loadWS().execute();
//                }
//                else Log.i(TAG, "EMPTY email");
//
//
//            }
//        });

     return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_encuestas, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_sync:
                Log.i(TAG, "CLICKED SYNC! WSData" );

                //--- WS Load JSON API
                ws = new wsData();
                if(flagEncs) {
                    pb.setVisibility(View.VISIBLE);
                    pb.setProgress(50);
                    //noinspection unchecked
                    new loadWS().execute();
                }
                else Log.i(TAG, "EMPTY email");


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void ListEncuestas(){
        /*
        * Método para cargar en la lista expandible (ExpandableListView) los proyectos y sus
        * encuestas correspondientes, de acuerdo al usuario registrado en la aplicación móvil.
        * */
        ExpandableListView expListView = (ExpandableListView) root.findViewById(R.id.lvExp);

        // preparing list data
        try {
            prepareListData();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ExpandableListAdapter listAdapter = new MyExpandableListAdapter(cntx, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Log.i(TAG, "CLICK " + listDataChild.get(
                        listDataHeader.get(groupPosition)).get(
                        childPosition));
                Log.i(TAG, "Click Get:" + listDataHeader.get(groupPosition));
                Intent iSelected = new Intent(cntx, encuesta_activity.class);
                iSelected.putExtra("ENC", listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition));
                //iSelected.putExtra("CP",childPosition);
                startActivityForResult(iSelected, REQUEST_CODE);

                return false;

            }
        });
    }

    private void prepareListData() throws SQLException {
        @SuppressWarnings("UnusedAssignment") int j = 0;
        String[] adata;
        @SuppressWarnings("UnusedAssignment") ContentValues cv = new ContentValues();
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        List<String> Encuestas;
        rcEnc.open();
        // cargar relation
        rcRelacion rcRel = new rcRelacion(cntx);
        rcRel.open();
        adata = rcRel.readEncs(IDUser);
        //----
        int cvLenght = adata.length + 1 ;
        @SuppressWarnings("UnusedAssignment") String[] cvHeaders = new String[cvLenght];
        // Encabezados encuestas

        int i = 0;
        String[] tempString; // = "";
        tempString = rcEnc.getHeaders(IDUser);
        // Adding child data
        //leer etiquetas
        while (i < (tempString.length )) {
            listDataHeader.add(String.valueOf(tempString[i]));
            i++;
        }

        i = 0; @SuppressWarnings("UnusedAssignment") int k, l=1;
        String[] surveyTemp = rcRel.readEncs(IDUser);
        String surveyTempResp;
//      agregar encuestas a etiquetas
        while (i < tempString.length){
            Encuestas = new ArrayList<>();
            k=0;
            while(k  < adata.length){
                // id encuesta, etiquetas
                surveyTempResp = rcEnc.getSurveys(surveyTemp[k],tempString[i]);
                if(surveyTempResp.compareTo("@")!= 0){
                    // no es @
                    Encuestas.add(String.valueOf(surveyTempResp));
                }

                k++;
            }
            listDataChild.put(listDataHeader.get(i), Encuestas); // Header, Child data
            i++;
        }
        rcRel.close();
        rcEnc.close();
    }


    private class loadWS extends AsyncTask {

        @Override
        protected Object doInBackground(Object... params) {
            try {
                Log.i(TAG,"doInBackground....");
                flagEdo =  ws.webServiceSearch(email, cntx.getApplicationContext());
                ws.relationEncEdores(email, cntx);
                Log.i(TAG, "onPostExecute....");

                            /* ***************** */
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pb.setVisibility(View.GONE);
                        if (flagEdo) {
                            //--- Load info to Expandible list
                            ListEncuestas();
                        } else
                            Toast.makeText(cntx, "No hay encuestas disponible para el usuario: " + email, Toast.LENGTH_SHORT).show();
                    }
                });
                /* ***************** */
                rcCon.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
//            ListEncuestas();
            return null;
        }
    }
}
