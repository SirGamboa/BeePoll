package haramara.cicese.beepoll.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

/**
 * Created by diseno on 7/23/15. for Surbee_Haramara
 */
public class rcDataEnc {
    private final dbDataEnc dbTable;
    private SQLiteDatabase database;

    public rcDataEnc(Context context){
        dbTable = new dbDataEnc(context);
    }
    /*              Abre DB                           */
    public void open()  throws SQLException{
        database = dbTable.getWritableDatabase();
    }
    /*                Cierra DB                         */
    public void close(){
        database.close();
    }
    /*                    Lee ( Abre ) DB                     */
    public void read() {
        database = dbTable.getReadableDatabase();
    }
    /*      read data and return numEnvios      */
    public int readNumSend(int idEnc, int idEdor){
        Cursor c;
        String i= "0";
        int returnValue = 0;
        String sql = "select "
                + dbTable.COLUMN_NAME_ENVIOS
                + " from "
                + dbTable.TABLE_NAME
                + " where "
                + dbTable.COLUMN_NAME_IDENCUESTA
                + " = '"
                + idEnc
                +"' and  "
                + dbTable.COLUMN_NAME_IDENCUESTADOR
                + " = '"
                + idEdor
                + "' "
                ;
        c = database.rawQuery(sql, null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            i = c.getString(0);
            c.moveToNext();
        }
        if(i != null)
            returnValue = Integer.valueOf(i);
        c.close();
        return returnValue;
//        return i;
    }
    public int readNumResp(int idEnc, int idEdor){
        Cursor c;
        String i= "0";
        int returnValue = 0;
        String sql = "select "
                + dbTable.COLUMN_NAME_APLICACIONES
                + " from "
                + dbTable.TABLE_NAME
                + " where "
                + dbTable.COLUMN_NAME_IDENCUESTA
                + " = '"
                + idEnc
                +"' and  "
                + dbTable.COLUMN_NAME_IDENCUESTADOR
                + " = '"
                + idEdor
                + "' "
                ;
        c = database.rawQuery(sql, null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            i = c.getString(0);
            c.moveToNext();
        }
        if(i != null)
            returnValue = Integer.valueOf(i);
        c.close();
        return returnValue;
    }



    public void updateNumResp(int i, int id_encuesta, String idEncuestador) {
        @SuppressWarnings("UnusedAssignment") boolean r = false;
        ContentValues cv = new ContentValues();
        cv.put(dbTable.COLUMN_NAME_IDENCUESTADOR, idEncuestador);
        cv.put(dbTable.COLUMN_NAME_IDENCUESTA, String.valueOf(id_encuesta));
        cv.put(dbTable.COLUMN_NAME_APLICACIONES, String.valueOf(i));
        String whereClause =  dbTable.COLUMN_NAME_IDENCUESTA + " = ? and "  +  dbTable.COLUMN_NAME_IDENCUESTADOR + " = ? ";
        database.update(dbTable.TABLE_NAME, cv, whereClause, new String[]{String.valueOf(id_encuesta), idEncuestador});
    }
    public void updateNumSend(int i, int id_encuesta, String idEncuestador) {
        @SuppressWarnings("UnusedAssignment") boolean r = false;
        ContentValues cv = new ContentValues();
        cv.put(dbTable.COLUMN_NAME_IDENCUESTADOR, idEncuestador);
        cv.put(dbTable.COLUMN_NAME_IDENCUESTA, String.valueOf(id_encuesta));
        cv.put(dbTable.COLUMN_NAME_ENVIOS, String.valueOf(i));
        String whereClause =  dbTable.COLUMN_NAME_IDENCUESTA + " = ? and "  +  dbTable.COLUMN_NAME_IDENCUESTADOR + " = ? ";
        database.update(dbTable.TABLE_NAME, cv,whereClause,new String[]{String.valueOf(id_encuesta), idEncuestador});
    }

    public boolean check(int i, int id_encuesta, String idEncuestador) {
        Cursor c; //int i= 0;
        boolean value = false;
        String sql = "select "
                + dbTable.COLUMN_NAME_ID
                + " from "
                + dbTable.TABLE_NAME
                + " where "
                + dbTable.COLUMN_NAME_IDENCUESTA
                + " = '"
                + String.valueOf(id_encuesta)
                + "'  and "
                + dbTable.COLUMN_NAME_IDENCUESTADOR
                + " = '"
                + idEncuestador
                + "' ";
        c = database.rawQuery(sql, null);
        c.moveToFirst();
        if( c.getCount()> 0)
            value = true;
        c.close();
        return value;
    }

    public void addNumResp(int i, int id_encuesta, String idEncuestador) {
        ContentValues cv = new ContentValues();
        cv.put(dbTable.COLUMN_NAME_APLICACIONES,String.valueOf(i));
        cv.put(dbTable.COLUMN_NAME_IDENCUESTA, String.valueOf(id_encuesta));
        cv.put(dbTable.COLUMN_NAME_IDENCUESTADOR, idEncuestador);
        database.insert(dbTable.TABLE_NAME, null, cv);
    }
//    public void addNumSend(int i, int id_encuesta, String idEncuestador) {
//        ContentValues cv = new ContentValues();
//        cv.put(dbTable.COLUMN_NAME_ENVIOS,String.valueOf(i));
//        cv.put(dbTable.COLUMN_NAME_IDENCUESTA, String.valueOf(id_encuesta));
//        cv.put(dbTable.COLUMN_NAME_IDENCUESTADOR, idEncuestador);
//        database.insert(dbTable.TABLE_NAME,null, cv);
//    }

    public void clearDraft(int id_encuesta, String idEncuestador) {
        String sql = "delete from "
                + dbTable.TABLE_NAME
                + " where "
                +dbTable.COLUMN_NAME_IDENCUESTA
                + " = '"
                +String.valueOf(id_encuesta)
                +"' and "
                + dbTable.COLUMN_NAME_IDENCUESTADOR
                +" = '"+idEncuestador
                +"'";
        database.rawQuery(sql,null);
    }
}
