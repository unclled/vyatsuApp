package com.example.vyatsuapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.vyatsuapp.utils.EducationInfo;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public PowerSpinnerView spTypeOfEducation;
    public PowerSpinnerView spFaculties;
    public PowerSpinnerView spGroups;

    public Button confirmCourseFacultyButton;

    public EditText courseField;

    private String selected_TypeEd = null;
    private String selected_Faculty = null;
    private String selected_Group = null;
    private int Course = 0;
    private int Semester = 1;

    private ProgressBar progressBar;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.MONTH) <= 8) {
            Semester = 2;
        } else {
            Semester = 1;
        }

        spTypeOfEducation = findViewById(R.id.EducationTypeSpinner);
        spFaculties = findViewById(R.id.FacultiesSpinner);
        spGroups = findViewById(R.id.GroupSpinner);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        selected_TypeEd = sharedPreferences.getString("EducationType", null);
        selected_Faculty = sharedPreferences.getString("Faculty", null);
        Course = sharedPreferences.getInt("Course", 0);
        selected_Group = sharedPreferences.getString("Group", null);

        courseField = findViewById(R.id.Course);
        confirmCourseFacultyButton = findViewById(R.id.confirm_faculty_course);
        progressBar = findViewById(R.id.progressBar);

        isFirstStart();
    }

    private void isFirstStart() {
        SharedPreferences sp = getSharedPreferences("hasStudentInfo", Context.MODE_PRIVATE);
        boolean hasStudentInfo = sp.getBoolean("hasStudentInfo", false);
        System.out.println(hasStudentInfo);
        if (!hasStudentInfo) { //Если нет информации о студенте
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("hasStudentInfo", true);
            editor.apply();
            getStudentInfo();
        } else {
                //создать новую активность
        }
    }

    private void getStudentInfo() {
        ///////////////////////////Обработка выбора в выпадающих списках///////////////////////////
        spTypeOfEducation.setOnSpinnerItemSelectedListener(
                (OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
            selected_TypeEd = newItem;
            String[] selected_Faculties = switch (newItem) {
                case "Бакалавр" -> getResources().getStringArray(R.array.FullTimeBachelorFacs);
                case "Специалист" -> getResources().getStringArray(R.array.FullTimeSpecFacs);
                case "Магистр" -> getResources().getStringArray(R.array.FullTimeMasterFacs);
                default -> getResources().getStringArray(R.array.FullTimeGraduateFacs);
            };
            spFaculties.setVisibility(View.VISIBLE);
            List<String> Faculties = new ArrayList<>(Arrays.asList(selected_Faculties));
            spFaculties.setItems(Faculties);

            spFaculties.setOnSpinnerItemSelectedListener(
                    (OnSpinnerItemSelectedListener<String>) (oldIndex1, oldItem1, newIndex1, newItem1) -> {
                selected_Faculty = newItem1;
                courseField.setVisibility(View.VISIBLE);
            });
        });
    }

    public void ConfirmButtonPressed(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        if (selected_Faculty == null || selected_TypeEd == null || courseField.getText().toString().equals("")) {
            Toast toast = Toast.makeText(
                    this,
                    "Заполните все поля!",
                    Toast.LENGTH_LONG);
            toast.show();
        }
        else {
            Course = Integer.parseInt(courseField.getText().toString());
            progressBar.setVisibility(ProgressBar.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Thread thread = new Thread(() -> {
                try {
                    EducationInfo educationInfo = new EducationInfo(
                            selected_TypeEd,
                            selected_Faculty,
                            Course,
                            Semester);
                    String receivedInfo = educationInfo.ConnectAndGetInfo();

                    runOnUiThread(() -> {
                        List<String> groups;
                        groups = educationInfo.getGroups();
                        spGroups.setItems(groups);
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                        spGroups.setVisibility(View.VISIBLE);

                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        Button SaveButton = findViewById(R.id.SaveButton);

                        confirmCourseFacultyButton.setVisibility(View.GONE);
                        SaveButton.setVisibility(View.VISIBLE);

                        spGroups.setOnSpinnerItemSelectedListener(
                                (OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) ->
                                        selected_Group = newItem);
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            thread.start();
        }
    }

    public void SaveButtonPressed(View view) {
        if (selected_Group != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("EducationType", selected_TypeEd);
            editor.putString("Faculty", selected_Faculty);
            editor.putInt("Course", Course);
            editor.putString("Group", selected_Group);
            editor.apply();
        }
    }

    public void ClearAll(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        spTypeOfEducation.clearSelectedItem();
        spFaculties.clearSelectedItem();
        spGroups.clearSelectedItem();

        getStudentInfo();

        spGroups.setVisibility(View.GONE);
        courseField.setVisibility(View.GONE);
        spFaculties.setVisibility(View.GONE);
        spTypeOfEducation.setVisibility(View.VISIBLE);
        confirmCourseFacultyButton.setVisibility(View.VISIBLE);
        Button SaveButton = findViewById(R.id.SaveButton);
        SaveButton.setVisibility(View.GONE);
    }
}