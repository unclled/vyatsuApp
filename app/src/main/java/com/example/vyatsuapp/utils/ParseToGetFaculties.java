package com.example.vyatsuapp.utils;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.URL;

public class ParseToGetFaculties {

    public static String getFaculties(URL url) {
        String faculties = "";
        try {
            var document = Jsoup.connect("url").get();
            var faculty = document.select("table.fak_name");
            faculties += faculty;
            return faculties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
