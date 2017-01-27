package saeed.life.SQLiteDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import saeed.life.Model.User;


public class UserSQLite extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "User.db";
    public static final String TABLE_NAME = "current_user";
    public static final String COL_1 = "id";
    public static final String COL_2 = "name";
    public static final String COL_3 = "email";
    public static final String COL_4 = "blood_type";
    public static final String COL_5 = "phone";
    public static final String COL_6 = "gender";

    public UserSQLite(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME +" (" +  COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_2 + " TEXT," + COL_3 + " TEXT," + COL_4 + " TEXT,"
                + COL_5 + " TEXT," + COL_6 + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, (int)Math.random()+10000);
        contentValues.put(COL_2, user.getName());
        contentValues.put(COL_3, user.getEmail());
        contentValues.put(COL_4, user.getBloodType());
        contentValues.put(COL_5, user.getPhone());
        contentValues.put(COL_6, user.getGender());
        long result = db.insert(TABLE_NAME,null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public User getUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME, null);
        User user = null;
        if (cursor != null) {
            while(cursor.moveToNext()) {
                 user = new User(cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4), cursor.getString(5), "");
            }
            cursor.close();
        }
        return user;
    }

    public Integer deleteUser (User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "email = ?", new String[] {user.getEmail()});
    }
}