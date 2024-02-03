package com.example.vyatsuapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dx.dxloadingbutton.lib.LoadingButton;
import com.example.vyatsuapp.utils.EducationInfo;
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton;
import com.shuhart.stepview.StepView;
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

    public CircularProgressButton confirmCourseFacultyButton;
    public CircularProgressButton SaveButton;

    public EditText courseField;

    private String selected_TypeEd = null;
    private String selected_Faculty = null;
    private String selected_Group = null;
    private int Course = 0;
    private int Semester = 1;

    private SharedPreferences sharedPreferences;

    Animation showSpinner;
    Animation translateButtons;

    Button ClearButton;

    private StepView stepView;

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
        ClearButton = findViewById(R.id.clear_button);
        SaveButton = findViewById(R.id.SaveButton);
        stepView = findViewById(R.id.stepView);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        selected_TypeEd = sharedPreferences.getString("EducationType", null);
        selected_Faculty = sharedPreferences.getString("Faculty", null);
        Course = sharedPreferences.getInt("Course", 0);
        selected_Group = sharedPreferences.getString("Group", null);

        courseField = findViewById(R.id.Course);
        confirmCourseFacultyButton = findViewById(R.id.confirm_faculty_course);
        stepView.setStepsNumber(4);

        showSpinner = AnimationUtils.loadAnimation(this, R.anim.alpha);
        translateButtons = AnimationUtils.loadAnimation(this, R.anim.translate);

        isFirstStart();
    }

    private void isFirstStart() {
        SharedPreferences sp = getSharedPreferences("hasStudentInfo", Context.MODE_PRIVATE);
        boolean hasStudentInfo = sp.getBoolean("hasStudentInfo", false);
        System.out.println(hasStudentInfo);
        if (!hasStudentInfo || selected_TypeEd == null || selected_Faculty == null
                || Course == 0 || selected_Group == null) { //Если нет какой-либо информации о студенте
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("hasStudentInfo", true);
            editor.apply();
            getStudentInfo();
        } else { //запуск активности с расписанием
            //Intent intent = new Intent(this, BasicMainActivity.class);
            //startActivity(intent);
        }
    }

    private void getStudentInfo() {
        ///////////////////////////Обработка выбора в выпадающих списках///////////////////////////
        spTypeOfEducation.setOnSpinnerItemSelectedListener(
                (OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
                    selected_TypeEd = newItem;
                    stepView.done(true);
                    stepView.go(0, true);
                    LinearLayout border = findViewById(R.id.Spinners);
                    Animation scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.border_increase);
                    border.startAnimation(scaleAnimation);
                    confirmCourseFacultyButton.startAnimation(translateButtons);
                    ClearButton.startAnimation(translateButtons);

                    String[] selected_Faculties = switch (newItem) {
                        case "Бакалавр" -> getResources().getStringArray(R.array.FullTimeBachelorFacs);
                        case "Специалист" -> getResources().getStringArray(R.array.FullTimeSpecFacs);
                        case "Магистр" -> getResources().getStringArray(R.array.FullTimeMasterFacs);
                        default -> getResources().getStringArray(R.array.FullTimeGraduateFacs);
                    };
                    spFaculties.setVisibility(View.VISIBLE);
                    spFaculties.startAnimation(showSpinner);
                    List<String> Faculties = new ArrayList<>(Arrays.asList(selected_Faculties));
                    spFaculties.setItems(Faculties);

                    spFaculties.setOnSpinnerItemSelectedListener(
                            (OnSpinnerItemSelectedListener<String>) (oldIndex1, oldItem1, newIndex1, newItem1) -> {
                        selected_Faculty = newItem1;
                        stepView.done(true);
                        stepView.go(1, true);
                        courseField.setVisibility(View.VISIBLE);
                        Animation scaleAnimation2 = AnimationUtils.loadAnimation(this, R.anim.border_increase2);
                        border.startAnimation(scaleAnimation2);
                        courseField.startAnimation(showSpinner);
                        ClearButton.startAnimation(translateButtons);
                        confirmCourseFacultyButton.startAnimation(translateButtons);
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
            if (Course < 1 || Course > 5) {
                Toast toast = Toast.makeText(
                        this,
                        "Укажите реальный курс",
                        Toast.LENGTH_LONG);
                toast.show();
            }
            else {
                confirmCourseFacultyButton.startAnimation();
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
                            if (groups.size() != 0) {
                                stepView.done(true);
                                stepView.go(2, true);
                                spGroups.setItems(groups);
                                /*Bitmap bitmap = BitmapFactory.decodeResource(view.getContext().getResources(),
                                      R.drawable.done_background);
                                confirmCourseFacultyButton.doneLoadingAnimation(Color.parseColor("#076dab"), bitmap);*/
                                confirmCourseFacultyButton.revertAnimation();

                                spGroups.setVisibility(View.VISIBLE);
                                confirmCourseFacultyButton.setVisibility(View.GONE);
                                SaveButton.setVisibility(View.VISIBLE);

                                spGroups.startAnimation(showSpinner);
                                SaveButton.startAnimation(translateButtons);
                                ClearButton.startAnimation(translateButtons);

                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                spGroups.setOnSpinnerItemSelectedListener(
                                        (OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) -> {
                                            selected_Group = newItem;
                                            stepView.done(true);
                                            stepView.go(3, true);
                                        });
                            } else {
                                confirmCourseFacultyButton.revertAnimation();
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Toast toast = Toast.makeText(
                                        this,
                                        "Не существует групп для данного курса",
                                        Toast.LENGTH_LONG);
                                toast.show();
                            }
                        });
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }); thread.start();
            }
        }
    }

    public void SaveButtonPressed(View view) throws InterruptedException {
        if (selected_Group != null) {
            SaveButton.startAnimation();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("EducationType", selected_TypeEd);
            editor.putString("Faculty", selected_Faculty);
            editor.putInt("Course", Course);
            editor.putString("Group", selected_Group);
            editor.apply();
            SaveButton.revertAnimation();
            Intent intent = new Intent(this, BasicMainActivity.class);
            startActivity(intent);
        }
    }

    public void ClearAll(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        spTypeOfEducation.clearSelectedItem();
        spFaculties.clearSelectedItem();
        spGroups.clearSelectedItem();
        courseField.setText("");

        getStudentInfo();

        spGroups.setVisibility(View.GONE);
        courseField.setVisibility(View.GONE);
        spFaculties.setVisibility(View.GONE);
        spTypeOfEducation.setVisibility(View.VISIBLE);
        confirmCourseFacultyButton.setVisibility(View.VISIBLE);
        SaveButton.setVisibility(View.GONE);
        for (int i = 4; i >= 0; i--) {
            stepView.go(i, true);
            stepView.done(false);
        }
    }
}