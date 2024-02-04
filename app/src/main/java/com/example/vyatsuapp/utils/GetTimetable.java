package com.example.vyatsuapp.utils;

import android.content.Context;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;


public class GetTimetable {
    private static final String VyatsuURL = "https://www.vyatsu.ru/studentu-1/spravochnaya-informatsiya/raspisanie-zanyatiy-dlya-studentov.html";
    private String Faculty;
    private String TypeOfEduc;
    private int Semester;
    private String Group;
    private String[] classTime;

    public GetTimetable(String TypeOfEduc, String Faculties, String Group, int Semester) {
        this.TypeOfEduc = TypeOfEduc;
        this.Faculty = Faculties;
        this.Group = Group;
        this.Semester = Semester;
    }

    public String GetActualTimetable() {
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

                                if (groupName.contains(Group)) { //Если курс группы совпадает с выбранным
                                    Elements getPDFs = groupElement.nextElementSiblings().select("a"); //Выбираем все ссылки
                                    for (Element getPDF : getPDFs) {
                                        String PDFurl = getPDF.attr("href"); //Ссылка на таблицу

                                        if (PDFurl.contains("_" + Semester + "_")) {
                                            return PDFurl;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            System.out.println("Не нашли");
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String ReadPDFTable(Context context) throws IOException {
        StringBuilder finalText = new StringBuilder();
        File file = new File(context.getExternalFilesDir(null), "timetable.pdf"); // Путь к сохраненному PDF файлу
        PDDocument document = PDDocument.load(file);
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        String[] linex = text.split("\n");
        for (String line : linex) {
            for (String time : classTime) {
                if (line.contains(time)) {
                    finalText.append(line).append("\n");
                }
            }
        }
        System.out.print(text);
        return finalText.toString();
    }

    public void setClassTime(String[] classTime) {
        this.classTime = classTime;
    }
}
