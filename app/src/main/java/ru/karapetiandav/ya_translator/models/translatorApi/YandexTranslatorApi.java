package ru.karapetiandav.ya_translator.models.translatorApi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface YandexTranslatorApi {
    @GET("/api/v1.5/tr.json/translate?")
    Call<DataFromTranslator> getTranslatedData(
            @Query("key") String key,
            @Query("text") String textToTranslate,
            @Query("lang") String language);

    @GET("/api/v1.5/tr.json/getLangs?")
    Call<LangsFromTranslator> getLangs(
            @Query("key") String key,
            @Query("ui") String ui);
}
