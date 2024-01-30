package com.example.vyatsuapp.utils;

import android.widget.EditText;

import java.util.Calendar;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class EducationInfo {
    private static final String VyatsuURL = "https://www.vyatsu.ru";
    private final String FullTimeTimetable = "/studentu-1/spravochnaya-informatsiya/raspisanie-zanyatiy-dlya-studentov.html";
    // расписание для очного обучения
    private final String DistanceCertification = "/internet-gazeta/raspisanie-promezhutochnoy-attestatsii-obuchayusch-1.html";
    // расписание промежуточной аттестации и занятий для заочников
    private final String FullTimeAndDistance = "/studentu-1/spravochnaya-informatsiya/raspisanie-zanyatiy-studentov-ochno-zaochnoy-formy.html";
    // расписание промежуточной аттестации и занятий для очно-заочного обучения
    private final String FullTimeCertification = "/internet-gazeta/raspisanie-sessiy-obuchayuschihsya-na-2016-2017-uc.html";
    // расписание промежуточной аттестации для очников
    private final String PracticeCertification = "/internet-gazeta/raspisanie-promezhutochnoy-attestatsii-obuchayusch-1.html";
    // расписание промежуточной аттестации для обучающихся по практике
    private String Faculty;
    private String EducFormat;
    private String Course;
    private String TypeOfEduc;
    private int Semester;


    public EducationInfo(String EducFormat, String TypeOfEduc, String Faculties, EditText Course, int Semester) {
        this.EducFormat = EducFormat;
        this.TypeOfEduc = TypeOfEduc;
        this.Faculty = Faculties;
        this.Course = Course.getText().toString();
        this.Semester = Semester;
    }

    public String ConnectAndGetInfo() {
        StringBuilder groups = new StringBuilder();
        String secondPartURL, reveivedInfo;
        switch (EducFormat) {
            case "Очно" -> secondPartURL = FullTimeTimetable;
            case "Очно-заочно" -> secondPartURL = FullTimeAndDistance;
            default -> secondPartURL = DistanceCertification;
        }

        String URL = VyatsuURL + secondPartURL;
        try {
            var document = Jsoup.connect(URL).maxBodySize(0).get();
            Elements programElements = document.select(".headerEduPrograms"); //Выбираем все заголовки типов обучения

            for (Element programElement : programElements) {
                String programType = programElement.text(); //Тип обучения

                if (programType.equals(TypeOfEduc)) { //Если тип обучения совпадает с выбранным
                    Elements facultyElements = programElement.nextElementSibling().select("div.fak_name"); //Выбираем все факультеты

                    for (Element facultyElement : facultyElements) {
                        String facultyName = facultyElement.text(); //Название факультета

                        if (facultyName.contains(Faculty)) { //Если факультет совпадает с выбранным
                            Elements groupElements = facultyElement.nextElementSibling().select(".grpPeriod"); //Выбираем все группы

                            for (Element groupElement : groupElements) {
                                String groupName = groupElement.text(); //Название группы

                                if (groupName.contains("-" + Course)) { //Если курс группы совпадает с выбранным
                                    Elements getPDFs = groupElement.nextElementSiblings().select("a"); //Выбираем все ссылки

                                    for (Element getPDF : getPDFs) {
                                        String PDFurl = getPDF.attr("href"); //Ссылка на таблицу

                                        if (PDFurl.contains("_" + Semester + "_")) {
                                            groups.append(groupName).
                                                    append(": ").
                                                    append(VyatsuURL).
                                                    append(PDFurl).
                                                    append("\n");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            reveivedInfo = String.valueOf(groups);
            return reveivedInfo;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public String getFaculty() { return Faculty; }

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
