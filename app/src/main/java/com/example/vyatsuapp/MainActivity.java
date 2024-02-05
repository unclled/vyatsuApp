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
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    public CircularProgressButton SaveButton;

    public TextInputEditText loginField;
    public TextInputEditText passwordField;

    private SharedPreferences sharedPreferences;

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginField = findViewById(R.id.LoginField);
        passwordField = findViewById(R.id.PasswordField);
        SaveButton = findViewById(R.id.SaveButton);
        bitmap = BitmapFactory.decodeResource(getResources(), com.github.leandroborgesferreira.loadingbutton.R.drawable.ic_done_white_48dp);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        isFirstStart();
    }

    private void isFirstStart() {
        SharedPreferences sp = getSharedPreferences("hasStudentInfo", Context.MODE_PRIVATE);
        boolean hasStudentInfo = sp.getBoolean("hasStudentInfo", false);

        if (!hasStudentInfo) {//||) { //Если нет какой-либо информации о студенте
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("hasStudentInfo", true);
            editor.apply();
            Calendar calendar = Calendar.getInstance();
            getStudentInfo();
        } else { //запуск активности с расписанием
            startActivity(new Intent(getApplicationContext(), BasicMainActivity.class));
            overridePendingTransition(0, 0);
        }
    }

    private void getStudentInfo() {

    }

    public void ConfirmButtonPressed(View view) {
        if (isOnline()) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            if (1 > 2) {
                Toast toast = Toast.makeText(
                        this,
                        "Заполните все поля!",
                        Toast.LENGTH_LONG);
                toast.show();
            } else {
                if (1 > 2) {
                    Toast toast = Toast.makeText(
                            this,
                            "Укажите реальный курс",
                            Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    //getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Thread thread = new Thread(() -> {
                        try {
                            runOnUiThread(() -> {});
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                    thread.start();
                }
            }
        } else {
            Toast toast = Toast.makeText(
                    this,
                    "Нет подключения к интернету!",
                    Toast.LENGTH_LONG);
            toast.show();
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        }
    }

    public void SaveButtonPressed(View view) {
        //if () {
            SaveButton.startAnimation();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.apply();
            toNextPage();
        //}
    }

    private void toNextPage() {
        View animate_view = findViewById(R.id.animate_view);
        int[] coordinates = new int[2];
        SaveButton.getLocationInWindow(coordinates);
        int cx = coordinates[0] + SaveButton.getWidth() / 2 - animate_view.getLeft();
        int cy = coordinates[1] + SaveButton.getHeight() / 2 - animate_view.getTop() - 100;
        SaveButton.doneLoadingAnimation(1, bitmap);

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
                        SaveButton.revertAnimation();
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

    public void ClearAll(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        getStudentInfo();
        SaveButton.setVisibility(View.GONE);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}