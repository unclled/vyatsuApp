package com.example.vyatsuapp.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EducationInfo {
    private static final String VyatsuURL = "https://www.vyatsu.ru/studentu-1/spravochnaya-informatsiya/raspisanie-zanyatiy-dlya-studentov.html";
    private String Faculty;
    private int Course;
    private String TypeOfEduc;
    private List<String> groups = new ArrayList<>();
    private int Semester;
    private String Group;

    public EducationInfo(String TypeOfEduc, String Faculties, int Course, int Semester) {
        this.TypeOfEduc = TypeOfEduc;
        this.Faculty = Faculties;
        this.Course = Course;
        this.Semester = Semester;
    }

    public List<String> ConnectAndGetInfo() {
        StringBuilder PDFurlForGroup = new StringBuilder();
        groups.clear();
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
                                            System.out.println("Зашли");
                                            groups.add(groupName);
                                        }
                                    }
                                }
                            }

                        }

                    }

                }

            }
            return groups;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public List<String> getGroups() { return groups; }

    public String getFaculty() { return Faculty; }

    public int getCourse() { return Course; }

    public String getTypeOfEduc() { return TypeOfEduc; }
}
