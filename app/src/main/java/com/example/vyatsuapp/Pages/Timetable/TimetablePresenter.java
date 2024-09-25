package com.example.vyatsuapp.Pages.Timetable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;

import com.example.vyatsuapp.BuildConfig;
import com.example.vyatsuapp.R;
import com.example.vyatsuapp.Utils.MethodsForMVP.PresenterBase;
import com.example.vyatsuapp.Utils.Resoponses.AuthorizationAPI;
import com.example.vyatsuapp.Utils.Resoponses.DownloadService;
import com.example.vyatsuapp.Utils.ServerRequests.AuthRequestBody;
import com.example.vyatsuapp.Utils.ServerRequests.BasicAuthInterceptor;
import com.example.vyatsuapp.Utils.UtilsClass;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
    private StringBuilder allTimetable;
    private static final String CURRENT_VERSION = BuildConfig.VERSION_NAME;
    private static final String GITHUB_API_URL = "https://api.github.com/repos/unclled/vyatsuApp/releases/latest";
    private static final String VyatsuURL = "https://www.vyatsu.ru/studentu-1/spravochnaya-informatsiya/raspisanie-zanyatiy-dlya-studentov.html";
    private Context context;
    private final UtilsClass utils = new UtilsClass();

    @Override
    public void viewIsReady() {
        String timetable = getHTMLTimetable();
        Thread thread = new Thread(() -> {
            String allTimetable = parseTimetable(timetable).toString();
            getView().setText(allTimetable);
        });
        thread.start();
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

        AuthRequestBody body = new AuthRequestBody();
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
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            }
        });
    }

    @Override
    public void getLoginAndPassword() {
        List<String> keys = new ArrayList<>();
        keys.add("USER_LOGIN");
        keys.add("USER_PASSWORD");
        List<String> values = utils.loadFromPreferences(keys, context);
        getAuthorization(values.get(0), values.get(1));
    }

    @Override
    public String getActualTimetable(String studyGroup) {
        try {
            Document document = Jsoup.connect(VyatsuURL)
                    .maxBodySize(0)
                    .get();

            Elements groupElements = document.select("div.grpPeriod");

            for (Element groupElement : groupElements) {
                String groupName = groupElement.text().trim();

                if (groupName.contains(studyGroup)) {
                    Element listPeriodElement = groupElement.nextElementSibling();

                    if (listPeriodElement != null) {
                        Elements pdfLinks = listPeriodElement.select("a[href]");
                        if (!pdfLinks.isEmpty()) {
                            String pdfUrl = pdfLinks.first().attr("href");

                            return "https://www.vyatsu.ru" + pdfUrl;
                        }
                    }
                    break;
                }
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void downloadPDF(String studyGroup) {
        if (studyGroup == null || studyGroup.isEmpty()) {
            getView().showEditGroupWindow();
            return;
        }

        String fileName = studyGroup + "_расписание.pdf";
        File pdfFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);

        if (getView().isNetworkAvailable()) {
            if (pdfFile.exists()) {
                getView().openPDF(fileName);
            } else {
                utils.showToastLong("Нет подключения к интернету и отсутствует локальная копия PDF!", context);
            }
            return;
        }

        utils.showToastShort("Скачивание началось...", context);
        new Thread(() -> {
            String pdfUrl = getActualTimetable(studyGroup);
            if (pdfUrl != null) {
                try {
                    Response<ResponseBody> response = downloadFile(pdfUrl);
                    if (response.isSuccessful()) {
                        boolean writtenToDisk = writeResponseBodyToDisk(response.body(), fileName);
                        if (writtenToDisk) {
                            getView().openPDF(fileName);
                        } else {
                            utils.showToastShort("Ошибка при сохранении PDF", context);
                        }
                    } else {
                        utils.showToastShort("Не удалось загрузить PDF", context);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    utils.showToastShort("Ошибка при загрузке PDF", context);
                }
            } else {
                utils.showToastShort("Не удалось найти расписание для группы", context);
            }
        }).start();
    }

    private boolean writeResponseBodyToDisk(ResponseBody body, String fileName) {
        try {
            File downloadDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

            if (downloadDir != null) {
                File[] files = downloadDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (!file.delete()) {
                            Log.e("File Deletion", "Failed to delete file: " + file.getName());
                        }
                    }
                }
            }

            File pdfFile = new File(downloadDir, fileName);

            InputStream inputStream = body.byteStream();
            try (FileOutputStream outputStream = new FileOutputStream(pdfFile)) {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;

                    // Обновление прогресса загрузки
                    int progress = (int) (100 * fileSizeDownloaded / fileSize);
                    getView().animateDownload(progress);
                }

                outputStream.flush();
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Response<ResponseBody> downloadFile(String pdfUrl) throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(pdfUrl + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DownloadService downloadService = retrofit.create(DownloadService.class);
        return downloadService.downloadFile(pdfUrl).execute();
    }

    @Override
    public void logout() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("USER_LOGIN");
        editor.remove("USER_PASSWORD");
        editor.remove("HAS_STUDENT_INFO");
        editor.remove("LAST_UPDATE");
        editor.apply();
    }

    @Override
    public void saveGroupToPreferences(String group) {
        utils.toMapAndSaveSP("STUDY_GROUP", group, context);
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
        List<String> key = new ArrayList<>();
        key.add("HTML_RESPONSE");
        List<String> value = utils.loadFromPreferences(key, context);
        return value.isEmpty() ? null : value.get(0);
    }

    public void applyHTMLResponse(String htmlContent) {
        utils.toMapAndSaveSP("HTML_RESPONSE", htmlContent, context);
    }

    public StringBuilder getAllTimetable() {
        return allTimetable;
    }

    public void checkForUpdates() {
        new CheckUpdateTask().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class CheckUpdateTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(GITHUB_API_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Accept", "application/vnd.github.v3+json");

                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return response.toString();
            } catch (Exception e) {
                Log.e("UpdateCheck", "Ошибка при проверке обновления", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String latestVersion = jsonObject.getString("tag_name"); // получение версии из GitHub

                    if (isNewerVersion(CURRENT_VERSION, latestVersion)) {
                        String downloadUrl = jsonObject.getJSONArray("assets").getJSONObject(0).getString("browser_download_url");

                        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context, R.style.CustomAlertDialogTheme);
                        builder.setTitle(HtmlCompat.fromHtml("<font color='#000000'>Доступно обновление</font>", HtmlCompat.FROM_HTML_MODE_LEGACY))
                                .setMessage(HtmlCompat.fromHtml("<font color='#000000'>Доступна новая версия приложения. Хотите обновить?</font>", HtmlCompat.FROM_HTML_MODE_LEGACY))
                                .setPositiveButton("Обновить", (dialog, which) -> {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl));
                                    context.startActivity(intent);
                                })
                                .setNegativeButton("Позже", null)
                                .show();
                    }
                } catch (Exception e) {
                    Log.e("UpdateCheck", "Ошибка при разборе данных обновления", e);
                }
            }
        }

        /**
         * Compares two version strings.
         *
         * @param currentVersion The current version of the app.
         * @param latestVersion  The latest version from GitHub.
         * @return true if the latest version is newer than the current version, false otherwise.
         */
        private boolean isNewerVersion(String currentVersion, String latestVersion) {
            // Remove the leading 'v' if present
            currentVersion = currentVersion.startsWith("v") ? currentVersion.substring(1) : currentVersion;
            latestVersion = latestVersion.startsWith("v") ? latestVersion.substring(1) : latestVersion;

            // Split versions by dot (.)
            String[] currentParts = currentVersion.split("\\.");
            String[] latestParts = latestVersion.split("\\.");

            int length = Math.max(currentParts.length, latestParts.length);

            for (int i = 0; i < length; i++) {
                int currentPart = i < currentParts.length ? Integer.parseInt(currentParts[i]) : 0;
                int latestPart = i < latestParts.length ? Integer.parseInt(latestParts[i]) : 0;

                if (currentPart < latestPart) {
                    return true; // A newer version is available
                } else if (currentPart > latestPart) {
                    return false; // Current version is newer or the same
                }
            }

            return false; // Versions are identical
        }
    }
}