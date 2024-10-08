package com.example.vyatsuapp.Utils.Resoponses;

import com.example.vyatsuapp.Utils.ServerRequests.AuthRequestBody;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthorizationAPI {
    @POST("https://new.vyatsu.ru/account/obr/rasp/")
    Call<ResponseBody> authUser(@Body AuthRequestBody body);
}
