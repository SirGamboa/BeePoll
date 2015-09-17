package haramara.cicese.beepoll.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

/**
 * Created by diseno on 7/20/15. for Surbee_Haramara
 */
public class rcRelacion {
    private final dbRelacion dbTable;
    private SQLiteDatabase database;
    String TAG = "RCRelacion";

    public rcRelacion(Context context)
    {
        dbTable = new dbRelacion(context);

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

    public void addRel(String s, int idEncuestador) {
        ContentValues cv = new ContentValues();
        cv.put(dbTable.COLUMN_NAME_IDEDO, idEncuestador);
        cv.put(dbTable.COLUMN_NAME_IDENC, s);
        database.insert(dbTable.TABLE_NAME, null, cv);
    }

    public String[] readEncs(String idUser) {
        @SuppressWarnings("UnusedAssignment") ContentValues cv = new ContentValues();
        Cursor c;
        int i=0;
        String[] arg = new String[]{idUser};
        String sql = "select "
                + dbTable.COLUMN_NAME_IDENC
                + " from "
                + dbTable.TABLE_NAME
                +" where "
                + dbTable.COLUMN_NAME_IDEDO
                + " = ?";
        c = database.rawQuery(sql, arg);
        c.moveToFirst();
        String[] aData = new String[c.getCount()];
        while(!c.isAfterLast()){
            aData[i] = c.getString(0);
            c.moveToNext();
            i++;
        }
        c.close();
        return aData;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean checkUpdate(String s, int idEncuestador) {
        ContentValues cv = new ContentValues();
        cv.put(dbTable.COLUMN_NAME_IDEDO, idEncuestador);
        cv.put(dbTable.COLUMN_NAME_IDENC, s);
        String whereClause =  dbTable.COLUMN_NAME_IDEDO + " = ? and "  +  dbTable.COLUMN_NAME_IDENC + " = ? ";
        return database.update(dbTable.TABLE_NAME, cv, whereClause, new String[]{String.valueOf(idEncuestador), s}) > 0;
    }

    public void deleteRel() {
        database.delete(dbTable.TABLE_NAME,null,null);
    }
}
