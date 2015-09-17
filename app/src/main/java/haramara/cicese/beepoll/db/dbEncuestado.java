package haramara.cicese.beepoll.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by diseno on 9/14/15. for BeePoll
 */
public class dbEncuestado extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "survey_encuestado";
    public static final String COLUMN_NAME_ID ="id";
    public static final String COLUMN_NAME_CLAVE ="clave";
    public static final String COLUMN_NAME_NOMBRES ="nombres";
    public static final String COLUMN_NAME_APELLIDO_PATERNO ="apellido_paterno";
    public static final String COLUMN_NAME_APELLIDO_MATERNO ="apellido_materno";
    public static final String COLUMN_NAME_FECHA_NACIMIENTO ="fecha_nacimiento";
    public static final String COLUMN_NAME_SEXO ="sexo";

    private static final String DATABASE_NAME ="DRSurbee.db"; //nombre de la base de datos local
    private static final int DATABASE_VERSION = 1;

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + dbEncuestado.TABLE_NAME;
	/*   crea la tabla en el dispositivo con los campos específicos.  */

    public static final String DATABASE_CREATE = " create table "
            + TABLE_NAME + " ( "
            + COLUMN_NAME_ID
            + " integer primary key, "
            + COLUMN_NAME_CLAVE
            + " text, "
            + COLUMN_NAME_NOMBRES
            + " text not null, "
            + COLUMN_NAME_APELLIDO_PATERNO
            + " text, "
            + COLUMN_NAME_APELLIDO_MATERNO
            + " text, "
            + COLUMN_NAME_FECHA_NACIMIENTO
            + " text, "
            + COLUMN_NAME_SEXO
            + " text "
            + "); ";


    public dbEncuestado(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }


    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(DATABASE_CREATE); // corre el query de la creación de la base de datos.
    }


    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        Log.w(dbEncuestado.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
