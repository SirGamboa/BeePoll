package haramara.cicese.beepoll.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by diseno on 5/27/15. for Surbee_Haramara
 */
public class dbRespuestas extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "survey_respuestas";
    private static final String COLUMN_NAME_ID ="id";
    public static final String COLUMN_NAME_IDPREGTIPO ="idPregTipo";
    public static final String COLUMN_NAME_ENCUESTADO_ID ="id_encuestado";
    public static final String COLUMN_NAME_ENCUESTA_ID ="id_encuesta";
    public static final String COLUMN_NAME_ENCUESTADOR_ID ="id_encuestador";
    public static final String COLUMN_NAME_PREGUNTA_ID ="id_pregunta";
    public static final String COLUMN_NAME_RESPUESTA ="respuesta";
    public static final String COLUMN_NAME_IDVAL ="idVal";
    public static final String COLUMN_NAME_STATUS ="status";
    private static final String DATABASE_NAME ="DRSurbee.db"; //nombre de la base de datos local
    private static final int DATABASE_VERSION = 1;

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + dbRespuestas.TABLE_NAME;
	/*   crea la tabla en el dispositivo con los campos específicos.  */

    public static final String DATABASE_CREATE = " create table "
            + TABLE_NAME + " ( "
            + COLUMN_NAME_ID
            + " integer primary key, "
            + COLUMN_NAME_IDPREGTIPO
            + " text, "
            + COLUMN_NAME_ENCUESTADO_ID
            + " text, "
            + COLUMN_NAME_ENCUESTA_ID
            + " text, "
            + COLUMN_NAME_ENCUESTADOR_ID
            + " text, "
            + COLUMN_NAME_PREGUNTA_ID
            + " text,"
            + COLUMN_NAME_RESPUESTA
            + " text, "
            + COLUMN_NAME_IDVAL
            + " text, "
            + COLUMN_NAME_STATUS
            + " text "
            + "); ";

    public dbRespuestas(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(DATABASE_CREATE); // corre el query de la creación de la base de datos.
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        Log.w(dbRespuestas.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
