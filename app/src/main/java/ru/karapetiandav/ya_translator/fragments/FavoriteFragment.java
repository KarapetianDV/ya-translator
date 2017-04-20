package ru.karapetiandav.ya_translator.fragments;


import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import ru.karapetiandav.ya_translator.R;
import ru.karapetiandav.ya_translator.data.DBHelper;
import ru.karapetiandav.ya_translator.data.HistoryContract;
import ru.karapetiandav.ya_translator.models.HistoryCursorAdapter;

public class FavoriteFragment extends ListFragment {

    private static final String TAG = FavoriteFragment.class.getSimpleName();
    private ItemClickInterface favoriteItemListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (!(context instanceof ItemClickInterface)) throw new AssertionError();
        favoriteItemListener = (ItemClickInterface) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        DBHelper dbHelper = new DBHelper(getActivity());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.query(
                HistoryContract.HistoryTable.TABLE_NAME,
                null,
                HistoryContract.HistoryTable.COLUMN_FAV + " = ?",
                new String[]{String.valueOf(HistoryContract.HistoryTable.IN_FAV)},
                null,
                null,
                null);

        Log.d(TAG, "onStart: " + DatabaseUtils.dumpCursorToString(c));

        CursorAdapter adapter = new HistoryCursorAdapter(getActivity(), c);

        getListView().setAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String lang = ((TextView) v.findViewById(R.id.lang)).getText().toString();
        String text = ((TextView) v.findViewById(R.id.historyText)).getText().toString();
        int length = lang.length();

        TranslateFragment translateFragment =
                TranslateFragment.newInstance(
                        lang.substring(0, 2),
                        lang.substring(length-2, length),
                        text);
        favoriteItemListener.clicked(translateFragment);
    }
}
