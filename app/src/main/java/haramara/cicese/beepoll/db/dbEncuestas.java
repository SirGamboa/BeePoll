package haramara.cicese.beepoll.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by diseno on 5/27/15. for Surbee_Haramara
 */
public class dbEncuestas extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "survey_encuestas";
    public static final String COLUMN_NAME_ID ="id";
    public static final String COLUMN_NAME_IDENCUESTA ="id_encuesta";
    //    public static final String COLUMN_NAME_ID_ENCUESTADO ="id_encuestado";
    public static final String COLUMN_NAME_NOMBRE ="nombre";
    public static final String COLUMN_NAME_DESCRIPCION ="descripcion";
    public static final String COLUMN_NAME_INSTRUCCIONES ="instrucciones";
    public static final String COLUMN_NAME_ETIQUETA ="etiqueta";
    private static final String COLUMN_NAME_PESO ="peso";
    private static final String COLUMN_NAME_PREGUNTAS ="preguntas";
    public static final String COLUMN_NAME_IDEDO ="encuestador";
    public static final String COLUMN_NAME_EDO ="datos_edo";


    private static final String DATABASE_NAME ="DRSurbee.db"; //nombre de la base de datos local
    private static final int DATABASE_VERSION = 1;

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + dbEncuestas.TABLE_NAME;
	/*   crea la tabla en el dispositivo con los campos específicos.  */

    public static final String DATABASE_CREATE = " create table "
            + TABLE_NAME + " ( "
            + COLUMN_NAME_ID
            + " integer primary key , "
            + COLUMN_NAME_IDENCUESTA
            + " text, "
            + COLUMN_NAME_NOMBRE
            + " text not null, "
            + COLUMN_NAME_DESCRIPCION
            + " text, "
            + COLUMN_NAME_INSTRUCCIONES
            + " text, "
            + COLUMN_NAME_ETIQUETA
            + " text, "
            + COLUMN_NAME_PESO
            + " text, "
            + COLUMN_NAME_PREGUNTAS
            + " text, "
            + COLUMN_NAME_IDEDO
            + " text , "
            + COLUMN_NAME_EDO
            + " text "
            + "); ";
    public dbEncuestas(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

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
