package com.crcexam.android.network;

import android.app.Activity;

import com.crcexam.android.constants.Constant;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitClient {
    Retrofit retrofit = null;

    public Retrofit getRetrogitClient(Activity activity) {
        if (CheckInternetConnection.isNetworkAvailable(activity)) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(5, java.util.concurrent.TimeUnit.MINUTES).readTimeout(5, java.util.concurrent.TimeUnit.MINUTES).addInterceptor(interceptor).build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(Constant.BASEURL_LOGIN)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client)
                    .build();
        }

        return retrofit;
    }

}
