package com.example.vyatsuapp.interfaces;

import com.example.vyatsuapp.utils.AuthRequestBody;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthorizationAPI {
    @POST("https://new.vyatsu.ru/account/obr/rasp/")
    Call<ResponseBody> authUser(@Body AuthRequestBody body);
}
