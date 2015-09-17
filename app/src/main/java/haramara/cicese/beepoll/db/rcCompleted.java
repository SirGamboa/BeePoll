package haramara.cicese.beepoll.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;

/**
 * Created by diseno on 9/14/15. for BeePoll
 */
public class rcCompleted {
    private final dbCompleted dbTable;
    private SQLiteDatabase database;
    private final String TAG = this.getClass().getName();

    public rcCompleted(Context context){
        dbTable = new dbCompleted(context);
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


    /********** public methods for RCCOMPLETED *****************/
    public void draft(String[] sborrador) {
        ContentValues cv = new ContentValues();
        cv.put(dbTable.COLUMN_NAME_IDEDO,sborrador[0]);
        cv.put(dbTable.COLUMN_NAME_IDENC,sborrador[1]);
        cv.put(dbTable.COLUMN_NAME_IDPREG, sborrador[2]);
        cv.put(dbTable.COLUMN_NAME_STATUS, sborrador[3]);
        cv.put(dbTable.COLUMN_NAME_IDEDR, sborrador[4]);
        database.insert(dbTable.TABLE_NAME,null, cv);
    }

    public int readDrafts(String idEdor) {
        Cursor c;
        String sql = "select id from "+dbTable.TABLE_NAME + " where status = '1' and "+dbTable.COLUMN_NAME_IDEDR +" = '"+idEdor+"'";
        c = database.rawQuery(sql,null);
        int i = c.getCount();
        c.close();
        return i ;//c.getCount();
    }

    public String[] readDraftEdos() {
        Cursor c; int i=0;
        String[] sName;
        String sql = "select "
                + dbTable.COLUMN_NAME_IDEDO
                +" , "
                + dbTable.COLUMN_NAME_IDENC
                +" from "
                +dbTable.TABLE_NAME
                + " where status = '1'";
        c = database.rawQuery(sql,null);
        if(c.getCount() > 0)
            sName  = new String[c.getCount()];
        else
            sName = new String[] {"0","0"};
        c.moveToFirst();
        while(!c.isAfterLast()){
            sName[i] = c.getString(0)+":"+c.getString(1);
            i++;
            c.moveToNext();
        }
        c.close();
        return sName;
    }

    public String[] readEnc() {
        Cursor c; int i=0;
        String[] sEnc;
        String sql = "select "
                + dbTable.COLUMN_NAME_IDENC
                +" from "
                +dbTable.TABLE_NAME;
        c = database.rawQuery(sql,null);
        if(c.getCount() > 0)
            sEnc  = new String[c.getCount()];
        else
            sEnc = new String[] {"0"};
        c.moveToFirst();
        while(!c.isAfterLast()){
            sEnc[i] = c.getString(0);
            i++;
            c.moveToNext();
        }
        c.close();
        return sEnc;
        // return new String[0];
    }

    public int getLastQ(String sidEnc, String id_encuestado) {
        Cursor c;
        int idPreg = 0;
        String sql = "select "
                + dbTable.COLUMN_NAME_IDPREG
                +" from "
                +dbTable.TABLE_NAME
                + " where "
                + dbTable.COLUMN_NAME_IDENC
                + " = " + sidEnc
                + "  and "
                + dbTable.COLUMN_NAME_IDEDO
                + " = '" + id_encuestado+"'" ;
        Log.i(TAG, sql);
        c = database.rawQuery(sql,null);

        c.moveToFirst();
        while(!c.isAfterLast()){
            idPreg = c.getInt(0);
            c.moveToNext();
        }
        idPreg ++;
        c.close();
        return idPreg;
    }

    public int deleteEdo(String id_encuestado) {
        String sqlUpdate = " update "+dbTable.TABLE_NAME+ " set "+ dbTable.COLUMN_NAME_STATUS+ " = '0' where "+dbTable.COLUMN_NAME_IDEDO +" = '"+id_encuestado+"'";
        Cursor c;
        c = database.rawQuery(sqlUpdate,null);
        c.moveToFirst();
        int i = c.getCount();
        c.close();
        return i;
    }

    public boolean Checkdraft(String[] sborrador) {
        boolean value = true;
        Cursor c;
        String sql = "select "
                + dbTable.COLUMN_NAME_IDPREG
                +" from "
                +dbTable.TABLE_NAME
                + " where "
                + dbTable.COLUMN_NAME_IDENC
                + " = " + sborrador[1]
                + "  and "
                + dbTable.COLUMN_NAME_IDEDO
                + " = '" + sborrador[0]+"'"
                + "  and "
                + dbTable.COLUMN_NAME_IDEDO
                + " = '" + sborrador[4]+"'" ;
        Log.i(TAG, sql);
        c = database.rawQuery(sql,null);
        if(c.getCount()>0) value= false;
        c.close();

        return value;
    }

    public int updateDraft(String[] sborrador) {
        // eliminar table
        String sqlDel = dbTable.COLUMN_NAME_IDEDO+" = '"+ sborrador[0] + "' and " + dbTable.COLUMN_NAME_IDENC + " = '"+ sborrador[1]+"'";
        database.delete(dbTable.TABLE_NAME,sqlDel,null);
        // insertar datos
        ContentValues cv = new ContentValues();
        long c;
        cv.put(dbTable.COLUMN_NAME_IDEDO,sborrador[0]);
        cv.put(dbTable.COLUMN_NAME_IDENC,sborrador[1]);
        cv.put(dbTable.COLUMN_NAME_IDPREG, sborrador[2]);
        cv.put(dbTable.COLUMN_NAME_STATUS, sborrador[3]);
        cv.put(dbTable.COLUMN_NAME_IDEDR, sborrador[4]);
        c = database.insert(dbTable.TABLE_NAME, null, cv);
        return (int) c;
    }

    public boolean Checkdraft(String id_encuestado, int id_encuesta, String idEdor) {
        boolean value = false;
        Cursor c;
        String sql = "select "
                + dbTable.COLUMN_NAME_ID
                +" from "
                +dbTable.TABLE_NAME
                + " where "
                + dbTable.COLUMN_NAME_IDENC
                + " = " + id_encuesta
                + "  and "
                + dbTable.COLUMN_NAME_IDEDO
                + " = '" + id_encuestado+"'"
                + "  and "
                + dbTable.COLUMN_NAME_IDEDR
                + " = '" + idEdor+"'"
                + " and status = '1'";
        Log.i(TAG, sql);
        c = database.rawQuery(sql,null);
        if(c.getCount()>0) value= true;
        c.close();

        return value;
    }

    public void deleteBorrador(String[] sborrador) {
        String sqlDel = dbTable.COLUMN_NAME_IDEDO+" = '"+ sborrador[0] + "' and " + dbTable.COLUMN_NAME_IDENC + " = '"+ sborrador[1]+"'";
        database.delete(dbTable.TABLE_NAME,sqlDel,null);
    }
}

