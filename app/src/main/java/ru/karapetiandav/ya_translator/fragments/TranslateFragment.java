package ru.karapetiandav.ya_translator.fragments;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.karapetiandav.ya_translator.App;
import ru.karapetiandav.ya_translator.R;
import ru.karapetiandav.ya_translator.activity.FullscreenActivity;
import ru.karapetiandav.ya_translator.data.DBHelper;
import ru.karapetiandav.ya_translator.data.HistoryContract;
import ru.karapetiandav.ya_translator.models.GetCursorTask;
import ru.karapetiandav.ya_translator.models.dictionaryApi.DataFromDictionary;
import ru.karapetiandav.ya_translator.models.dictionaryApi.Def;
import ru.karapetiandav.ya_translator.models.dictionaryApi.Mean;
import ru.karapetiandav.ya_translator.models.dictionaryApi.Syn;
import ru.karapetiandav.ya_translator.models.dictionaryApi.Tr;
import ru.karapetiandav.ya_translator.models.translatorApi.DataFromTranslator;
import ru.karapetiandav.ya_translator.models.translatorApi.LangsFromTranslator;

public class TranslateFragment extends Fragment {

    private static final String TAG = TranslateFragment.class.getSimpleName();

    private EditText mInputEditText;
    private TextView mResultText;
    private TextView mOriginalText;
    private TextView mSynonymText;

    private ImageView favoriteButton;

    private Spinner fromLangSpinner;
    private Spinner toLangSpinner;
    private ImageView reverseImageView;

    private ArrayAdapter<String> adapter;

    private String defaultFromLang = "ru";
    private String nowSelectedFromLang = defaultFromLang;

    private String defaultToLang = "en";
    private String nowSelectedToLang = defaultToLang;

    private HashMap<String, String> langs;

    private SQLiteDatabase db;

    private Timer saveInDatabaseTimer;

    public static TranslateFragment newInstance(String fromLang, String toLang, String text) {

        Bundle args = new Bundle();
        args.putString("fromLang", fromLang);
        args.putString("toLang", toLang);
        args.putString("editText", text);

        TranslateFragment fragment = new TranslateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DBHelper(getActivity()).getWritableDatabase();
        Log.d(TAG, "onCreate: запуск заново");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView: запуск заново");

        View view = inflater.inflate(R.layout.fragment_translate, container, false);

        fromLangSpinner = (Spinner) view.findViewById(R.id.fromLangSpinner);
        toLangSpinner = (Spinner) view.findViewById(R.id.toLangSpinner);

        reverseImageView = (ImageView) view.findViewById(R.id.reverseImageView);

        initialSpinners(fromLangSpinner, toLangSpinner);

        fromLangSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for (String key : langs.keySet()) {
                    if (langs.get(key).equals(parent.getSelectedItem())) {
                        nowSelectedFromLang = key;
                        Log.d(TAG, "onItemSelected: nowSelectedFromLang: " + nowSelectedFromLang);
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        toLangSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for (String key : langs.keySet()) {
                    if (langs.get(key).equals(parent.getSelectedItem())) {
                        nowSelectedToLang = key;
                        Log.d(TAG, "onItemSelected: nowSelectedToLang: " + nowSelectedToLang);
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        reverseImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reverseLangs();
            }
        });

        mInputEditText = (EditText) view.findViewById(R.id.inputEditText);
        mResultText = (TextView) view.findViewById(R.id.outputText);
        mOriginalText = (TextView) view.findViewById(R.id.originalText);
        favoriteButton = (ImageView) view.findViewById(R.id.favoriteButton);
        ImageView shareTranslate = (ImageView) view.findViewById(R.id.shareTranslate);
        ImageView btnFullscreen = (ImageView) view.findViewById(R.id.btnFullscreen);

        mSynonymText = (TextView) view.findViewById(R.id.synonymText);

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(
                        getActivity(),
                        "Невозможно добавить слово без перевода в избранное",
                        Toast.LENGTH_SHORT).show();
            }
        });

        shareTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, mResultText.getText().toString());
                startActivity(Intent.createChooser(shareIntent, "Отправить с помощью..."));
            }
        });

        btnFullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FullscreenActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, mResultText.getText().toString());
                startActivity(intent);
            }
        });

        mInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // Сбрасываем отсчёт таймера если пользователь снова начал вводить текст
                if (saveInDatabaseTimer != null) {
                    saveInDatabaseTimer.cancel();
                    Log.d(TAG, "onTextChanged: saveInDatabaseTimer canceled");
                }

                if (s.length() > 0) {
                    final String text = s.toString();

                    if (isFavorite(text)) {
                        favoriteButton.setImageResource(R.drawable.ic_bookmark_yellow_24dp);
                    } else {
                        favoriteButton.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                    }

                    App.getTranslatorApi().getTranslatedData(
                            getResources().getString(R.string.translator_api_key),
                            text,
                            nowSelectedFromLang + "-" + nowSelectedToLang
                    ).enqueue(new Callback<DataFromTranslator>() {
                        @Override
                        public void onResponse(Call<DataFromTranslator> call, Response<DataFromTranslator> response) {
                            if (response.isSuccessful()) {
                                final StringBuilder builder = new StringBuilder();
                                for (String str : response.body().getText()) {
                                    builder.append(str);
                                }
                                mResultText.setText(builder.toString());
                                mOriginalText.setText(text);

                                // Добавление слова в избранное при клике
                                favoriteButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if (!isFavorite(text)) {
                                            saveInFavorite(text, builder);

                                            Toast.makeText(getActivity(),
                                                    "Слово добавлено в избранное",
                                                    Toast.LENGTH_SHORT).show();

                                            favoriteButton.setImageResource(R.drawable.ic_bookmark_yellow_24dp);
                                        } else {
                                            deleteFromFavorite(text);
                                            favoriteButton.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                                        }
                                    }
                                });

                                findSynonyms();

                                TimerTask saveInDatabaseTask = new TimerTask() {
                                    @Override
                                    public void run() {
                                        saveInHistory(text, builder.toString(),
                                                nowSelectedFromLang + "-" + nowSelectedToLang);
                                    }
                                };

                                saveInDatabaseTimer = new Timer();
                                saveInDatabaseTimer.schedule(saveInDatabaseTask, 1000);

                            } else {
                                Toast.makeText(
                                        getActivity(),
                                        "Не удалось получить перевод",
                                        Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }

                        @Override
                        public void onFailure(Call<DataFromTranslator> call, Throwable t) {
                            Toast.makeText(
                                    getActivity(),
                                    "Невозможно получить перевод в данный момент",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                if(s.length() == 0) {
                    mResultText.setText("");
                    mOriginalText.setText("");
                    mSynonymText.setText("");

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null) {
            Log.d(TAG, "onActivityCreated: Arguments");
            mInputEditText.setText(getArguments().getString("editText"));
            nowSelectedToLang = getArguments().getString("toLang");
            nowSelectedFromLang = getArguments().getString("fromLang");
        }
    }

    private void deleteFromFavorite(String text) {
        Log.d(TAG, "deleteFromFavorite: ");
        db.delete(
                HistoryContract.HistoryTable.TABLE_NAME,
                HistoryContract.HistoryTable.COLUMN_TEXT + " = ?",
                new String[]{text}
        );
    }

    // Выставление нужного значка если слово в избранном
    private boolean isFavorite(String text) {
        boolean isInFav = false;

        try {
            isInFav = new GetCursorTask(
                    db,
                    new String[]{HistoryContract.HistoryTable.COLUMN_TEXT},
                    HistoryContract.HistoryTable.COLUMN_TEXT + " = ? AND " +
                            HistoryContract.HistoryTable.COLUMN_FAV + " = ?",
                    new String[]{text, String.valueOf(HistoryContract.HistoryTable.IN_FAV)},
                    null,
                    null,
                    null
            ).execute().get().moveToFirst();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return isInFav;
    }

    private void saveInFavorite(String text, StringBuilder builder) {
        ContentValues values = new ContentValues();
        values.put(HistoryContract.HistoryTable.COLUMN_TEXT, text);
        values.put(HistoryContract.HistoryTable.COLUMN_TRANS, builder.toString());
        values.put(HistoryContract.HistoryTable.COLUMN_LANGS,
                nowSelectedFromLang + "-" + nowSelectedToLang);
        values.put(HistoryContract.HistoryTable.COLUMN_FAV,
                HistoryContract.HistoryTable.IN_FAV);

        Log.d(TAG, "saveInFavorite: ");

        db.update(
                HistoryContract.HistoryTable.TABLE_NAME,
                values,
                HistoryContract.HistoryTable.COLUMN_TEXT + " = ?",
                new String[]{text});
    }

    // Запись в базу данных, по прошествии определенного времени
    // Либо перезапись если слово уже существует
    private void saveInHistory(String text, String translate, String langs) {
        Log.d(TAG, "saveInHistory: save in database");

        ContentValues values = new ContentValues();
        values.put(HistoryContract.HistoryTable.COLUMN_TEXT, text);
        values.put(HistoryContract.HistoryTable.COLUMN_TRANS, translate);
        values.put(HistoryContract.HistoryTable.COLUMN_LANGS, langs);
        values.put(HistoryContract.HistoryTable.COLUMN_FAV, HistoryContract.HistoryTable.NOT_FAV);

        Cursor c = null;
        try {
            c = new GetCursorTask(
                    db,
                    new String[]{HistoryContract.HistoryTable.COLUMN_TEXT,
                            HistoryContract.HistoryTable.COLUMN_FAV},
                    HistoryContract.HistoryTable.COLUMN_TEXT + " = ?",
                    new String[]{text},
                    null,
                    null,
                    null
            ).execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        if (c != null) {
            if (!c.moveToFirst()) {
                db.insert(HistoryContract.HistoryTable.TABLE_NAME, null, values);
                Log.d(TAG, "saveInHistory: add new word "
                        + values.getAsString(HistoryContract.HistoryTable.COLUMN_TEXT));
            } else {

                int fav = c.getInt(c.getColumnIndexOrThrow(HistoryContract.HistoryTable.COLUMN_FAV));
                values.put(HistoryContract.HistoryTable.COLUMN_FAV, fav);

                db.update(HistoryContract.HistoryTable.TABLE_NAME,
                        values,
                        HistoryContract.HistoryTable.COLUMN_TEXT + " = ?",
                        new String[]{text});
                Log.d(TAG, "saveInHistory: rewrite word "
                        + values.getAsString(HistoryContract.HistoryTable.COLUMN_TEXT)
                        + " with fav param " + fav);
            }
        }

        if (c != null) {
            c.close();
        }
    }

    private void findSynonyms() {
        App.getDictionaryApi().getDataFromDictionary(
                getString(R.string.dictionary_api_key),
                nowSelectedFromLang + "-" + nowSelectedToLang,
                mInputEditText.getText().toString()
        ).enqueue(new Callback<DataFromDictionary>() {
            @Override
            public void onResponse(Call<DataFromDictionary> call, Response<DataFromDictionary> response) {
                if (response.body() != null && response.body().getDef().size() > 0) {
                    Def def = response.body().getDef().get(0);
                    StringBuilder builder = buildSynonymText(def);

                    mSynonymText.setText(builder.toString());
                }
            }

            // Составление текста для mSynonymText, с использованием Dictionary API
            @NonNull
            private StringBuilder buildSynonymText(Def def) {
                StringBuilder builder = new StringBuilder();
                int index = 1;
                for (Tr tr : def.getTr()) {
                    builder.append(index++).append(". ");
                    builder.append(tr.getText()).append("\n");
                    if (tr.getSyn() != null) {
                        for (Syn syn : tr.getSyn()) {
                            builder.append(", ").append(syn.getText());
                        }
                        builder.append("\n");
                    }

                    if (tr.getMean() != null) {
                        builder.append("    (");
                        for (Mean mean : tr.getMean()) {
                            builder.append(mean.getText()).append(",");

                        }
                        builder.append(")");
                        builder.deleteCharAt(builder.lastIndexOf(","));
                        builder.append("\n");
                    }
                }
                builder.deleteCharAt(builder.indexOf("\n"));
                return builder;
            }

            @Override
            public void onFailure(Call<DataFromDictionary> call, Throwable t) {

            }
        });
    }

    // Инициализация spinners, языками которые получены из Translator API
    private void initialSpinners(final Spinner fromLangSpinner, final Spinner toLangSpinner) {
        App.getTranslatorApi().getLangs(
                getResources().getString(R.string.translator_api_key),
                nowSelectedFromLang
        ).enqueue(new Callback<LangsFromTranslator>() {
            @Override
            public void onResponse(Call<LangsFromTranslator> call, Response<LangsFromTranslator> response) {
                if (response.isSuccessful()) {
                    langs = response.body().getLangs();
                    ArrayList<String> langsList = new ArrayList<>();
                    langsList.addAll(langs.values());
                    adapter =
                            new ArrayAdapter<>(getActivity(),
                                    android.R.layout.simple_spinner_item,
                                    langsList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    fromLangSpinner.setAdapter(adapter);
                    toLangSpinner.setAdapter(adapter);

                    fromLangSpinner.setSelection(adapter.getPosition(langs.get(nowSelectedFromLang)));
                    toLangSpinner.setSelection(adapter.getPosition(langs.get(nowSelectedToLang)));
                } else {
                    Toast.makeText(getActivity(), "Ответ не получен", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LangsFromTranslator> call, Throwable t) {

            }
        });
    }

    // Обмен значений spinner при нажатии на reverseImageView
    public void reverseLangs() {
        String tmp = nowSelectedFromLang;
        nowSelectedFromLang = nowSelectedToLang;
        nowSelectedToLang = tmp;
        fromLangSpinner.setSelection(adapter.getPosition(langs.get(nowSelectedFromLang)));
        toLangSpinner.setSelection(adapter.getPosition(langs.get(nowSelectedToLang)));
    }
}
