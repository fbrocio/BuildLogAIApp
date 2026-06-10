package com.example.buildlogai;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    public static final String BASE_URL = "https://buildlogai-api.onrender.com/";

    private static Retrofit retrofit;

    public static Retrofit getClient(Context context) {

        if (retrofit == null) {

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(180, TimeUnit.SECONDS)
                    .writeTimeout(180, TimeUnit.SECONDS)
                    .addInterceptor(chain -> {

                        SharedPreferences prefs =
                                context.getSharedPreferences("app", Context.MODE_PRIVATE);

                        String token = prefs.getString("token", null);

                        Request.Builder builder = chain.request().newBuilder();

                        if (token != null) {
                            builder.addHeader("Authorization", "Bearer " + token);
                        }

                        return chain.proceed(builder.build());
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}
