package com.example.vyatsuapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vyatsuapp.Pages.Authorization.AuthorizationActivity;
import com.example.vyatsuapp.Pages.Timetable.TimetableActivity;
import com.example.vyatsuapp.Utils.UtilsClass;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String loginText = null;
    private String passwordText = null;
    private String htmlResponse = null;
    private final UtilsClass utils = new UtilsClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<String> keys = new ArrayList<>();
        keys.add("USER_LOGIN");
        keys.add("USER_PASSWORD");
        keys.add("HTML_RESPONSE");
        keys.add("HAS_STUDENT_INFO");
        List<String> values = utils.loadFromPreferences(keys, this);
        loginText = values.isEmpty() ? null : values.get(0);
        passwordText = values.isEmpty() ? null : values.get(1);
        htmlResponse = values.isEmpty() ? null : values.get(2);
        boolean hasStudentInfo = values.isEmpty() ? Boolean.parseBoolean(null) : Boolean.parseBoolean(values.get(3));

        isFirstStart(hasStudentInfo);
    }

    private void isFirstStart(boolean hasStudentInfo) {
        if (!hasStudentInfo || loginText == null || passwordText == null || htmlResponse == null) {
            utils.toMapAndSaveSP("HAS_STUDENT_INFO", String.valueOf(true), this);
            Intent intent = new Intent(MainActivity.this, AuthorizationActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        } else {
            Intent intent = new Intent(MainActivity.this, TimetableActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }
}
