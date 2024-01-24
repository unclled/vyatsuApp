package com.example.vyatsuapp;

import static com.example.vyatsuapp.utils.NetworkUtils.generateURL;
import static com.example.vyatsuapp.utils.NetworkUtils.getResponseFromURL;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private TextView result;

    String[] faculty = {"ПЕД", "ИБиБ", "ХиЭ", "ФАВТ", "ФИПНиК", "ФКиФМН"};

    class QueryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... urls) {
            return getResponseFromURL(urls[0]);
        }

        @Override
        protected void onPostExecute(String response) {
            result.setText(response);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText courseField = findViewById(R.id.Course);
        Button confirmCourseFacultyButton = findViewById(R.id.confirm_faculty_course);
        result = findViewById(R.id.timetable);

        ArrayAdapter<String> facultyAdapter = new ArrayAdapter<>(this,
                                            android.R.layout.simple_spinner_item, faculty);
        facultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spFaculty = (Spinner) findViewById(R.id.chooseFaculty);
        spFaculty.setAdapter(facultyAdapter);


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                URL generatedURL = generateURL();
                new QueryTask().execute(generatedURL);
            }
        };

        confirmCourseFacultyButton.setOnClickListener(onClickListener);

    }
}