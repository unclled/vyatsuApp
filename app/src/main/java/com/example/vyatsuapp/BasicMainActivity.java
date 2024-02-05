package com.example.vyatsuapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vyatsuapp.utils.GetTimetable;
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

public class BasicMainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    private ProgressBar progressBar;

    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_main_activity);

        progressBar = findViewById(R.id.progressBar);
        text = findViewById(R.id.timeTableURL);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String selected_TypeEd = sharedPreferences.getString("EducationType", null);
        String selected_Faculty = sharedPreferences.getString("Faculty", null);
        String selected_Group = sharedPreferences.getString("Group", null);
        int Semester;
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.MONTH) <= 8) {
            Semester = 2;
        } else {
            Semester = 1;
        }
        Thread thread = new Thread(() -> {
            //progressBar.setVisibility(View.VISIBLE);
            GetTimetable getTimetable = new GetTimetable(selected_TypeEd, selected_Faculty, selected_Group, Semester);
            String uri = getTimetable.GetActualTimetable();
            String fullURI = "https://www.vyatsu.ru" + uri;
            try {
                PDFBoxResourceLoader.init(getApplicationContext());
                URL url = new URL(fullURI);
                URLConnection conn = url.openConnection();
                conn.connect();
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                File file = new File(getExternalFilesDir(null), "timetable.pdf");

                OutputStream output = new FileOutputStream(file);
                byte[] data = new byte[1024];
                int count;
                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();

                String[] classTime = getResources().getStringArray(R.array.ClassTime);
                getTimetable.setClassTime(classTime);
                String receivedData = getTimetable.ReadPDFTable(this);
                //progressBar.setVisibility(View.GONE);
                runOnUiThread(() -> text.setText(receivedData));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
}