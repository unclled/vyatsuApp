package com.example.vyatsuapp.utils;

import android.content.Context;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

public class EducationInfo {
    private static final String VyatsuURL = "https://www.vyatsu.ru/studentu-1/spravochnaya-informatsiya/raspisanie-zanyatiy-dlya-studentov.html";
    private String Faculty;
    private String Course;
    private String TypeOfEduc;
    private List<String> groups = new ArrayList<>();
    private int Semester;

    public EducationInfo(String TypeOfEduc, String Faculties, EditText Course, int Semester) {
        this.TypeOfEduc = TypeOfEduc;
        this.Faculty = Faculties;
        this.Course = Course.getText().toString();
        this.Semester = Semester;
    }

    public String ConnectAndGetInfo() {
        StringBuilder PDFurlForGroup = new StringBuilder();
        String receivedInfo;

        try {
            var document = Jsoup.connect(VyatsuURL).maxBodySize(0).get();
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
                                            groups.add(groupName);
                                            PDFurlForGroup.append(groupName).
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
            receivedInfo = String.valueOf(PDFurlForGroup);
            return receivedInfo;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public List<String> getGroups() { return groups; }

    public String getFaculty() { return Faculty; }

    public String getCourse() { return Course; }

    public String getTypeOfEduc() { return TypeOfEduc; }
}
