package com.crcexam.android.network;

import android.app.Activity;

import com.crcexam.android.constants.Constant;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitLoggedIn {
    Retrofit retrofit = null;

    public Retrofit RetrofitClient(final Activity activity, boolean auth_header) {

        if (CheckInternetConnection.isNetworkAvailable(activity)) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

//            httpClient.addInterceptor(new Interceptor() {
//                @Override
//                public okhttp3.Response intercept(Chain chain) throws IOException {
//                    Request original = chain.request();
//
//                    Request.Builder requestBuilder = original.newBuilder()
//                            .header("Content-Type", "application/json");
//
//                    Request request = requestBuilder.build();
//
//                    return chain.proceed(request);
//                }
//            });

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            httpClient.addInterceptor(interceptor);

            OkHttpClient client = httpClient.connectTimeout(5, java.util.concurrent.TimeUnit.MINUTES).readTimeout(5, java.util.concurrent.TimeUnit.MINUTES).build();

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
