package ru.karapetiandav.ya_translator.models.dictionaryApi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface YandexDictionaryApi {
    @GET("/api/v1/dicservice.json/lookup?")
    Call<DataFromDictionary> getDataFromDictionary(
            @Query("key") String dictApiKey,
            @Query("lang") String language,
            @Query("text") String text);
}
