package com.example.vyatsuapp.Pages.Timetable;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import com.example.vyatsuapp.Pages.Authorization.AuthorizationAPI;
import com.example.vyatsuapp.Pages.PresenterBase;
import com.example.vyatsuapp.utils.AuthRequestBody;
import com.example.vyatsuapp.utils.BasicAuthInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TimetablePresenter extends PresenterBase<Timetable.View> implements Timetable.Presenter {
    StringBuilder allTimetable;
    @Override
    public void viewIsReady() {
        String timetable = getHTMLTimetable();
        Thread thread = new Thread(() -> {
            String allTimetable = parseTimetable(timetable).toString();
            getView().setText(allTimetable);
        }); thread.start();
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public StringBuilder parseTimetable(String timetable) {
        String day = getCurrentDay();
        Date currentDate, actualDate;
        try {
            currentDate = new SimpleDateFormat("dd.MM.yyyy").parse(day);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        allTimetable = new StringBuilder();
        Document document = Jsoup.parse(timetable);
        Elements programElements = document.select(".day-container");

        for (Element programElement : programElements) {
            Elements classesDesc = programElement.select(".font-normal");
            String receivedDate = classesDesc.select("b").text();
            int length = receivedDate.length();
            String date = receivedDate.substring(length - 11, length);
            try {
                actualDate = new SimpleDateFormat("dd.MM.yyyy").parse(date);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            // Убедимся, что мы обрабатываем данные только будущих дней
            assert currentDate != null;
            if (currentDate.before(actualDate)) {
                StringBuilder dailyTimetable = new StringBuilder();
                dailyTimetable.append(receivedDate).append("\n\n");

                Elements dayClasses = programElement.select(".day-pair");

                for (Element currentClass : dayClasses) {
                    String classData = currentClass.select(".font-semibold").text();
                    String classDesc = currentClass.select(".pair_desc").text();

                    dailyTimetable.append(classData).append("\n").append(classDesc).append("\n\n");
                }

                // Добавляем каждый день как отдельную запись
                allTimetable.append(dailyTimetable).append("\n\n\n");
            }
        }
        return allTimetable;
    }


    @Override
    public void getAuthorization(String login, String password) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // Время на подключение
                .readTimeout(30, TimeUnit.SECONDS) // Время на чтение
                .writeTimeout(30, TimeUnit.SECONDS) // Время на запись
                .addInterceptor(new BasicAuthInterceptor(login, password))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://new.vyatsu.ru/account/obr/rasp/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        AuthRequestBody body = new AuthRequestBody(login, password);
        AuthorizationAPI api = retrofit.create(AuthorizationAPI.class);

        Call<ResponseBody> call = api.authUser(body);
        call.enqueue(new Callback<>() {
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        try {
                            BufferedSource source = responseBody.source();
                            String htmlContent = source.readUtf8();
                            applyHTMLResponse(htmlContent);
                            getView().setText(parseTimetable(htmlContent).toString());
                            getView().updateLastAuthorization();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {}
        });
    }

    @Override
    public void getLoginAndPassword() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getView().getContext());
        String login = sharedPreferences.getString("UserLogin", null);
        String password = sharedPreferences.getString("UserPassword", null);
        getAuthorization(login, password);
    }

    @Override
    public String getCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE) - 1;
        int month = calendar.get(Calendar.MONTH) + 1;
        return day + "."
                + month + "."
                + calendar.get(Calendar.YEAR);
    }

    @Override
    public String getHTMLTimetable() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getView().getContext());
        return sharedPreferences.getString("HTMLResponse", null);
    }

    public void applyHTMLResponse(String htmlContent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getView().getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("HTMLResponse", htmlContent);
        editor.apply();
    }

    public StringBuilder getAllTimetable() {
        return allTimetable;
    }
}
