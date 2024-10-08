package com.example.vyatsuapp.Pages.Authorization;

import androidx.annotation.NonNull;

import com.example.vyatsuapp.Utils.MethodsForMVP.PresenterBase;
import com.example.vyatsuapp.Utils.Resoponses.AuthorizationAPI;
import com.example.vyatsuapp.Utils.ServerRequests.AuthRequestBody;
import com.example.vyatsuapp.Utils.ServerRequests.BasicAuthInterceptor;
import com.example.vyatsuapp.Utils.UtilsClass;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
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
    private final UtilsClass utils = new UtilsClass();

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

        AuthRequestBody body = new AuthRequestBody();
        AuthorizationAPI api = retrofit.create(AuthorizationAPI.class);

        Call<ResponseBody> call = api.authUser(body);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        try {
                            BufferedSource source = responseBody.source();
                            String htmlContent = source.readUtf8();

                            if (htmlContent.contains("Выйти")) {
                                htmlContent = htmlContent.substring(29000, htmlContent.length() - 80000);
                                applyHTMLResponse(htmlContent);
                                saveUserInfo();
                                getView().unlockWindow();
                                getView().toNextPage(htmlContent);
                            } else {
                                getView().tryAgain("Некорректный логин или пароль!");
                                getView().unlockWindow();
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        getView().tryAgain("Некорректный логин или пароль!");
                        getView().unlockWindow();
                    }
                } else {
                    getView().tryAgain("Некорректный логин или пароль!");
                    getView().unlockWindow();
                }
            }


            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    getView().tryAgain("Время ожидания истекло, попробуйте еще раз!");
                } else {
                    getView().tryAgain("Ошибка при выполнении запроса!");
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
        utils.toMapAndSaveSP("HTML_RESPONSE", htmlContent, getView().getContext());
    }

    @Override
    public void saveUserInfo() {
        Map<String, String> values = new HashMap<>();
        values.put("USER_LOGIN", getView().getLogin());
        values.put("USER_PASSWORD", getView().getPassword());
        utils.saveToPreferences(values, getView().getContext());
    }

}
