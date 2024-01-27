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

import com.example.vyatsuapp.utils.NothingSelectedSpinnerAdapter;
import com.example.vyatsuapp.utils.dropdownLists;

import org.jsoup.Jsoup;

import java.io.IOException;

public class Faculties {
    private static final String VyatsuURL = "https://www.vyatsu.ru/";
    String Faculty;
    String educ_format;
    String Course;
    String educ_form;
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

    public Faculties(Spinner Format, Spinner Form, Spinner Fac, EditText Cour) throws IOException {
        this.educ_format = Format.getSelectedItem().toString();
        if (educ_format.equals("Очно")) {
            this.educ_form = FullTimeTimetable;
        } else if (educ_format.equals("Очно-заочно")) {
            this.educ_form = FullTimeAndDistance;
        } else {
            this.educ_form = DistanceCertification;
        }
        this.Course= Cour.getText().toString();
        try {
            String url = VyatsuURL + educ_form;
            var document = Jsoup.connect(url).maxBodySize(0).get();
            String fac = document.select("div.grpPeriod").text();
            this.Faculty= removeUnnecessary(fac);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private String removeUnnecessary(String faculty) {
        String clearedString = faculty;
        String trash = "\\(ОРУ\\)";
        clearedString = clearedString.replaceAll(trash, "");
        return clearedString;
    }

    public String getFaculty(){
        return Faculty;
    }

    public String getCourse(){
        return Course;
    }

    public String getEduc_format(){
        return educ_format;
    }

    public String getEduc_form(){
        return educ_form;
    }
}
