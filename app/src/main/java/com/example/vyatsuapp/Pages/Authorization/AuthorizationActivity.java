package com.example.vyatsuapp.Pages.Authorization;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vyatsuapp.Pages.Timetable.TimetableActivity;
import com.example.vyatsuapp.R;
import com.example.vyatsuapp.Utils.UtilsClass;
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class AuthorizationActivity extends AppCompatActivity implements Authorization.View {

    public CircularProgressButton LoginButton;

    public TextInputLayout loginField;
    public TextInputLayout passwordField;

    private Bitmap bitmap;

    private Authorization.Presenter presenter;
    private final UtilsClass utils = new UtilsClass();

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

        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public void loginPressed(View view) {
        passwordField.clearFocus();
        loginField.clearFocus();
        if (!isOnline()) tryAgain("Отсутствует подключение к интернету!");

        if (editTextIsNull(getLoginField()) &&
                editTextIsNull(getPasswordField())) {

            presenter.checkLogin(getLogin());

        } else {
            tryAgain("Заполните все поля!");
        }
    }

    @Override
    public void tryAgain(String text) {
        LoginButton.revertAnimation();
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        LoginButton.startAnimation(shake);
        utils.showToastLong(text, this);
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
                public void onAnimationCancel(@NonNull Animator animation) {
                }

                @Override
                public void onAnimationRepeat(@NonNull Animator animation) {
                }
            });
        }, 700);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void blockWindow() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(
                getWindow().
                        getDecorView().
                        getWindowToken(),
                0);

        getWindow().setFlags(
                WindowManager.
                        LayoutParams.
                        FLAG_NOT_TOUCHABLE,
                WindowManager.
                        LayoutParams.
                        FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void unlockWindow() {
        getWindow().clearFlags(
                WindowManager.
                        LayoutParams.
                        FLAG_NOT_TOUCHABLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView(); //освобождаем View
        if (isFinishing()) {
            presenter.destroy();
        }
    }

    @Override
    public boolean editTextIsNull(TextInputLayout text) {
        return text.getEditText() != null;
    }

    @Override
    public void startLoading() {
        LoginButton.startAnimation();
    }

    @Override
    public String getLogin() {
        return Objects.requireNonNull(loginField.getEditText()).getText().toString();
    }

    @Override
    public String getPassword() {
        return Objects.requireNonNull(passwordField.getEditText()).getText().toString();
    }

    @Override
    public TextInputLayout getLoginField() {
        return loginField;
    }

    @Override
    public TextInputLayout getPasswordField() {
        return passwordField;
    }
}
