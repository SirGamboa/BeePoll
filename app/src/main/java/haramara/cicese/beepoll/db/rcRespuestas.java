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
public class rcRespuestas {
    private final dbRespuestas dbTable;
    private SQLiteDatabase database;
    private final String TAG = "rcRespuestas";
    Cursor c;

    /* Metodos de inicialización de las tablas */
    public rcRespuestas(Context context){
        dbTable = new dbRespuestas(context);
    }
    public void open() throws SQLException{ database = dbTable.getWritableDatabase(); }
    public void close(){ database.close(); }
    public void read() { database = dbTable.getReadableDatabase(); }
    /* Inicialización tablas */
    /*  ----- metodos ----*/
    @SuppressWarnings("UnusedReturnValue")
    public long  addRespuestas(ContentValues cv){
        return database.insert(dbTable.TABLE_NAME, null, cv);
    }

    public Cursor readAns(String IDEnc, String IDE){
        String sql = "select "
                +dbTable.COLUMN_NAME_ENCUESTADO_ID + " , "
                + dbTable.COLUMN_NAME_IDPREGTIPO + " , "
                + dbTable.COLUMN_NAME_ENCUESTA_ID + " , "
                + dbTable.COLUMN_NAME_PREGUNTA_ID + " , "
                + dbTable.COLUMN_NAME_RESPUESTA +" , "
                + dbTable.COLUMN_NAME_IDVAL
                + " from "
                + dbTable.TABLE_NAME
                + " where "
                + dbTable.COLUMN_NAME_ENCUESTADO_ID
                + " = '" + IDEnc +"'"
                + " and "
                + dbTable.COLUMN_NAME_ENCUESTA_ID+ " = "
                + IDE;
        Log.i(TAG, sql);
        return database.rawQuery(sql, null);

    }

    public String[] getRespEdos(){
        String[] data; int i=0;
        Cursor c;
        String sql = "select distinct id_encuesta, id_encuestado from " + dbTable.TABLE_NAME + " where status = 1"; //
        c = database.rawQuery(sql, null);
        data = new String[c.getCount()];
        if(c.getCount()>0) {
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    data[i] = c.getString(0) + ":"+ c.getString(1);
                    i++;
                    c.moveToNext();
                }
                c.close();
        }
        return  data;
    }

    public boolean check(String id_encuestado, int id_encuesta, int ttl) {
        Cursor c;
        int  ttl2;
        boolean answer = false;
        if(id_encuestado != null && id_encuesta != 0) {

            String sql = "select id from " + dbTable.TABLE_NAME + " where id_encuestado = '" + id_encuestado + "'  and id_encuesta = " + id_encuesta;
            // select id from survey_respuestas where id_encuestado = 'ROMR19810421H' and id_encuesta = 1
            Log.i(TAG, sql);
            c = database.rawQuery(sql, null);
            ttl2 = c.getCount();
            c.close();
            if (ttl2 < ttl) {
                //sí existe encuesta
                answer = false;
            }
            if(ttl == ttl2)
                answer = true;
        }
//        if (c == null) throw new AssertionError();
//        else   c.close();
        return answer;
    }
    // metodo para obtener el total de respuestas contestadas del usuario
    public int getTtlResp(String id_encuestado, String id_Edor) {
        Cursor c;
        int x;
        String sql = "select id from " + dbTable.TABLE_NAME + " where id_encuestado = '"
                + id_encuestado + "' and "
                + dbTable.COLUMN_NAME_ENCUESTADOR_ID
                + " = " + id_Edor;//  and id_encuesta = " + id_encuesta;
        c = database.rawQuery(sql,null);
        if(c != null){
            x = c.getCount();
        }else{
            x = 0;
        }
        assert c != null;
        c.close();
        return x;
    }

    public boolean status(String sTitle, int sId) {
        Cursor c; boolean value = false;
        String sql = "update   "
                + dbTable.TABLE_NAME
                + " set status = '0' where "
                + dbTable.COLUMN_NAME_ENCUESTADO_ID
                + " = '"
                + sTitle
                + "'  and "
                + dbTable.COLUMN_NAME_ENCUESTA_ID
                + " =  '"
                + String.valueOf(sId)
                + "' ";
        c = database.rawQuery(sql, null);
        c.moveToFirst();

        if(c.getCount() > 1)  { c.close(); value = true; }
        return value;

    }

    public int readTtlResp() {
        Cursor c; int i;
        String sql = "select distinct "
                + dbTable.COLUMN_NAME_ENCUESTA_ID
                + " , "
                + dbTable.COLUMN_NAME_ENCUESTADO_ID
                +" from "
                +dbTable.TABLE_NAME
                + " where status = 1";
        c = database.rawQuery(sql,null);
        i =  c.getCount();
        c.close();
        return i;
    }
    public int readTtlEnviados() {
        Cursor c; int i;
        String sql = "select distinct "
                + dbTable.COLUMN_NAME_ENCUESTA_ID
                + " , "
                + dbTable.COLUMN_NAME_ENCUESTADO_ID
                +" from "
                +dbTable.TABLE_NAME
                + " where status = 0";
        c = database.rawQuery(sql,null);
        i =  c.getCount();
        c.close();
        return i;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean updateSurvey(int id_encuesta, String id_encuestado) {
        boolean value = false;
        String sql = " update "
                + dbTable.TABLE_NAME
                + " set "
                + dbTable.COLUMN_NAME_STATUS
                + " = 1 where "
                + dbTable.COLUMN_NAME_ENCUESTADO_ID
                + " = '"
                + id_encuestado
                +"' and "
                + dbTable.COLUMN_NAME_ENCUESTA_ID
                + " = '"
                + id_encuesta
                + "' ";
        Cursor c = database.rawQuery(sql, null);
        c.moveToFirst();
       if(c.getCount()>0){ c.close(); value = true; }
        return value;
    }
}
