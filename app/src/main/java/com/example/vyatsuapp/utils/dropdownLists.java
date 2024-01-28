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
            return switch (selectedEducationType) {
                case "Бакалавриат" -> FullTimeBachelorFacs;
                case "Специалитет" -> FullTimeSpecFacs;
                case "Магистратура" -> FullTimeMasterFacs;
                default -> FullTimeGraduateFacs;
            };
        } else if (selectedEducationFormat.equals("Очно-заочно")) {
            return switch (selectedEducationType) {
                case "Бакалавриат" -> FTDistBachelorFacs;
                case "Магистратура" -> new String[] {"ФПИП"};
                default -> new String[] {};
            };
        } else {
            return switch (selectedEducationType) {
                case "Бакалавриат" -> DistanceBachelorFacs;
                case "Специалитет" -> DistanceSpecFacs;
                case "Магистратура" -> DistanceMasterFacs;
                default -> DistanceGraduateFacs;

            };
        }
    }
}
