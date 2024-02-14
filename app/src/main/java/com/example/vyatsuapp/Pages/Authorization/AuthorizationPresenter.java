package com.example.vyatsuapp.Pages.Authorization;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import com.example.vyatsuapp.Pages.PresenterBase;
import com.example.vyatsuapp.utils.AuthRequestBody;
import com.example.vyatsuapp.utils.BasicAuthInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthorizationPresenter extends PresenterBase<Authorization.View> implements Authorization.Presenter {
    private String loginText;

    @Override
    public void viewIsReady() {
        loginText = getView().getLogin();
    }

    public void getAuthorization() {
        loginText = getView().getLogin();
        String passwordText = getView().getPassword();
        getView().blockWindow();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // Время на подключение
                .readTimeout(30, TimeUnit.SECONDS) // Время на чтение
                .writeTimeout(30, TimeUnit.SECONDS) // Время на запись
                .addInterceptor(new BasicAuthInterceptor(loginText, passwordText))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://new.vyatsu.ru/account/obr/rasp/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        AuthRequestBody body = new AuthRequestBody(loginText, passwordText);
        AuthorizationAPI api = retrofit.create(AuthorizationAPI.class);

        Call<ResponseBody> call = api.authUser(body);
        call.enqueue(new Callback<>() {
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        try {
                            BufferedSource source = responseBody.source();
                            String htmlContent = source.readUtf8();

                            applyHTMLResponse(htmlContent);

                            saveUserInfo();
                            getView().unlockWindow();
                            getView().toNextPage(htmlContent);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    getView().tryAgain("Некорректный логин или пароль!");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    getView().tryAgain("Время ожидания истекло, попробуйте еще раз!");
                } else {
                    getView().tryAgain("Ошибка при выполнении запроса_2!");
                }
            }
        });
    }

    @Override
    public void checkLogin(String login) {
        Pattern pattern = Pattern.compile("^(stud\\\\?[0-9]{6})|^(cstud\\\\?[0-9]{6})|^(usr\\\\?[0-9]{6})/gm");
        Matcher matcher = pattern.matcher(login);
        if (matcher.find()) {
            getView().startLoading();
            getAuthorization();
        } else {
            getView().tryAgain("Неверно указан логин!");
        }
    }

    @Override
    public void applyHTMLResponse(String htmlContent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getView().getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("HTMLResponse", htmlContent);
        editor.apply();
    }

    @Override
    public void saveUserInfo() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getView().getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UserLogin", getView().getLogin());
        editor.putString("UserPassword", getView().getPassword());
        editor.apply();
    }

    @Override
    public void checkPassword(String password) {
        /* TODO проверка пароля */
    }

}
