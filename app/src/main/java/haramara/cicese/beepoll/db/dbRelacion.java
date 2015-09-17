package haramara.cicese.beepoll.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by diseno on 7/20/15. for Surbee_Haramara
 */
public class dbRelacion extends SQLiteOpenHelper{
    public static final String TABLE_NAME = "survey_relacion";
    private static final String COLUMN_NAME_ID ="id";
    public static final String COLUMN_NAME_IDEDO ="id_encuestado";
    public static final String COLUMN_NAME_IDENC ="id_encuesta";

    private static final String DATABASE_NAME ="DRSurbee.db"; //nombre de la base de datos local
    private static final int DATABASE_VERSION = 1;

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + dbCompleted.TABLE_NAME;

    public static final String DATABASE_CREATE = " create table "
            + TABLE_NAME + " ( "
            + COLUMN_NAME_ID
            + " integer primary key, "
            + COLUMN_NAME_IDEDO
            + " text, "
            + COLUMN_NAME_IDENC
            + " text "
            + "); ";

    public dbRelacion(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(dbEncuestado.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
