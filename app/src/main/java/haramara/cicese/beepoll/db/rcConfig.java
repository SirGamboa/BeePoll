package haramara.cicese.beepoll.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.sql.SQLException;

/**
 * Created by diseno on 9/14/15. for BeePoll
 */
public class rcConfig {
    private final dbConfig dbTable;
    private SQLiteDatabase database;
    private final String TAG = "RCConfig";

    public rcConfig(Context context)
    {
        dbTable = new dbConfig(context);

    }
    /*              Abre DB                           */
    public void open() throws SQLException{
        database = dbTable.getWritableDatabase();
    }
    /*                Cierra DB                         */
    public void close(){
        database.close();
    }
    /*                    Lee ( Abre ) DB                     */
    public void read() {  database = dbTable.getReadableDatabase();}


    public boolean addUser(ContentValues cv){
        ContentValues x = new ContentValues();
        String id, user;
        id = cv.getAsString("ID");
        user = cv.getAsString("USER");
        x.put(dbTable.COLUMN_NAME_ID, id);
        x.put(dbTable.COLUMN_NAME_USUARIO, user);
        return database.insert(dbTable.TABLE_NAME, null, x) >= 0;
    }
    public boolean delUser(){//String toDel
        boolean value = false;
        if(database.delete(dbTable.TABLE_NAME, null, null) > 0)
            value = true;
        return value;
    }
    public  int updateRow(String usuario) {
        // New value for one column
        ContentValues values = new ContentValues();
        values.put(dbTable.COLUMN_NAME_USUARIO, usuario);
        Log.i(TAG, "Usuario: " + usuario);
        // Which row to update, based on the ID
        String selection = dbConfig.COLUMN_NAME_USUARIO + " LIKE ? ";
        String[] selectionArgs = {usuario};
        int count = database.update(
                dbTable.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
        // database.close();
        Log.i("SelArgs", selectionArgs[0]);
        return count;
    }
    public String readID(){
        String result = "-1";
        Cursor c;
        c = database.rawQuery("select id from " + dbTable.TABLE_NAME, null);
        c.moveToFirst();
        if(c.getCount() > 0) { // existe registro
            result = c.getString(c.getColumnIndex(dbTable.COLUMN_NAME_ID));
        }
        c.close();
        return result;

    }
    public  int readUsuario() {
        Cursor cursor = database.query(
                dbTable.TABLE_NAME,
                new String[]{
                        dbTable.COLUMN_NAME_ID,
                        dbTable.COLUMN_NAME_USUARIO
                },
                null,
                null,
                null,
                null,
                null,
                null
        );
        int i = cursor.getCount();
        cursor.close();
        return i;//cursor.getCount();

    }
    public  String readUser() {
        String result ="";

        Cursor cursor = database.query(
                dbTable.TABLE_NAME,
                new String[]{
                        dbTable.COLUMN_NAME_ID,
                        dbTable.COLUMN_NAME_USUARIO
                },
                null,
                null,
                null,
                null,
                null,
                null
        );
        cursor.moveToFirst();
        if(cursor.getCount() > 0) { // no encontró
            Log.i(TAG, "Cursor");
            result = cursor.getString(cursor.getColumnIndex(dbTable.COLUMN_NAME_USUARIO));
        }
        cursor.close();
        return result;

    }
    public boolean checkUserExist() {
        boolean value = false;
        String sql = "select id from "+dbTable.TABLE_NAME;
        Cursor cursor = database.rawQuery(sql, null);

        cursor.moveToFirst();
        if(cursor.getCount() > 0) {
            value = true;
            Log.i(TAG, "EXIST USER");
        }
        cursor.close();
        return value;
    }

    public String readEmail(String mail_toCheck) {
        String result = "";
        Cursor cursor = database.query(
                dbTable.TABLE_NAME,
                new String[]{
                        dbTable.COLUMN_NAME_ID,
                        dbTable.COLUMN_NAME_USUARIO
                },
                dbTable.COLUMN_NAME_USUARIO + " like ?",
                new String[]{mail_toCheck},
                null,
                null,
                null,
                null
        );

        cursor.moveToFirst();
        if(cursor.getCount() > 0) { // no encontró
            Log.i(TAG, "readMail");
            result =  cursor.getString(cursor.getColumnIndex(dbTable.COLUMN_NAME_USUARIO));
        }
        cursor.close();
        return result;
    }

    public String readUserMail() {
        String result = "@";

        Cursor cursor = database.query(
                dbTable.TABLE_NAME,
                new String[]{
                        dbTable.COLUMN_NAME_ID,
                        dbTable.COLUMN_NAME_USUARIO
                },
                null, null, null, null, null, null
        );
        cursor.moveToFirst();
        if(cursor.getCount() > 0) { // no encontró
            Log.i(TAG, "readMail");
            result =  cursor.getString(cursor.getColumnIndex(dbTable.COLUMN_NAME_USUARIO));
        }
        cursor.close();
        return result;
    }

}
