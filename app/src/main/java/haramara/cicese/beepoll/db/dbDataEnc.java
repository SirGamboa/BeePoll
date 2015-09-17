package haramara.cicese.beepoll.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by diseno on 6/23/15. for Surbee_Haramara
 */
class dbDataEnc extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "survey_dataencuestador";
    public static final String COLUMN_NAME_ID ="id";
    public static final String COLUMN_NAME_IDENCUESTA ="id_encuesta";
    public static final String COLUMN_NAME_IDENCUESTADOR ="id_encuestador";
    public static final String COLUMN_NAME_ENVIOS ="num_envios";
    public static final String COLUMN_NAME_APLICACIONES ="num_aplicaciones";


    private static final String DATABASE_NAME ="DRSurbee.db"; //nombre de la base de datos local
    private static final int DATABASE_VERSION = 1;

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + dbDataEnc.TABLE_NAME;
	/*   crea la tabla en el dispositivo con los campos específicos.  */

    public static final String DATABASE_CREATE = " create table "
            + TABLE_NAME + " ( "
            + COLUMN_NAME_ID
            + " integer primary key, "
            + COLUMN_NAME_IDENCUESTA
            + " text, "
            + COLUMN_NAME_IDENCUESTADOR
            + " text, "
            + COLUMN_NAME_ENVIOS
            + " text, "
            + COLUMN_NAME_APLICACIONES
            + " text "
            + "); ";

    public dbDataEnc(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(DATABASE_CREATE); // corre el query de la creaci—n de la base de datos.
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        Log.w(dbEncuestado.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }
}
