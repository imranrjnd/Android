package com.example.sqlitecrud;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MyDatabase";

    public static final int DATABASE_VERSION = 1;

    public SqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE users (email TEXT PRIMARY KEY, gender Text, hobbies Text, blood Text)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i2) {
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }
    public boolean saveUser (String email, String gender, String hobbies, String blood)
    {
        Cursor cursor = getUser(email);

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("gender", gender);
        contentValues.put("hobbies", hobbies);
        contentValues.put("blood", blood);

        long result;
        if (cursor.getCount() == 0) { // Record does not exist
            contentValues.put("email", email);
            result = db.insert("users", null, contentValues);
        } else { // Record exists
            result = db.update("users", contentValues, "email=?", new String[] { email });
        }

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Cursor getUser(String email){

        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "SELECT * FROM users WHERE email=?";

        return db.rawQuery(sql, new String[] { email });
    }

    public void deleteUser(String email){

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete("users", "email=?", new String[] { email });
    }
}
