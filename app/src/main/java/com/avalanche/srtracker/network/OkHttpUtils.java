package com.avalanche.srtracker.network;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtils {

    MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client ;

    public OkHttpUtils(){

        OkHttpClient client = new OkHttpClient();
    }



    public String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
