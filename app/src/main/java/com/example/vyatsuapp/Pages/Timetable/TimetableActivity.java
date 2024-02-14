package com.example.vyatsuapp.Pages.Timetable;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vyatsuapp.R;
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton;

import java.util.Calendar;

public class TimetableActivity extends AppCompatActivity implements Timetable.View {
    private TextView text;
    private TextView header;
    private TextView lastUpdate;

    Timetable.Presenter presenter;

    private CircularProgressButton updateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable_activity);
        presenter = new TimetablePresenter();

        text = findViewById(R.id.timetableInfo);
        header = findViewById(R.id.WindowName);
        lastUpdate = findViewById(R.id.lastUpdate);
        updateButton = findViewById(R.id.updateButton);

        presenter.attachView(this); //привязка View к презентеру

        presenter.viewIsReady();
    }

    @Override
    public void setText(String timetableText) {
        runOnUiThread(() -> text.setText(timetableText));
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
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH) + 1;
        int hours = calendar.get(Calendar.HOUR);
        int minutes = calendar.get(Calendar.MINUTE);
        runOnUiThread(() -> lastUpdate.setText("Последнее обновление:\n" + day + ". " + month + ", " + hours + ":" + minutes));
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
    public void personalDataPressed(View view) {
        setHeaderText("Персональные данные");
    }

    @Override
    public Context getContext() {
        return this;
    }
}
