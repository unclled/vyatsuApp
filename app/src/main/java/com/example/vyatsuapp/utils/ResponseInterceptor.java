package com.example.vyatsuapp.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public record ResponseInterceptor() implements Interceptor {
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException { //Логирование запросов и ответов
            Request request = chain.request(); //отправленый хттп запрос
            long start_time = System.nanoTime();
            Log.d("Request", String.format("Sending_request",request, request.url(),chain.connection(),request.headers()));
            Response response = chain.proceed(request); //полученный хттп запрос
            long end_time = System.nanoTime();
            Log.d("Response", String.format("Received response for %s in %.1fms%n%s", response.request().url(), (end_time - start_time) / 1e6d, response.headers()));
            return response;
    }
}
