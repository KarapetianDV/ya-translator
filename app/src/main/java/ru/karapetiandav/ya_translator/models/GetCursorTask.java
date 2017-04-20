package ru.karapetiandav.ya_translator.models;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import ru.karapetiandav.ya_translator.data.HistoryContract;


public class GetCursorTask extends AsyncTask<Void, Void, Cursor> {

    private SQLiteDatabase db;
    private String[] columns = null;
    private String selection = null;
    private String[] selectionArgs = null;
    private String groupBy = null;
    private String having = null;
    private String orderBy = null;

    public GetCursorTask(SQLiteDatabase db, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        this.db = db;
        this.columns = columns;
        this.selection = selection;
        this.selectionArgs = selectionArgs;
        this.groupBy = groupBy;
        this.having = having;
        this.orderBy = orderBy;
    }

    @Override
    protected Cursor doInBackground(Void[] params) {
        return db.query(
                HistoryContract.HistoryTable.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                groupBy,
                having,
                orderBy
        );
    }
}
