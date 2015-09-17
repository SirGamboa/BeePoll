package haramara.cicese.beepoll.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

/**
 * Created by diseno on 5/27/15. for Surbee_Haramara
 */
public class rcEncuestas {
    private final dbEncuestas dbTable;
    private SQLiteDatabase database;
    public static Context mContext;

    public rcEncuestas(Context context){
        dbTable = new dbEncuestas(context);
    }
    @SuppressWarnings("UnusedReturnValue")
    public  long addEncuesta(ContentValues cv){
        return database.insert(dbTable.TABLE_NAME, null, cv);
    }
    public void open()throws SQLException {
        database = dbTable.getWritableDatabase();
    }
    public void close(){
        database.close();
    }
    public void read() {
        database = dbTable.getReadableDatabase();
    }

    public String[] getHeaders(String EdoID) { // id encuesta
        String[] etiquetas;
        String sqlHeaders = "select distinct e.etiqueta from survey_relacion as a\n" +
                "        join survey_encuestas as e on a.id_encuesta = e.id_encuesta\n" +
                "        where a.id_encuestado = ?";// + EdoID;
        int i=0;

        Cursor c = database.rawQuery(sqlHeaders, new String[]{EdoID});
        etiquetas = new String[c.getCount()];
        c.moveToFirst();
        //noinspection ConstantConditions
        if (c != null) {
            while (!c.isAfterLast()) {
                etiquetas[i] = c.getString(0);
                i++;
                c.moveToNext();
            }
            c.close();
        } else {
            etiquetas[i] = "NO TITLE";
            //noinspection UnusedAssignment
            i++;
        }
        c.close();
        return etiquetas;
    }
    public String getSurveys(String EncID, String etiqueta){
        String result = "@"; // = new String[]{};
        String sqlSurvey;
        Cursor c;
        @SuppressWarnings("UnusedAssignment") int i=0;
        sqlSurvey = "select "
                + dbTable.COLUMN_NAME_NOMBRE
                + " from "
                + dbTable.TABLE_NAME
                + " where "
                + dbTable.COLUMN_NAME_ETIQUETA
                +" = ? "
                + " and "
                + dbTable.COLUMN_NAME_IDENCUESTA
                + " = ?";
        c = database.rawQuery(sqlSurvey,new String[]{etiqueta, EncID});
        c.moveToFirst();
        if(!c.isAfterLast()){
            result = c.getString(0);

        }
        c.close();
        return result;
    }

    public int getListEncsLenght(String s) {
        @SuppressWarnings("UnusedAssignment") ContentValues cv = new ContentValues();

        String sql = "select "+
                dbTable.COLUMN_NAME_ID +
                " from " +
                dbTable.TABLE_NAME + "  where " +
                dbTable.COLUMN_NAME_IDEDO +
                " = " + s;
        Cursor c = database.rawQuery(sql,null);
        int i = c.getCount() + 1;
        c.close();
        return i;
    }

    public ContentValues getInfoEncs(String encInfo) {
        ContentValues cv = new ContentValues();
        String sql = "select nombre, instrucciones, descripcion, id_encuesta, datos_edo from " + dbTable.TABLE_NAME + "  where nombre = '" + encInfo + "'";
        Cursor c = database.rawQuery(sql,null);
        if(c!= null){
            c.moveToFirst();
            while(!c.isAfterLast()){
                cv.put(dbTable.COLUMN_NAME_NOMBRE,c.getString(0));
                cv.put(dbTable.COLUMN_NAME_INSTRUCCIONES, c.getString(1));
                cv.put(dbTable.COLUMN_NAME_DESCRIPCION, c.getString(2));
                cv.put(dbTable.COLUMN_NAME_IDENCUESTA, c.getString(3));
                cv.put(dbTable.COLUMN_NAME_EDO, c.getString(4));
                c.moveToNext();
            }
            c.close();
        }else{
            cv.put(dbTable.COLUMN_NAME_NOMBRE,"NO TITLE");
            cv.put(dbTable.COLUMN_NAME_INSTRUCCIONES, "NO INSTRUCTIONS");
            cv.put(dbTable.COLUMN_NAME_DESCRIPCION, "NO DESCRIPTION");
            cv.put(dbTable.COLUMN_NAME_IDENCUESTA, "0");
            cv.put(dbTable.COLUMN_NAME_EDO, "000001");
        }
        return cv;

    }

    public String getEncName(String s){
        Cursor c;
        String sql = "select "
                + dbTable.COLUMN_NAME_NOMBRE
                +" from "
                +dbTable.TABLE_NAME
                + "  where "
                + dbTable.COLUMN_NAME_IDENCUESTA
                +"= '"+ s +"'";
        c = database.rawQuery(sql,null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            s = c.getString(0);
            c.moveToNext();
        }
        c.close();
        return s;
    }

    public void deleteEncs() {
        database.delete(dbTable.TABLE_NAME, null, null);
    }

    public int getEncs() {
        String sql = "select id from "+dbTable.TABLE_NAME;
        Cursor c = database.rawQuery(sql, null);
        int i = c.getCount();
        c.close();
        return i;
    }
}
