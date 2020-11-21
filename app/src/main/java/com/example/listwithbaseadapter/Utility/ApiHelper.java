package com.example.listwithbaseadapter.Utility;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiHelper {

    public static String Insert(String strUrl,String paramValue){
        String result = "";
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody body = RequestBody.create(null,new byte[0]);

        Request request = new Request.Builder()
                .url(strUrl+paramValue)
                .method("POST",body)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if(response.isSuccessful()){
                result = response.body().string();
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
