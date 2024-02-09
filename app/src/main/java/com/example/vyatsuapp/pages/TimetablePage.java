package com.example.vyatsuapp.pages;

/*public class TimetablePage {
    private StringBuilder timetableInfo;
    private SharedPreferences sharedPreferences;

    public TimetablePage() {}

    public void getInfoAboutClasses() {
        String accessToken = sharedPreferences.getString("accessToken", "");
        String url = "https://new.vyatsu.ru/account/obr/rasp/";
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // Время на подключение
                .readTimeout(30, TimeUnit.SECONDS) // Время на чтение
                .writeTimeout(30, TimeUnit.SECONDS) // Время на запись
                .addInterceptor(chain -> {
                    Request originalRequest = chain.request();
                    Request newRequest = originalRequest.newBuilder()
                            .header("Authorization", "Bearer " + accessToken)
                            .build();
                    return chain.proceed(newRequest);
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        Timetable api = retrofit.create(Timetable.class);
        Call<ResponseBody> call = api.getTimetableHTML(url);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String htmlContent = response.body().string();
                        System.out.println(htmlContent);
                        // Здесь вы можете использовать Jsoup для парсинга и работы с HTML контентом
                        Document document = Jsoup.parse(htmlContent);
                        //Document document = Jsoup.parse(responseBody);
                        //Elements programElements = document.select("body > main > div.container > div:nth-child(6) > div.px-5.md\\:px-16.py-7.space-y-6 > div.flex.flex-col.day-pair");

                    *//*for (Element programElement : programElements) {
                        String pairData = programElement.select(".font-semibold").first().text(); // Получить данные о паре

                        Elements pairDesc = programElement.select(".pair_desc");
                        String subject = pairDesc.select("b").text(); // Получить предмет
                        String teacher = pairDesc.select(".prepod").text(); // Получить имя преподавателя
                        String room = pairDesc.text().replace(subject, "").replace(teacher, "").replace(",", "").trim(); // Получить номер кабинета

                        System.out.println("Дата/Время: " + pairData);
                        System.out.println("Предмет: " + subject);
                        System.out.println("Преподаватель: " + teacher);
                        System.out.println("Кабинет: " + room);
                    }*//*
                        // Дальнейшая обработка HTML контента
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Обработка неуспешного ответа (HTTP код не 2xx)
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println("Ошибка");
            }

            });

    }

    public StringBuilder getTimetableInfo() { return timetableInfo; }
}*/
