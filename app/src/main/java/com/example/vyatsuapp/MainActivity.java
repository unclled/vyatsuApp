package com.example.vyatsuapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String VyatsuURL = "https://www.vyatsu.ru/";
    private final String FullTimeTimetable = "studentu-1/spravochnaya-informatsiya/raspisanie-zanyatiy-dlya-studentov.html";
    // расписание для очного обучения
    private final String DistanceCertification = "internet-gazeta/raspisanie-promezhutochnoy-attestatsii-obuchayusch-1.html";
    // расписание промежуточной аттестации и занятий для заочников
    private final String FullTimeAndDistance = "studentu-1/spravochnaya-informatsiya/raspisanie-zanyatiy-studentov-ochno-zaochnoy-formy.html";
    // расписание промежуточной аттестации и занятий для очно-заочного обучения
    private final String FullTimeCertification = "internet-gazeta/raspisanie-sessiy-obuchayuschihsya-na-2016-2017-uc.html";
    // расписание промежуточной аттестации для очников
    private final String PracticeCertification = "internet-gazeta/raspisanie-promezhutochnoy-attestatsii-obuchayusch-1.html";
    // расписание промежуточной аттестации для обучающихся по практике

    private final TextView result = findViewById(R.id.timetable);;

    private static final String[] typeOfEducation = {"Очно", "Очно-заочно", "Заочно"};

    EditText courseField = findViewById(R.id.Course);

    public void ClearAll(View view) {
        courseField.setText("");
        result.setText("Расписание будет здесь!");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button confirmCourseFacultyButton = findViewById(R.id.confirm_faculty_course);

        ArrayAdapter<String> facultyAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, typeOfEducation);
        facultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spFaculty = findViewById(R.id.chooseFaculty);
        spFaculty.setAdapter(facultyAdapter);

        View.OnClickListener onClickListener = v -> {
            String selectedItem = spFaculty.getSelectedItem().toString();

            String selectedEducationForm;
            if (selectedItem.equals("Очно")) {
                selectedEducationForm = FullTimeTimetable;
            } else if (selectedItem.equals("Очно-заочно")) {
                selectedEducationForm = FullTimeAndDistance;
            } else {
                selectedEducationForm = DistanceCertification;
            }

            Thread thread = new Thread(() -> {
                try {
                    URL url = new URL(VyatsuURL + selectedEducationForm);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            response.append(line);
                        }
                        bufferedReader.close();
                        inputStream.close();

                        final String htmlResponse = response.toString();
                        Document document = Jsoup.parse(htmlResponse);
                        final String facultyText = document.select("div.fak_name").text();

                        runOnUiThread(() -> result.setText(facultyText));
                    }

                    connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            thread.start();
        };

        confirmCourseFacultyButton.setOnClickListener(onClickListener);
    }
}