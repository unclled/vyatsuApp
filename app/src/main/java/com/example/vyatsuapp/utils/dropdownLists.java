package com.example.vyatsuapp.utils;

public class dropdownLists {
    private static final String[] FullTimeBachelorFacs = {"Пед", "ИБиБ", "ИХиЭ", "ФАВТ", "ФИПНиК",
            "ФКиФМН", "ФЛ", "ФМО", "ФМиС", "ФПиП", "ФСиА", "ФТИД", "ФФКС", "ФФиМ", "ФЭФ", "ЭТФ", "ЮИ"};
    private static final String[] FullTimeSpecFacs = {"ФАВТ", "ФТИД", "ФЭФ", "ЮИ"};
    private static final String[] FullTimeMasterFacs = {"Пед", "ИБиБ", "ИХиЭ", "ФАВТ", "ФИПНиК",
            "ФКиФМН", "ФЛ", "ФМО", "ФМиС", "ФПиП", "ФСиА", "ФТИД", "ФФиМ", "ФЭФ", "ЭТФ", "ЮИ"};
    private static final String[] FullTimeGraduateFacs = {"ИБиБ", "ИХиЭ", "ФАВТ", "ФИПНиК",
            "ФКиФМН", "ФЛ", "ФМО", "ФМиС", "ФПиП", "ФСиА", "ФТИД", "ФФКС", "ФФиМ", "ФЭФ", "ЭТФ", "ЮИ"};
    private static final String[] FTDistBachelorFacs = {"ФМиС", "ФПиП", "ФСиА", "ФЭФ", "ЮИ"};
    private static final String[] DistanceBachelorFacs = {"Пед", "ФАВТ", "ФИПНиК", "ФМиС", "ФПиП",
            "ФСиА", "ФТИД", "ФФКС", "ФФиМ", "ФЭФ", "ЭТФ", "ЮИ"};
    private static final String[] DistanceSpecFacs = {"ФЭФ", "ЮИ"};
    private static final String[] DistanceMasterFacs = {"Пед", "ФМиС", "ФПиП", "ФСиА", "ФТИД", "ФФКС",
           "ФЭФ", "ЭТФ", "ЮИ"};
    private static final String[] DistanceGraduateFacs = {"ФИПНиК", "ФПиП", "ФТИД", "ФФиМ", "ФЭФ"};
    private String selectedEducationFormat; //Очно, Очно-заочно, Заочно
    private String selectedEducationType; //Бакалавриат, Специалитет, Магистратура, Аспирантура

    public dropdownLists(String selectedEducationFormat, String selectedEducationType) {
        this.selectedEducationFormat = selectedEducationFormat;
        this.selectedEducationType = selectedEducationType;
    }


    public String[] spFacultyItems() {
        if (selectedEducationFormat.equals("Очно")) {
            if (selectedEducationType.equals("Бакалавриат")) {
                return FullTimeBachelorFacs;
            } else if (selectedEducationType.equals("Специалитет")) {
                return FullTimeSpecFacs;
            } else if (selectedEducationType.equals("Магистратура")) {
                return FullTimeMasterFacs;
            } else {
                return FullTimeGraduateFacs;
            }
        } else if (selectedEducationFormat.equals("Очно-заочно")) {
            if (selectedEducationType.equals("Бакалавриат")) {
                return FTDistBachelorFacs;
            } else if (selectedEducationType.equals("Магистратура")){
                return new String[] {"ФПИП"};
            } else return new String[] {};
        } else {
            if (selectedEducationFormat.equals("Бакалавриат")) {
                return DistanceBachelorFacs;
            } else if (selectedEducationType.equals("Специалитет")) {
                return DistanceSpecFacs;
            } else if (selectedEducationType.equals("Магистратура")) {
                return DistanceMasterFacs;
            } else {
                return DistanceGraduateFacs;
            }
        }
    }
}
