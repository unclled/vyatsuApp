package com.example.vyatsuapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BasicMainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    private TextView text;
    private TextView header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_main_activity);

        text = findViewById(R.id.timeTableURL);
        header = findViewById(R.id.WindowName);

        String timetable = (String) getIntent().getSerializableExtra("TIMETABLE");
        Thread thread = new Thread(() -> {
            StringBuilder allTimetable = parseTimetable(timetable);
            runOnUiThread(() -> text.setText(allTimetable));
        }); thread.start();
    }

    private StringBuilder parseTimetable(String timetable) {
        StringBuilder allTimetable = new StringBuilder();
        Document document = Jsoup.parse(timetable);
        Elements programElements = document.select(".day-container");

        for (Element programElement : programElements) {
            Elements classesDesc = programElement.select(".font-normal");
            String date = classesDesc.select("b").text();
            allTimetable.append(date).append("\n");
            Elements dayClasses = programElement.select(".day-pair");

            for (Element currentClass : dayClasses) {
                String classData = currentClass.select(".font-semibold").text();
                String classDesc = currentClass.select(".pair_desc").text();

                allTimetable.append(classData).append("\n").append(classDesc).append("\n");
            }

        }
        return allTimetable;
    }

    public void timetablePressed(View view) {
        Animation windowChange = AnimationUtils.loadAnimation(this, R.anim.window_change);
        header.startAnimation(windowChange);
        header.setText("Расписание");
    }

    public void SettingsPressed(View view) {
        Animation windowChange = AnimationUtils.loadAnimation(this, R.anim.window_change);
        header.startAnimation(windowChange);
        header.setText("Настройки");
    }

    public void PersonalDataPressed(View view) {
        Animation windowChange = AnimationUtils.loadAnimation(this, R.anim.window_change);
        header.startAnimation(windowChange);
        header.setText("Персональные данные");
    }

    public void SurveysPressed(View view) {
        Animation windowChange = AnimationUtils.loadAnimation(this, R.anim.window_change);
        header.startAnimation(windowChange);
        header.setText("Опросы");
    }

    public void DocumentsPressed(View view) {
        Animation windowChange = AnimationUtils.loadAnimation(this, R.anim.window_change);
        header.startAnimation(windowChange);
        header.setText("Документы");
    }
}