package ru.karapetiandav.ya_translator.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "history";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + HistoryContract.HistoryTable.TABLE_NAME + " (" +
                HistoryContract.HistoryTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                HistoryContract.HistoryTable.COLUMN_TEXT + " TEXT NOT NULL," +
                HistoryContract.HistoryTable.COLUMN_TRANS + " TEXT NOT NULL, " +
                HistoryContract.HistoryTable.COLUMN_LANGS + " TEXT NOT NULL, " +
                HistoryContract.HistoryTable.COLUMN_FAV + " INTEGER NOT NULL DEFAULT "
                + HistoryContract.HistoryTable.IN_FAV + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF IT EXISTS " + HistoryContract.HistoryTable.TABLE_NAME);
        onCreate(db);
    }
}
