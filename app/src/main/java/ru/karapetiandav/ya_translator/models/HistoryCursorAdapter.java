package ru.karapetiandav.ya_translator.models;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ru.karapetiandav.ya_translator.R;
import ru.karapetiandav.ya_translator.data.HistoryContract;


public class HistoryCursorAdapter extends CursorAdapter {

    public HistoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView isFavoriteImageView = (ImageView) view.findViewById(R.id.isFavoriteImageView);
        TextView historyText = (TextView) view.findViewById(R.id.historyText);
        TextView historyTraslatedText = (TextView) view.findViewById(R.id.historyTraslatedText);
        TextView lang = (TextView) view.findViewById(R.id.lang);

        // Получение статуса 0 или 1, для избранного
        int fav = cursor.getInt(
                cursor.getColumnIndexOrThrow(
                        HistoryContract.HistoryTable.COLUMN_FAV));

        if(fav == HistoryContract.HistoryTable.NOT_FAV) {
            isFavoriteImageView.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
        }

        historyText.setText(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                HistoryContract.HistoryTable.COLUMN_TEXT)));
        historyTraslatedText.setText(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                HistoryContract.HistoryTable.COLUMN_TRANS)));
        lang.setText(
                cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                HistoryContract.HistoryTable.COLUMN_LANGS)).toUpperCase());
    }
}
