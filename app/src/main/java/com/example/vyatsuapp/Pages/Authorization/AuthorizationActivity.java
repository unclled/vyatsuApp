package com.example.vyatsuapp.Pages.Authorization;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vyatsuapp.Pages.Timetable.TimetableActivity;
import com.example.vyatsuapp.R;
import com.example.vyatsuapp.utils.AuthRequestBody;
import com.example.vyatsuapp.utils.BasicAuthInterceptor;
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthorizationActivity extends AppCompatActivity implements Authorization.View {

    public CircularProgressButton LoginButton;

    public TextInputLayout loginField;
    public TextInputLayout passwordField;

    private String loginText = null;
    private String passwordText = null;

    Bitmap bitmap;

    private SharedPreferences sharedPreferences;

    Authorization.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authorization_activity);
        presenter = new AuthorizationPresenter();

        loginField = findViewById(R.id.LoginField);
        passwordField = findViewById(R.id.PasswordField);
        LoginButton = findViewById(R.id.LoginButton);
        bitmap = BitmapFactory.decodeResource(getResources(), com.github.leandroborgesferreira.loadingbutton.R.drawable.ic_done_white_48dp);

        presenter.attachView(this); //привязка View к презентеру

        presenter.viewIsReady();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public void loginPressed(View view) {
        if (!isOnline()) tryAgain("Отсутствует подключение к интернету!");

        if (loginField.getEditText() != null && passwordField.getEditText() != null) {
            loginText = getLogin();
            passwordText = getPassword();

            presenter.checkLogin(loginText);

        } else {
            tryAgain("Заполните все поля!");
        }
    }

    @Override
    public void getAuthorization() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

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

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("HTMLResponse", htmlContent);
                            editor.apply();

                            saveUserInfo();
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            toNextPage(htmlContent);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    tryAgain("Некорректный логин или пароль!");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    tryAgain("Время ожидания истекло, попробуйте еще раз!");
                } else {
                    tryAgain("Ошибка при выполнении запроса_2!");
                }
            }
        });
    }

    @Override
    public void saveUserInfo() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UserLogin", loginText);
        editor.putString("UserPassword", passwordText);
        editor.apply();
    }

    @Override
    public void tryAgain(String text) {
        LoginButton.revertAnimation();
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        LoginButton.startAnimation(shake);
        Toast toast = Toast.makeText(
                this,
                text,
                Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void toNextPage(String htmlContent) {
        View animate_view = findViewById(R.id.animate_view);
        int[] coordinates = new int[2];
        LoginButton.getLocationInWindow(coordinates);
        int cx = coordinates[0] + LoginButton.getWidth() / 2 - animate_view.getLeft();
        int cy = coordinates[1] + LoginButton.getHeight() / 2 - animate_view.getTop() - 130;
        LoginButton.doneLoadingAnimation(1, bitmap);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Animator anim = ViewAnimationUtils.createCircularReveal(animate_view, cx, cy, 0f, getResources().getDisplayMetrics().heightPixels * 1.2f);
            anim.setDuration(500);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            animate_view.setVisibility(View.VISIBLE);
            anim.start();

            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(@NonNull Animator animation) {
                    handler.postDelayed(() -> {
                        LoginButton.revertAnimation();
                        animate_view.setVisibility(View.INVISIBLE);
                    }, 200);
                }

                @Override
                public void onAnimationEnd(@NonNull Animator animation) {
                    Intent intent = new Intent(AuthorizationActivity.this, TimetableActivity.class);
                    intent.putExtra("TIMETABLE", htmlContent);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }

                @Override
                public void onAnimationCancel(@NonNull Animator animation) {}
                @Override
                public void onAnimationRepeat(@NonNull Animator animation) {}
            });
        }, 700);
    }

    @Override
    public void setLogin(String login) {
        loginText = login;
    }

    @Override
    public void setPassword(String password) {
        passwordText = password;
    }

    @Override
    public void startLoading() {
        LoginButton.startAnimation();
    }

    @Override
    public String getLogin() {
        return loginField.getEditText().getText().toString();
    }

    @Override
    public String getPassword() {
        return passwordField.getEditText().getText().toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /* TODO работа с утечкой памяти у гитхабовских библиотек */
        presenter.detachView(); //освобождаем View
        if (isFinishing()) {
            presenter.destroy();
        }
    }
}
