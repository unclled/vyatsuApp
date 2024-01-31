package com.example.vyatsuapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vyatsuapp.utils.EducationInfo;
import com.example.vyatsuapp.utils.NothingSelectedSpinnerAdapter;
import com.example.vyatsuapp.utils.dropdownLists;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView result;

    private static final String[] typeOfEducation = {"Бакалавр", "Специалист", "Магистр", "Аспирант"};

    public Spinner spTypeOfEducation;
    public Spinner spFaculties;
    public Spinner spGroups;

    public Button confirmCourseFacultyButton;

    public EditText courseField;

    private String selected_TypeEd = null;
    private String selected_Faculty = null;
    private int Semester = 1;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.MONTH) <= 8) {
            Semester = 2;
        } else {
            Semester = 1;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        courseField = findViewById(R.id.Course);
        confirmCourseFacultyButton = findViewById(R.id.confirm_faculty_course);
        result = findViewById(R.id.timetable);
        spTypeOfEducation = findViewById(R.id.typeOfEducation);
        spFaculties = findViewById(R.id.Faculty);
        spGroups = findViewById(R.id.Groups);
        progressBar = findViewById(R.id.progressBar);

        spFaculties.setVisibility(View.GONE);
        spGroups.setVisibility(View.GONE);
        courseField.setVisibility(View.GONE);

        //////////////////////////Создание адаптеров и выпадающих списков//////////////////////////
        ArrayAdapter<String> typeOfEducation_Adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                typeOfEducation);
        typeOfEducation_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spTypeOfEducation.setAdapter(new NothingSelectedSpinnerAdapter(
                typeOfEducation_Adapter,
                R.layout.contact_spinner_row_nothing_selected2,
                this));
        ///////////////////////////////////////////////////////////////////////////////////////////

        ///////////////////////////Обработка выбора в выпадающих списках///////////////////////////
        spTypeOfEducation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object selectedItem_TypeEd = parent.getItemAtPosition(position);

                if (selectedItem_TypeEd != null) { //если что-то выбрано во втором списке
                    selected_TypeEd = selectedItem_TypeEd.toString();

                    //создаем экземпляр класса для оторбражения возможных факультетов
                    dropdownLists spFaculty = new dropdownLists(selected_TypeEd);
                    String[] chosenFaculties = spFaculty.spFacultyItems();

                    //создаем адаптер для отображения вариантов для последнего списка
                    ArrayAdapter<String> Faculties_Adapter = new ArrayAdapter<>(
                            MainActivity.this,
                            android.R.layout.simple_spinner_item,
                            chosenFaculties);
                    Faculties_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spFaculties.setPrompt("Выберите факультет");
                    spFaculties.setAdapter(new NothingSelectedSpinnerAdapter(
                            Faculties_Adapter,
                            R.layout.contact_spinner_row_nothing_selected3,
                            MainActivity.this));

                    spFaculties.setVisibility(View.VISIBLE);
                    spFaculties.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Object selectedItem_Faculty = parent.getItemAtPosition(position);

                            if (selectedItem_Faculty != null) { //если что-то выбрано во третьем списке
                                selected_Faculty = selectedItem_Faculty.toString();
                                courseField.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    }
    ///////////////////////////////////////////////////////////////////////////////////////////

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
            progressBar.setVisibility(ProgressBar.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            Thread thread = new Thread(() -> {
                try {
                    EducationInfo educationInfo = new EducationInfo(
                            selected_TypeEd,
                            selected_Faculty,
                            courseField,
                            Semester);
                    String receivedInfo = educationInfo.ConnectAndGetInfo();

                    runOnUiThread(() -> {
                        List<String> groups;
                        groups = educationInfo.getGroups();

                        ArrayAdapter<String> groups_Adapter = new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_spinner_item,
                                groups);
                        groups_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        spGroups.setAdapter(new NothingSelectedSpinnerAdapter(
                                groups_Adapter,
                                R.layout.contact_spinner_row_nothing_selected,
                                this));
                        spFaculties.setVisibility(View.GONE);
                        spTypeOfEducation.setVisibility(View.GONE);
                        courseField.setVisibility(View.GONE);
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        spGroups.setVisibility(View.VISIBLE);
                        result.setText(receivedInfo);
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            thread.start();
        }
    }

    public void ClearAll(View view) {
        spTypeOfEducation.setSelection(0);
        spFaculties.setSelection(0);
        EditText courseField = findViewById(R.id.Course);
        result = findViewById(R.id.timetable);
        courseField.setText("");
        spGroups.setVisibility(View.GONE);
        courseField.setVisibility(View.GONE);
        spFaculties.setVisibility(View.GONE);
        spTypeOfEducation.setVisibility(View.VISIBLE);
        result.setText("Расписание будет здесь");
    }
}