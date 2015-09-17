package haramara.cicese.beepoll.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

/**
 * Created by diseno on 5/27/15. for Surbee_Haramara
 */
public class rcEncuestado  {
    private final dbEncuestado dbTable;
    private SQLiteDatabase database;

    public rcEncuestado(Context context){
        dbTable = new dbEncuestado(context);
    }
    /*                                         */
    public  long addEncuesta(ContentValues cv){
        return database.insert(dbTable.TABLE_NAME, null, cv);
    }
    /*                    Agrega Encuestado en base de datos del dispo.                     */
    @SuppressWarnings("UnusedReturnValue")
    public long addEncuestado(ContentValues cv){
        return database.insert(dbTable.TABLE_NAME, null, cv);
    }
    /*              Abre DB                           */
    public void open()throws SQLException {
        database = dbTable.getWritableDatabase();
    }
    /*                Cierra DB                         */
    public void close(){
        database.close();
    }
    /*                    Lee ( Abre ) DB                     */
    public void read() {  database = dbTable.getReadableDatabase();}
    /*                   Obtiene el último ID disponinble de Encuestados en DB                      */
    public int getID(){
        Cursor c;
        int id = 1;
        c = database.rawQuery("select id from "+dbTable.TABLE_NAME, null);
        if(c.getCount() > 0){
            c.moveToFirst();
            while(c.isAfterLast()){
                id = Integer.valueOf(c.getString(0));
                c.moveToNext();
            }
        }
        c.close();
        return id;
    }
    /*                Lee la información del encuestado ( clave e ID )                         */
    public ContentValues readEncuestado() {
        Cursor c;
        ContentValues data = new ContentValues();
        String sql = "select clave, id from " + dbTable.TABLE_NAME;
        c = database.rawQuery(sql, null);
        if(c.getCount()>0) {
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    data.put(dbTable.COLUMN_NAME_CLAVE, c.getString(0));
                    data.put(dbTable.COLUMN_NAME_ID, c.getString(1));
                    c.moveToNext();
                }
                c.close();

        }
        c.close();
        return data;
    }
    /*          Revisa si existe un usuario a encuestar (regresa números de encuestados)            */
    public boolean checkEncuestado() {
        Cursor c;
        boolean value = false;
        String sql = "select clave, id from " + dbTable.TABLE_NAME;
        c = database.rawQuery(sql, null);
        c.moveToFirst();
        if(c.getCount() > 0){
            value = true;
        }
        c.close();
        return value;
    }

    public String getClave(){
        Cursor c;
        @SuppressWarnings("UnusedAssignment") String value = "0";
        String sql = "select clave from " + dbTable.TABLE_NAME+ " order by id desc";
        c = database.rawQuery(sql, null);
        c.moveToFirst();
        if(c.getCount() > 0){
            value = c.getString(0);
        }else value="0";
        c.close();
        return value;
    }
    /*             Genera y regresa la clave (AABB1234)                          */
    public String GenClave (String nombres, String apellidos, String fecha_nacimiento, int sexo) {

        String sexoString, fecha;
        String[] fDate;
        if(sexo == 1) {
            sexoString="F";
        }
        else {
            sexoString="M";
        }

        if(fecha_nacimiento.contains("-")){
            fDate = fecha_nacimiento.split("-");
            fecha = fDate[0]+fDate[1]+fDate[2];
        }else
        fecha = fecha_nacimiento;

        String nombresString;
        int spaceIndex = nombres.indexOf(" ");
        if(spaceIndex == -1) {
            nombresString = nombres.substring(0,2);
        }
        else {
                nombresString = nombres.substring(0,1);// + nombres.substring(spaceIndex + 1, spaceIndex + 2);
        }
        String apellidosString;
        spaceIndex = apellidos.indexOf(" ");
        if(spaceIndex == -1) {
            apellidosString = apellidos.substring(0, 2);
        }
        else {
            apellidosString = apellidos.substring(0,1) + apellidos.substring(spaceIndex + 1, spaceIndex + 2);
        }
        return (
                nombresString +
                        apellidosString +
                        fecha+
                        sexoString
        ).toUpperCase();

    }

    public String getName(String id_edo){
        String data= "", FN, LNP, LNM;
        Cursor c;
        String sql = "select nombres, apellido_paterno, apellido_materno from " + dbTable.TABLE_NAME + " where clave = '" + id_edo + "'";
        c = database.rawQuery(sql, null);
        if (c != null) {
            c.moveToFirst();
            while (!c.isAfterLast()) {
                FN = (c.getString(0).compareTo("NN")!=0)?c.getString(0):" ";
                LNP = (c.getString(1).compareTo("LN")!=0)?c.getString(1):" ";
                LNM = (c.getString(2).compareTo("LN")!=0)?c.getString(2):" ";

                data =  FN + LNP + LNM;
                c.moveToNext();
            }
            c.close();

        }
        if(data.compareTo("   ")== 0)
            data = "Sin datos personales";
        return  data;

    }
    // obtiene la clave de los encuestados
    public String[] getEdos() {
        String[] data; int i=0;
        Cursor c;
        @SuppressWarnings("UnusedAssignment") boolean value = false;
        String sql = "select clave from " + dbTable.TABLE_NAME; //
        c = database.rawQuery(sql, null);
        data = new String[c.getCount()];
        if(c.getCount()>0) {
                c.moveToFirst();
                while (!c.isAfterLast()) {
                    data[i] = c.getString(0); ///
                    i++;
                    c.moveToNext();
                }
                c.close();
        }
        return  data;
    }
    public String getData(String idEdo){
        Cursor c;
        String name=null, ap = null, am=null, dob=null, sx=null;
        String sqlData = "select "
                + dbTable.COLUMN_NAME_NOMBRES
                + " , "
                + dbTable.COLUMN_NAME_APELLIDO_PATERNO
                + " , "
                + dbTable.COLUMN_NAME_APELLIDO_MATERNO
                + " , "
                + dbTable.COLUMN_NAME_FECHA_NACIMIENTO
                + " , "
                + dbTable.COLUMN_NAME_SEXO
                + " from "
                + dbTable.TABLE_NAME
                + " where "
                +dbTable.COLUMN_NAME_CLAVE
                +" = '"
                + idEdo
                +"'";
        c = database.rawQuery(sqlData, null);
        c.moveToFirst();
        while(!c.isAfterLast()){
            name = c.getString(0);
            ap = c.getString(1);
            am = c.getString(2);
            dob = c.getString(3);
            sx = c.getString(4);
            c.moveToNext();
        }
        String data = name;
        if (name != null) {
            if(name.contains("Anónimo"))
                data = name;
            else
                data = name +" "+ ap +" "+am +" "+dob +" "+sx ;
        }
        c.close();
        return data;
    }

    public void deleteEdos() {
        database.delete(dbTable.TABLE_NAME,null,null);
    }
}
