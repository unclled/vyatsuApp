package com.example.vyatsuapp.Pages.Timetable;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        List<String> timetableDataList = new ArrayList<>(); // Здесь должны быть ваши данные
        adapter = new TimetableAdapter(timetableDataList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void setText(String timetableText) {
        runOnUiThread(() -> {
            String[] days = timetableText.split("\n\n\n"); // Разделяем дни
            List<String> timetableDataList = new ArrayList<>();
            for (String day : days) {
                if (!day.trim().isEmpty()) {
                    timetableDataList.add(day);
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
        runOnUiThread(() -> lastUpdate.setText("Обновлено: " + dateText + " " + month + " " + timeText));
        setText(presenter.getAllTimetable().toString());
        /* TODO  нормальное отображение времени + сохранение последнего обновления + UI */
    }

    @Override
    public void timetablePressed(View view) {
        setHeaderText("Расписание");
    }

    @Override
    public void settingsPressed(View view) {
        setHeaderText("Настройки");
    }

    @Override
    public Context getContext() {
        return this;
    }
}
