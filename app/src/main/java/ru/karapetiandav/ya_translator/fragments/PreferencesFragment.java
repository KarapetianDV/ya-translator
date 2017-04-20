package ru.karapetiandav.ya_translator.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.concurrent.ExecutionException;

import ru.karapetiandav.ya_translator.R;
import ru.karapetiandav.ya_translator.data.DBHelper;
import ru.karapetiandav.ya_translator.data.HistoryContract;
import ru.karapetiandav.ya_translator.models.GetCursorTask;


public class PreferencesFragment extends Fragment {

    SQLiteDatabase db;
    DBHelper dbHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DBHelper(getActivity());
        db = dbHelper.getWritableDatabase();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pref, container, false);

        Button deleteHistory = (Button) view.findViewById(R.id.deleteHistory);
        Button deleteFavorite = (Button) view.findViewById(R.id.deleteFavorite);

        deleteHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().deleteDatabase(HistoryContract.HistoryTable.TABLE_NAME);
            }
        });

        deleteFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Cursor cursor = new GetCursorTask(
                            db,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null
                    ).execute().get();

                    cursor.moveToFirst();
                    while (cursor.moveToNext()) {
                        ContentValues values = new ContentValues();
                        values.put(HistoryContract.HistoryTable.COLUMN_FAV, HistoryContract.HistoryTable.NOT_FAV);

                        db.update(
                                HistoryContract.HistoryTable.TABLE_NAME,
                                values,
                                null,
                                null
                        );
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }
}
