package ru.karapetiandav.ya_translator;

import android.app.Application;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.karapetiandav.ya_translator.models.dictionaryApi.YandexDictionaryApi;
import ru.karapetiandav.ya_translator.models.translatorApi.YandexTranslatorApi;


public class App extends Application {

    private static YandexTranslatorApi translatorApi;
    private static YandexDictionaryApi dictionaryApi;
    private Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://translate.yandex.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        translatorApi = retrofit.create(YandexTranslatorApi.class);

        retrofit = new Retrofit.Builder()
                .baseUrl("https://dictionary.yandex.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        dictionaryApi = retrofit.create(YandexDictionaryApi.class);
    }

    public static YandexTranslatorApi getTranslatorApi() {
        return translatorApi;
    }

    public static YandexDictionaryApi getDictionaryApi() {
        return dictionaryApi;
    }
}
