package com.example.vyatsuapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vyatsuapp.Pages.Authorization.AuthorizationActivity;
import com.example.vyatsuapp.Pages.Timetable.TimetableActivity;

public class MainActivity extends AppCompatActivity {
    private String loginText = null;
    private String passwordText = null;
    private String htmlResponse = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        loginText = sharedPreferences.getString("UserLogin", null);
        passwordText = sharedPreferences.getString("UserPassword", null);
        htmlResponse = sharedPreferences.getString("HTMLResponse", null);

        isFirstStart();
    }

    private void isFirstStart() {
        SharedPreferences sp = getSharedPreferences("hasStudentInfo", Context.MODE_PRIVATE);
        boolean hasStudentInfo = sp.getBoolean("hasStudentInfo", false);

        if (!hasStudentInfo || loginText == null || passwordText == null || htmlResponse == null) { //Если нет какой-либо информации о студенте
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("hasStudentInfo", true);
            editor.apply();
            Intent intent = new Intent(MainActivity.this, AuthorizationActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        } else { //запуск активности с расписанием
            Intent intent = new Intent(MainActivity.this, TimetableActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }
}



