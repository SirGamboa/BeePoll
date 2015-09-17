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
public class rcOpciones {
    private final dbOpciones dbTable;
    private SQLiteDatabase database;
    private final String TAG = "rcOpciones";

    public rcOpciones(Context context){
        dbTable = new dbOpciones(context);
    }
    @SuppressWarnings("UnusedReturnValue")
    public  long addOpciones(ContentValues cv){
        return database.insert(dbTable.TABLE_NAME, null, cv);
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

    public int getLenght(int loop, int id_encuesta) {
        Cursor c;
        String sql = "select id from survey_opciones where id_encuesta = "+ id_encuesta + " and id_pregunta = "+ loop;
        c = database.rawQuery(sql, null);
        int i = c.getCount();
        c.close();
        return i;//c.getCount();
    }

    public String[] getDesc(int loop, int id_encuesta) {
        Cursor c; String[] data;// = null;
        String sql = "select descripcion from survey_opciones where id_encuesta = "+ id_encuesta + " and id_pregunta = "+ loop;
        Log.i(TAG,sql );
        c = database.rawQuery(sql, null);
        int index = c.getCount();
        c.moveToFirst();
        data = new String[index];
        for(int i=0; i<index; i++){
            data[i] = c.getString(0);
            c.moveToNext();
        }
        c.close();
        return data;
    }

    public void deleteOpcs() {
        database.delete(dbTable.TABLE_NAME, null, null);
    }

    public String getRespVal (int loop, int id_encuesta) {
        Cursor c; int index;
        String sql = "select * "+
/*                dbTable.COLUMN_NAME_PREGVAL+ */
                "  from " +
                dbTable.TABLE_NAME ; //+
/*                " where "+
                dbTable.COLUMN_NAME_IDENCUESTA+
                " = "+
                id_encuesta +
                " and " +
                dbTable.COLUMN_NAME_IDPREGUNTATIPO +
                " = 3 and " +
                dbTable.COLUMN_NAME_IDPREGUNTA+
                " = "+
                loop; */



                c = database.rawQuery(sql, null);
                index = c.getCount();
                c.moveToFirst();
                Log.i(TAG + " Index:", String.valueOf(index));
                for(int i=0; i<index; i++){
//            data = c.getString(0);
                    Log.println(1, TAG, c.toString());
                    Log.i(TAG+"ID: ",(c.getString(0)==null)? "Null": c.getString(0));
                    Log.i(TAG+"ID Enc: ",(c.getString(1)==null)? "Null": c.getString(1));
                    Log.i(TAG+"ID PReg ",(c.getString(2)==null)? "Null": c.getString(2));
                    Log.i(TAG+"ID PregTipo: ",(c.getString(3)==null)? "Null": c.getString(3));
                    Log.i(TAG+"Desc: ",(c.getString(4)==null)? "Null": c.getString(4));
                    Log.i(TAG+"Valoracion: ",(c.getString(5)==null)? "Null": c.getString(5));
                    Log.i(TAG+"Peso: ",(c.getString(6)==null)? "Null": c.getString(6));
                    c.moveToNext();
                }


        c.close();
        return "0"; //data;
    }
}
