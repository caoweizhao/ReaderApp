package com.example.caoweizhao.readerapp.util;

import com.example.caoweizhao.readerapp.Constant;
import com.example.caoweizhao.readerapp.StringConverter;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by caoweizhao on 2017-9-22.
 */

public class RetrofitUtil {

    private static Retrofit mRetrofit;

    public static synchronized Retrofit getRetrofit() {
        if (mRetrofit == null) {
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(Constant.BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(StringConverter.StringConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(new OkHttpClient.Builder()
                            /*.addInterceptor(new OkHttpLoggingInterceptor())
                            .connectTimeout(8000, TimeUnit.MILLISECONDS)
                            .readTimeout(8000, TimeUnit.MILLISECONDS)*/
                            .build())
                    .build();
        }

        return mRetrofit;
    }
}
