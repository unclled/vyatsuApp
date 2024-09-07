package com.example.vyatsuapp.Pages.Timetable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vyatsuapp.Pages.Authorization.AuthorizationActivity;
import com.example.vyatsuapp.R;
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TimetableActivity extends AppCompatActivity implements Timetable.View {
    private TextView text;
    private TextView header;
    private TextView lastUpdate;
    private RecyclerView recyclerView;
    private TimetableAdapter adapter;
    private Timetable.Presenter presenter;

    private CircularProgressButton updateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable_activity);

        presenter = new TimetablePresenter();
        presenter.attachView(this);
        presenter.viewIsReady();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        lastUpdate = findViewById(R.id.lastUpdate);
        updateButton = findViewById(R.id.updateButton);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        runOnUiThread(() -> lastUpdate.setText(sharedPreferences.getString("LASTUPDATE", null)));
        recyclerView.setAdapter(adapter);

        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public void setText(String timetableText) {
        runOnUiThread(() -> {
            String[] days = timetableText.split("\n\n\n"); // Разделяем дни
            List<String> timetableDataList = new ArrayList<>();
            for (String day : days) {
                if (!day.trim().isEmpty()) {
                    timetableDataList.add(day.trim());
                }
            }
            adapter = new TimetableAdapter(timetableDataList);
            recyclerView.setAdapter(adapter);
        });
    }


    @Override
    public void setHeaderText(String text) {
        if (!header.getText().toString().equals(text)) {
            Animation windowChange = AnimationUtils.loadAnimation(this, R.anim.window_change);
            header.startAnimation(windowChange);
            header.setText(text);
        }
    }

    @Override
    public void updatePressed(View view) {
        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.update_rotate);
        updateButton.startAnimation(rotate);
        presenter.getLoginAndPassword();
    }

    @Override
    public void updateLastAuthorization() {
        updateButton.clearAnimation();
        Calendar calendar = Calendar.getInstance();
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd", Locale.getDefault());
        String dateText = dateFormat.format(currentDate);
        DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String timeText = timeFormat.format(currentDate);
        String month = calendar.getDisplayName(Calendar.MONTH,
                Calendar.LONG_FORMAT, new Locale("ru"));
        String updated = "Обновлено: " + dateText + " " + month + " " + timeText;
        runOnUiThread(() -> lastUpdate.setText(updated));
        setText(presenter.getAllTimetable().toString());
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("LASTUPDATE", updated);
        editor.apply();
    }

    @Override
    public void logoutPressed(View view) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("UserLogin");
        editor.remove("UserPassword");
        editor.remove("hasStudentInfo");
        editor.apply();
        Intent intent = new Intent(TimetableActivity.this, AuthorizationActivity.class);
        startActivity(intent);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        //запрет возвращаться назад
    }
}
