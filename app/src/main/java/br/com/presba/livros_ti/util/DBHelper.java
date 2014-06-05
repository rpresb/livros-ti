package br.com.presba.livros_ti.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_CREATE = "create table book (" +
            "_id integer primary key autoincrement, "
            + "bookid integer not null, "
            + "title text not null, "
            + "subtitle text null, "
            + "description text null, "
            + "isbn text null, "
            + "author text null, "
            + "year text null, "
            + "page text null, "
            + "publisher text null, "
            + "image BLOB null);";

    private static final String DATABASE_NAME = "livros_ti.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data"
        );
        db.execSQL("DROP TABLE IF EXISTS book");
        onCreate(db);
    }
}