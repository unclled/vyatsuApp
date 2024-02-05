package com.example.vyatsuapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class BasicMainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_main_activity);

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

            try {

                runOnUiThread(() -> text.setText(""));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
}