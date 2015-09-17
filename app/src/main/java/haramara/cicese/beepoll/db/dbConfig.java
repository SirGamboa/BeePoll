package haramara.cicese.beepoll.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by diseno on 9/14/15. for BeePoll
 */
public class dbConfig  extends SQLiteOpenHelper{
    public static final String TABLE_NAME = "survey_configuracion";
    public static final String COLUMN_NAME_ID ="id";
    public static final String COLUMN_NAME_USUARIO ="usuario";
    private static final String COLUMN_NAME_ENCUESTADO_ID ="encuestado_id";
    private static final String DATABASE_NAME ="DRSurbee.db"; //nombre de la base de datos local
    private static final int DATABASE_VERSION = 1;
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + dbEncuestado.TABLE_NAME;
	/*   crea la tabla en el dispositivo con los campos específicos.  */

    private static final String DATABASE_CREATE = " create table "
            + TABLE_NAME + " ( "
            + COLUMN_NAME_ID
            + " text primary key, "
            + COLUMN_NAME_USUARIO
            + " text, "
            + COLUMN_NAME_ENCUESTADO_ID
            + " text "
            + "); ";

    public  static final  String SQL_CREATE_DATA =
            "INSERT INTO " + TABLE_NAME + " (" +
                    COLUMN_NAME_ID + ", " +
                    COLUMN_NAME_USUARIO + ", " +
                    COLUMN_NAME_ENCUESTADO_ID +
                    " ) " +
                    "VALUES (" +
                    "0, " +
                    "'', " +
                    "-1" +
                    " )";
    public dbConfig(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // TODO Auto-generated method stub
        db.execSQL(DATABASE_CREATE); // corre el query de la creación de la base de datos.
        db.execSQL(dbEncuestas.DATABASE_CREATE);
        db.execSQL(dbPreguntas.DATABASE_CREATE);
        db.execSQL(dbOpciones.DATABASE_CREATE);
        db.execSQL(dbEncuestado.DATABASE_CREATE);
        db.execSQL(dbRespuestas.DATABASE_CREATE);
        db.execSQL(dbCompleted.DATABASE_CREATE);
        db.execSQL(dbDataEnc.DATABASE_CREATE);
        db.execSQL(dbRelacion.DATABASE_CREATE);
        db.execSQL("PRAGMA encoding = \"UTF-16\"");
        String TAG = "DBConfig";
        Log.i(TAG, "DBCreated");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // TODO Auto-generated method stub
        Log.w(dbConfig.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // db.execSQL(dbConfig.SQL_DELETE_ENTRIES);
        db.execSQL(dbEncuestas.SQL_DELETE_ENTRIES);
        db.execSQL(dbPreguntas.SQL_DELETE_ENTRIES);
        db.execSQL(dbOpciones.SQL_DELETE_ENTRIES);
        db.execSQL(dbEncuestado.SQL_DELETE_ENTRIES);
        db.execSQL(dbRespuestas.SQL_DELETE_ENTRIES);
        db.execSQL(dbCompleted.SQL_DELETE_ENTRIES);
        db.execSQL(dbDataEnc.SQL_DELETE_ENTRIES);
        db.execSQL(dbRelacion.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
