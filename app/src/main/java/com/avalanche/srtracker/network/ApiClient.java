package com.avalanche.srtracker.network;

import android.content.Context;

import com.avalanche.srtracker.model.Token;

import java.io.IOException;

import br.vince.easysave.EasySave;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static final String BASE_URL = "http://27.147.133.98:320";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getTokenClient(Context context){
        EasySave save = new EasySave(context.getApplicationContext());
        final Token token = save.retrieveModel("token", Token.class);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request newRequest  = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + token.getAccess_token())
                        .addHeader("HEADER", token.getAccess_token())
                        .addHeader("Accept", "application/json")
                        .build();
                return chain.proceed(newRequest);
            }
        }).build();

        final Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("http://27.147.133.98:320")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }
}
