package haramara.cicese.beepoll.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;

/**
 * Created by diseno on 5/27/15. for Surbee_Haramara
 */
public class rcPreguntas {
    private final dbPreguntas dbTable;
    private SQLiteDatabase database;
    private final String TAG = "rcPreguntas";

    public rcPreguntas(Context context){
        dbTable = new dbPreguntas(context);
    }
    public ContentValues readPreguntas(int ID, int cont){
        ContentValues cv = new ContentValues();
        int i = 0; //count para lectura de datos
        String sID = Integer.toString(ID);
        String sql = "select id_pregunta, idPregTipo, descripcion, tipo, idImage from " + dbTable.TABLE_NAME +" where "+ dbTable.COLUMN_NAME_IDENCUESTA +" = "+ sID;
        String TAG = "rcPreguntas";
        Log.i(TAG, sql);
        Cursor c = database.rawQuery(sql, null);
        c.moveToFirst();
//        if(c != null) {
            while(!c.isAfterLast()){
                if (i < cont) {
                    cv.put(dbTable.COLUMN_NAME_PREG_ID, c.getString(0));
                    cv.put(dbTable.COLUMN_NAME_IDPREGTIPO, c.getString(1));
                    cv.put(dbTable.COLUMN_NAME_DESCRIPCION, c.getString(2));
                    cv.put(dbTable.COLUMN_NAME_TIPO, c.getString(3));
                    cv.put(dbTable.COLUMN_NAME_IMAGE, c.getString(4));
                    i++;
                }
                c.moveToNext();
            }
            c.close();
//        }
        Log.i(TAG,cv.toString());
        return  cv;
    }
    @SuppressWarnings("UnusedReturnValue")
    public  long addPreguntas(ContentValues cv){

        return database.insert(dbTable.TABLE_NAME, null, cv);
    }

    public int preguntasTotal(String sIDEnc){
        Cursor c;
        String sql="select id_pregunta from "+ dbTable.TABLE_NAME + " where id_encuesta =  '" + sIDEnc + "'";
        c = database.rawQuery(sql,null);
        int i = c.getCount();
        c.close();
        return i;//c.getCount();
    }
    public void open() throws SQLException{
        database = dbTable.getWritableDatabase();
    }
    public void close(){
        database.close();
    }
    public void read() {
        database = dbTable.getReadableDatabase();
    }

    public boolean checkPreguntas(ContentValues cvPregs) {
        boolean r = false;
        String whereClause =  dbTable.COLUMN_NAME_PREG_ID + " = ? and "  +  dbTable.COLUMN_NAME_IDENCUESTA + " = ? ";
        if(database.update(dbTable.TABLE_NAME, cvPregs,whereClause,new String[]{cvPregs.get(dbTable.COLUMN_NAME_PREG_ID).toString(), cvPregs.get(dbTable.COLUMN_NAME_IDENCUESTA).toString()})>0 )
            r = true;
        return r;
    }

    public String getPregunta(int loop, int id_encuesta) {
        Cursor c;
        String value="0";
        String sql = "select "
                + dbTable.COLUMN_NAME_DESCRIPCION
                + " , "
                + dbTable.COLUMN_NAME_PESO
                + " , "
                + dbTable.COLUMN_NAME_PREG_IDVAL
                + " from "
                + dbTable.TABLE_NAME
                + " where "
                + dbTable.COLUMN_NAME_PREG_ID
                + " = '"
                + loop
                + "'  and  "
                + dbTable.COLUMN_NAME_IDENCUESTA
                + " = '"
                + id_encuesta
                +"'";
        c = database.rawQuery(sql, null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            value = c.getString(0) + ":"+ c.getString(1)+ ":"+ c.getString(2);
            c.moveToNext();
        }
        c.close();
        return value;
    }

    public void deletePregs() {
        database.delete(dbTable.TABLE_NAME, null, null);
    }

    public String getPregVal(int loop, int id_encuesta) {
        Cursor c; int index;
        String sql = "select  "+
                dbTable.COLUMN_NAME_PREGVAL+
                "  from " +
                dbTable.TABLE_NAME +
                " where "+
                dbTable.COLUMN_NAME_IDENCUESTA+
                " = "+
                id_encuesta +
                " and " +
                dbTable.COLUMN_NAME_PREG_ID +
                " = "+
                loop;



        c = database.rawQuery(sql, null);
        index = c.getCount();
        c.moveToFirst();
        Log.i(TAG + " Index:", String.valueOf(index));
        String data = "Sin Reactivos";// = new String[index];

        for(int i=0; i<index; i++){
            data = c.getString(0);
//            Log.println(1, TAG, c.toString());
//            Log.i(TAG+"ID: ",(c.getString(0)==null)? "Null": c.getString(0));
//            Log.i(TAG+"ID Enc: ",(c.getString(1)==null)? "Null": c.getString(1));
//            Log.i(TAG+"ID PReg ",(c.getString(2)==null)? "Null": c.getString(2));
//            Log.i(TAG+"ID PregTipo: ",(c.getString(3)==null)? "Null": c.getString(3));
//            Log.i(TAG+"Desc: ",(c.getString(4)==null)? "Null": c.getString(4));
//            Log.i(TAG+"Valoracion: ",(c.getString(5)==null)? "Null": c.getString(5));
//            Log.i(TAG+"Peso: ",(c.getString(6)==null)? "Null": c.getString(6));
            c.moveToNext();
        }


        c.close();
        return data;
    }

    public String[] getPhotos() {
        Cursor c;
        String sql = "select idImage from " + dbTable.TABLE_NAME;
        c = database.rawQuery(sql,null);
        c.moveToFirst();
        String[] data = new String[c.getCount()];
        int i = 0;
        while(!c.isAfterLast()){
            data[i] = c.getString(0); // + ":"+ c.getString(1)+ ":"+ c.getString(2);
            c.moveToNext();
            i++;
        }
        c.close();

        return  data;
    }
}
