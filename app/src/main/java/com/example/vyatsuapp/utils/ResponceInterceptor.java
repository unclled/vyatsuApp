package com.example.vyatsuapp.utils;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public record ResponceInterceptor() implements Interceptor {
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
            return chain.proceed(chain.request());
    }
}
