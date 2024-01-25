package com.example.vyatsuapp;

import static com.example.vyatsuapp.utils.NetworkUtils.generateURL;
import static com.example.vyatsuapp.utils.ParseToGetFaculties.getFaculties;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private TextView result;
    private static final String VyatsuTimeTableURL = "https://www.vyatsu.ru/studentu-1/spravochnaya-informatsiya/raspisanie-zanyatiy-dlya-studentov.html";
    String[] faculty = {"ПЕД", "ИБиБ", "ХиЭ", "ФАВТ", "ФИПНиК", "ФКиФМН"};

    private class NetworkTask extends AsyncTask<Void, Void, Document> {
        @Override
        protected Document doInBackground(Void... params) {
            try {
                return Jsoup.connect(VyatsuTimeTableURL).get();
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

        ArrayAdapter<String> facultyAdapter = new ArrayAdapter<>(this,
                                            android.R.layout.simple_spinner_item, faculty);
        facultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spFaculty = (Spinner) findViewById(R.id.chooseFaculty);
        spFaculty.setAdapter(facultyAdapter);


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NetworkTask().execute();
            }
        };

        confirmCourseFacultyButton.setOnClickListener(onClickListener);

    }
}