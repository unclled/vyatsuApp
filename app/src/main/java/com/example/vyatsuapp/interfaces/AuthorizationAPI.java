package com.example.vyatsuapp.interfaces;

import com.example.vyatsuapp.utils.AuthRequestBody;
import com.example.vyatsuapp.utils.AuthResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthorizationAPI {
    @POST("/auth")
    Call<AuthResponse> authUser(@Body AuthRequestBody body);
}
