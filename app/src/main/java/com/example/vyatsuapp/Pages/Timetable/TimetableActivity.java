package com.example.vyatsuapp.Pages.Timetable;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vyatsuapp.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimetableActivity extends AppCompatActivity implements Timetable.View {
    private TextView text;
    private TextView header;

    Timetable.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable_activity);
        presenter = new TimetablePresenter();

        text = findViewById(R.id.timeTableURL);
        header = findViewById(R.id.WindowName);

        presenter.attachView(this); //привязка View к презентеру

        presenter.viewIsReady();

        String timetable = getHTMLTimetable();
        Thread thread = new Thread(() -> {
            StringBuilder allTimetable = parseTimetable(timetable);
            runOnUiThread(() -> text.setText(allTimetable));
        }); thread.start();
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public StringBuilder parseTimetable(String timetable) {
        //RelativeLayout layout = findViewById(R.id.Timetable);
        String day = getCurrentDay();
        Date currentDate, actualDate;
        try {
            currentDate = new SimpleDateFormat("dd.MM.yyyy").parse(day);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        StringBuilder allTimetable = new StringBuilder();
        Document document = Jsoup.parse(timetable);
        Elements programElements = document.select(".day-container");

        for (Element programElement : programElements) {
            Elements classesDesc = programElement.select(".font-normal");
            String receivedDate = classesDesc.select("b").text();
            int length = receivedDate.length();
            String date = receivedDate.substring(length - 11, length);
            try {
                actualDate = new SimpleDateFormat("dd.MM.yyyy").parse(date);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            if (currentDate.before(actualDate)) {
                allTimetable.append(receivedDate).append("\n\n");

/*                TextView showDate = new TextView(this);
                showDate.setTextColor(0xFF003FBC);
                showDate.setTextSize(20);
                showDate.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                showDate.setText(receivedDate);
                layout.addView(showDate);*/
                Elements dayClasses = programElement.select(".day-pair");

                for (Element currentClass : dayClasses) {
                    String classData = currentClass.select(".font-semibold").text();
                    String classDesc = currentClass.select(".pair_desc").text();

/*                    TextView showClassData = new TextView(this);
                    showClassData.setTextSize(14);
                    showClassData.setTextAlignment(TextView.TEXT_ALIGNMENT_VIEW_START);
                    showClassData.setText(classData);
                    layout.addView(showClassData);

                    TextView showClassDesc = new TextView(this);
                    showClassDesc.setTextSize(14);
                    showClassDesc.setTextAlignment(TextView.TEXT_ALIGNMENT_VIEW_START);
                    showClassDesc.setText(classDesc);
                    layout.addView(showClassDesc);*/
                    allTimetable.append(classData).append("\n").append(classDesc).append("\n\n");
                }

            }
        }

        return allTimetable;
    }

    @Override
    public String getCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE) - 1;
        int month = calendar.get(Calendar.MONTH) + 1;
        return day + "."
                + month + "."
                + calendar.get(Calendar.YEAR);
    }

    @Override
    public String getHTMLTimetable() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getString("HTMLResponse", null);
    }

    @Override
    public void timetablePressed(View view) {
        if (!header.getText().toString().equals("Расписание")) {
            Animation windowChange = AnimationUtils.loadAnimation(this, R.anim.window_change);
            header.startAnimation(windowChange);
            header.setText("Расписание");
        }
    }

    @Override
    public void settingsPressed(View view) {
        if (!header.getText().toString().equals("Настройки")) {
            Animation windowChange = AnimationUtils.loadAnimation(this, R.anim.window_change);
            header.startAnimation(windowChange);
            header.setText("Настройки");
        }
    }

    @Override
    public void personalDataPressed(View view) {
        if (!header.getText().toString().equals("Персональные данные")) {
            Animation windowChange = AnimationUtils.loadAnimation(this, R.anim.window_change);
            header.startAnimation(windowChange);
            header.setText("Персональные данные");
        }
    }

    @Override
    public void surveysPressed(View view) {
        if (!header.getText().toString().equals("Опросы")) {
            Animation windowChange = AnimationUtils.loadAnimation(this, R.anim.window_change);
            header.startAnimation(windowChange);
            header.setText("Опросы");
        }
    }

    @Override
    public void documentsPressed(View view) {
        if (!header.getText().toString().equals("Документы")) {
            Animation windowChange = AnimationUtils.loadAnimation(this, R.anim.window_change);
            header.startAnimation(windowChange);
            header.setText("Документы");
        }
    }
}
