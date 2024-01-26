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

    private TextView result;

    private static final String[] typeOfEducation = {"Очно", "Очно-заочно", "Заочно"};
    Spinner spFaculty;
    Button confirmCourseFacultyButton;
    EditText courseField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        courseField = findViewById(R.id.Course);
        confirmCourseFacultyButton = findViewById(R.id.confirm_faculty_course);
        result = findViewById(R.id.timetable);
        ArrayAdapter<String> facultyAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, typeOfEducation);
        facultyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFaculty = findViewById(R.id.chooseFaculty);
        spFaculty.setAdapter(facultyAdapter);
    }

    public void ConfirmButtonPressed(View view) {
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
                    String url = VyatsuURL + selectedEducationForm;
                    var document = Jsoup.connect(url).maxBodySize(0).get();
                    String facultyText = document.select("div.fak_name").text();
                    String finalString = removeUnnecessary(facultyText);
                    runOnUiThread(() -> result.setText(finalString));
                    System.out.println(finalString);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }); thread.start();
    }

    public String removeUnnecessary(String faculty) {
        String clearedString = faculty;
        String trash = "\\(ОРУ\\)";
        clearedString = clearedString.replaceAll(trash, "");
        return clearedString;
    }

    public void ClearAll(View view) {
        EditText courseField = findViewById(R.id.Course);
        result = findViewById(R.id.timetable);
        courseField.setText("");
        result.setText("Расписание будет здесь!");
    }
}
