package com.example.vyatsuapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private TextView result;
    private static final String VyatsuTimeTableURL = "https://www.vyatsu.ru/studentu-1/spravochnaya-informatsiya/raspisanie-zanyatiy-dlya-studentov.html";
    private static final String fullTimeTimeTable = "123456";
    String[] faculty = {"ПЕД", "ИБиБ", "ХиЭ", "ФАВТ", "ФИПНиК", "ФКиФМН"};


    public void ClearAll(View view) {
        EditText courseField = findViewById(R.id.Course);
        Spinner spFaculty = findViewById(R.id.chooseFaculty);
        result = findViewById(R.id.timetable);

        courseField.clearComposingText();
        result.setText("Расписание будет здесь!");
    }


    private class NetworkTask extends AsyncTask<Void, Void, Document> {
        @Override
        protected Document doInBackground(Void... params) {
            try {
                return Jsoup.connect(VyatsuTimeTableURL).maxBodySize(0).get();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Document document) {
            if (document != null) {
                var faculty = document.select("h4");
                String facultyText = faculty.text();

                result.setText(facultyText);
            } else {

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText courseField = findViewById(R.id.Course);
        Button confirmCourseFacultyButton = findViewById(R.id.confirm_faculty_course);
        result = findViewById(R.id.timetable);

        Spinner spFaculty = (Spinner) findViewById(R.id.chooseFaculty);
        ArrayAdapter<String> facultyAdapter = new ArrayAdapter<>(this,
                                            android.R.layout.simple_spinner_item, faculty);
        facultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFaculty.setAdapter(facultyAdapter);

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item = (String)parent.getItemAtPosition(position);
                result.setText(item);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                String Item = "Выберите ваш факультет";
                result.setText(Item);
            }
        };


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NetworkTask().execute();
            }
        };

        confirmCourseFacultyButton.setOnClickListener(onClickListener);

    }
}