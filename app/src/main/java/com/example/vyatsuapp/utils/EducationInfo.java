package com.example.vyatsuapp.utils;

import android.widget.EditText;

import org.jsoup.Jsoup;

import java.io.IOException;

public class EducationInfo {
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
    private String Faculty;
    private String EducFormat;
    private String Course;
    private String TypeOfEduc;

    public EducationInfo(String EducFormat, String TypeOfEduc, String Faculties, EditText Course) {
        this.EducFormat = EducFormat;
        this.TypeOfEduc = TypeOfEduc;
        this.Faculty = Faculties;
        this.Course = Course.getText().toString();
    }

    public String ConnectAndGetInfo() {
        String secondPartURL;
        switch (EducFormat) {
            case "Очно" -> secondPartURL = FullTimeTimetable;
            case "Очно-заочно" -> secondPartURL = FullTimeAndDistance;
            default -> secondPartURL = DistanceCertification;
        }

        String URL = VyatsuURL + secondPartURL;
        try {
            var document = Jsoup.connect(URL).maxBodySize(0).get();
            return document.select("div.grpPeriod").text();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getFaculty(){ return Faculty; }

    public String getCourse(){
        return Course;
    }

    public String getEducFormat(){
        return EducFormat;
    }

    public String getTypeOfEduc(){
        return TypeOfEduc;
    }
}
