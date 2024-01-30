package com.example.vyatsuapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vyatsuapp.utils.EducationInfo;
import com.example.vyatsuapp.utils.NothingSelectedSpinnerAdapter;
import com.example.vyatsuapp.utils.dropdownLists;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private TextView result;

    private static final String[] EducationalFormat = {"Очно", "Очно-заочно", "Заочно"};
    private static final String[] typeOfEducation = {"Бакалавр", "Специалист", "Магистр", "Аспирант"};

    public Spinner spEducationalFormat;
    public Spinner spTypeOfEducation;
    public Spinner spFaculties;

    public Button confirmCourseFacultyButton;

    public EditText courseField;

    private String selected_TypeEd = null;
    private String selected_EdForm = null;
    private String selected_Faculty = null;
    private int Semester = 1;


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
        spEducationalFormat = findViewById(R.id.EducationalFormat);
        spTypeOfEducation = findViewById(R.id.typeOfEducation);
        spFaculties = findViewById(R.id.Faculty);

        spTypeOfEducation.setVisibility(View.GONE);
        spFaculties.setVisibility(View.GONE);

        //////////////////////////Создание адаптеров и выпадающих списков//////////////////////////
        ArrayAdapter<String> EducationalFormat_Adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                EducationalFormat);
        EducationalFormat_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> typeOfEducation_Adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                typeOfEducation);
        typeOfEducation_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spTypeOfEducation.setAdapter(new NothingSelectedSpinnerAdapter(
                typeOfEducation_Adapter,
                R.layout.contact_spinner_row_nothing_selected,
                this));


        spEducationalFormat.setAdapter(new NothingSelectedSpinnerAdapter(
                EducationalFormat_Adapter,
                R.layout.contact_spinner_row_nothing_selected2,
                this));
        spEducationalFormat.setPrompt("Укажите тип обучения");
        ///////////////////////////////////////////////////////////////////////////////////////////

        ///////////////////////////Обработка выбора в выпадающих списках///////////////////////////
        spEducationalFormat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Object selectedItem_EdForm = parent.getItemAtPosition(position);

                if (selectedItem_EdForm != null) { //если что-то выбрано в первом списке
                    selected_EdForm = selectedItem_EdForm.toString();
                    spTypeOfEducation.setVisibility(View.VISIBLE); //отображаем второй список
                    spTypeOfEducation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Object selectedItem_TypeEd = parent.getItemAtPosition(position);

                            if (selectedItem_TypeEd != null) { //если что-то выбрано во втором списке
                                selected_TypeEd = selectedItem_TypeEd.toString();

                                //создаем экземпляр класса для оторбражения возможных факультетов
                                dropdownLists spFaculty = new dropdownLists(selected_EdForm, selected_TypeEd);
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
                                        }
                                    }
                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {}
                                });
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        ///////////////////////////////////////////////////////////////////////////////////////////
    }

    public void ConfirmButtonPressed(View view) {
        if (selected_EdForm == null || selected_Faculty == null
                || selected_TypeEd == null || courseField.getText().toString().equals("")) {
            Toast toast = Toast.makeText(
                    this,
                    "Заполните все поля!",
                    Toast.LENGTH_LONG);
            toast.show();
        }
        else {
            Thread thread = new Thread(() -> {
                try {
                    EducationInfo educationInfo = new EducationInfo(
                            selected_EdForm,
                            selected_TypeEd,
                            selected_Faculty,
                            courseField,
                            Semester);
                    String receivedInfo = educationInfo.ConnectAndGetInfo();
                    runOnUiThread(() -> result.setText(receivedInfo));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            thread.start();
        }
    }

    public void ClearAll(View view) {
        spEducationalFormat.setSelection(0);
        spTypeOfEducation.setSelection(0);
        spFaculties.setSelection(0);
        EditText courseField = findViewById(R.id.Course);
        result = findViewById(R.id.timetable);
        courseField.setText("");
        result.setText("Расписание будет здесь!");
    }
}