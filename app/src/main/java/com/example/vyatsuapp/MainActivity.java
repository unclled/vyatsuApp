package com.example.vyatsuapp;

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
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vyatsuapp.interfaces.AuthorizationAPI;
import com.example.vyatsuapp.utils.AuthRequestBody;
import com.example.vyatsuapp.utils.AuthResponse;
import com.example.vyatsuapp.utils.ResponceInterceptor;
import com.github.leandroborgesferreira.loadingbutton.BuildConfig;
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    public CircularProgressButton LoginButton;

    private static final String LKVyatsuURL = "https://new.vyatsu.ru/";
    private final String Account = "account/";//главная
    private final String InfoAboutStudy="obr/";//учеба
    private final String TimeTable="rasp";//расписание

    public TextInputLayout loginField;
    public TextInputLayout passwordField;

    private String loginText = null;
    private String passwordText = null;

    private SharedPreferences sharedPreferences;

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginField = findViewById(R.id.LoginField);
        passwordField = findViewById(R.id.PasswordField);
        LoginButton = findViewById(R.id.LoginButton);
        bitmap = BitmapFactory.decodeResource(getResources(), com.github.leandroborgesferreira.loadingbutton.R.drawable.ic_done_white_48dp);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        loginText = sharedPreferences.getString("UserLogin", null);
        passwordText = sharedPreferences.getString("UserPassword", null);

        isFirstStart();
    }

    private void isFirstStart() {
        SharedPreferences sp = getSharedPreferences("hasStudentInfo", Context.MODE_PRIVATE);
        boolean hasStudentInfo = sp.getBoolean("hasStudentInfo", false);

        if (!hasStudentInfo || loginText == null || passwordText == null) { //Если нет какой-либо информации о студенте
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("hasStudentInfo", true);
            editor.apply();
        } else { //запуск активности с расписанием
            loginField.setPlaceholderText(loginText);
            passwordField.setPlaceholderText(passwordText);
            LoginButton.startAnimation();
            getAuthorization();
        }
    }

    public void LoginPressed(View view) {
        if (!isOnline()) tryAgain("Отсутствует подключение к интернету!");

        if (loginField.getEditText() != null && passwordField.getEditText() != null) {
            loginText = loginField.getEditText().getText().toString();
            passwordText = passwordField.getEditText().getText().toString();

            Pattern pattern = Pattern.compile("^(stud\\\\?[0-9]{6})|^(cstud\\\\?[0-9]{6})|^(usr\\\\?[0-9]{6})/gm");
            Matcher matcher = pattern.matcher(loginText);
            if (matcher.find()) {
                LoginButton.startAnimation();
                getAuthorization();
            }
        } else {
            tryAgain("Заполните все поля!");
        }
    }

    private void getAuthorization(){
        // Создаем логгер HTTP-запросов только в режиме отладки
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        if(BuildConfig.DEBUG){//Это проверка условия на режим отладки
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        }
        // Создаем клиент OkHttpClient с настройками логирования
        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(new ResponseInterceptor())
                .addInterceptor(loggingInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://new.vyatsu.ru/account/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        AuthRequestBody body = new AuthRequestBody(loginText, passwordText);
        AuthorizationAPI api = retrofit.create(AuthorizationAPI.class);

        Call<AuthResponse> call = api.authUser(body);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                if (response.isSuccessful()) {
                    AuthResponse serverAnswer = response.body();

                    if (serverAnswer != null && serverAnswer.isUserLoginIn()) {
                        saveUserInfo();
                        toNextPage();
                    }
                } else {
                    tryAgain("Ошибка при выполнении запроса!_1");
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                Log.e("RequestFailure", "Ошибка при выполнении запроса!_3", t);
                tryAgain("Ошибка при выполнении запроса!_2");

            }
        });
    }

/*    public void ConfirmButtonPressed(View view) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            //getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Thread thread = new Thread(() -> {
                        try {
                            runOnUiThread(() -> {});
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                    thread.start();
    }*/

    private void saveUserInfo() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UserLogin", loginText);
        editor.putString("UserPassword", passwordText);
        editor.apply();
    }

    private void tryAgain(String text) {
        LoginButton.revertAnimation();
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        LoginButton.startAnimation(shake);
        Toast toast = Toast.makeText(
                this,
                text,
                Toast.LENGTH_LONG);
        toast.show();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void toNextPage() {
        View animate_view = findViewById(R.id.animate_view);
        int[] coordinates = new int[2];
        LoginButton.getLocationInWindow(coordinates);
        int cx = coordinates[0] + LoginButton.getWidth() / 2 - animate_view.getLeft();
        int cy = coordinates[1] + LoginButton.getHeight() / 2 - animate_view.getTop() - 100;
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
                    startActivity(new Intent(getApplicationContext(), BasicMainActivity.class));
                    overridePendingTransition(0, 0);
                }

                @Override
                public void onAnimationCancel(@NonNull Animator animation) {}
                @Override
                public void onAnimationRepeat(@NonNull Animator animation) {}
            });
        }, 700);
    }
}