package com.example.vyatsuapp.utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class BasicAuthInterceptor implements Interceptor {
    private String credentials;

    public BasicAuthInterceptor(String username, String password) {
        this.credentials = Credentials.basic(username, password);
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request authenticatedRequest = chain.request().newBuilder()
                .header("Authorization", credentials)
                .build();
        return chain.proceed(authenticatedRequest);
    }
}
