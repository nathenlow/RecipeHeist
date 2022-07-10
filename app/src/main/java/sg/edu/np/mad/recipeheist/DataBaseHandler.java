package sg.edu.np.mad.recipeheist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DataBaseHandler extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "RecipeDB";
    public static Integer DATABASE_VERSION = 1;
    public static String TABLE_UPDATES = "Updates";
    public static String COLUMN_RECIPEID = "RecipeID";
    public static String COLUMN_TITLE = "Title";
    public static String COLUMN_IMAGEPATH = "ImagePath";
    public static String COLUMN_DURATION = "Duration";
    public static String[] COLUMNS = {COLUMN_RECIPEID, COLUMN_TITLE, COLUMN_IMAGEPATH, COLUMN_DURATION};


    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_UPDATES =
                "CREATE TABLE " + TABLE_UPDATES +"("  +
                        COLUMN_RECIPEID + " TEXT PRIMARY KEY," +
                        COLUMN_TITLE + " TEXT NOT NULL," +
                        COLUMN_IMAGEPATH + " TEXT NOT NULL," +
                        COLUMN_DURATION + " TEXT NOT NULL);";
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
        values.put(COLUMN_IMAGEPATH, recipePreview.getImagePath());
        values.put(COLUMN_DURATION, recipePreview.getduration());
        SQLiteDatabase db = this.getWritableDatabase();
        db.replace(TABLE_UPDATES, null, values);
        db.close();
    }

    public ArrayList<RecipePreview> chefUpdates(){
        ArrayList<RecipePreview> updatelist = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_UPDATES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        RecipePreview recipePreview = null;
        if (cursor.moveToFirst()){
            do {
                recipePreview = new RecipePreview(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3)
                );
                updatelist.add(recipePreview);
            }while (cursor.moveToNext());
        }
        return updatelist;
    }


}
