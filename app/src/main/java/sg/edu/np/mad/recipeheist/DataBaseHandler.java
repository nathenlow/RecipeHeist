package sg.edu.np.mad.recipeheist;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHandler extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "RecipeDB";
    public static Integer DATABASE_VERSION = 1;
    public static String TABLE_UPDATES = "Updates";
    public static String COLUMN_RECIPEID = "RecipeID";
    public static String COLUMN_TITLE = "Title";
    public static String COLUMN_DURATION = "Duration";
    public static String COLUMN_IMAGEPATH = "ImagePath";
    public static String[] COLUMNS = {TABLE_UPDATES, COLUMN_RECIPEID, COLUMN_TITLE, COLUMN_DURATION, COLUMN_IMAGEPATH};


    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_UPDATES =
                "CREATE TABLE " + TABLE_UPDATES +"("  +
                        COLUMN_RECIPEID + " TEXT PRIMARY KEY," +
                        COLUMN_TITLE + " TEXT NOT NULL," +
                        COLUMN_DURATION + " TEXT NOT NULL," +
                        COLUMN_IMAGEPATH + " TEXT NOT NULL);";
        db.execSQL(CREATE_TABLE_UPDATES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_UPDATES);
        this.onCreate(db);
    }


    //METHODS
    public void addUpdates(RecipePreview recipePreview){
        ContentValues values = new ContentValues();
        values.put(COLUMN_RECIPEID, recipePreview.getId());
        values.put(COLUMN_TITLE, recipePreview.getTitle());
        values.put(COLUMN_DURATION, recipePreview.getduration());
        values.put(COLUMN_IMAGEPATH, recipePreview.getImagePath());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_UPDATES, null, values);
        db.close();
    }
}
