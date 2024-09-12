package com.example.vyatsuapp.Pages.Timetable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vyatsuapp.Pages.Authorization.AuthorizationActivity;
import com.example.vyatsuapp.R;
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton;
import com.google.android.material.textfield.TextInputLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TimetableActivity extends AppCompatActivity implements Timetable.View {
    private static final String VyatsuURL = "https://www.vyatsu.ru/studentu-1/spravochnaya-informatsiya/raspisanie-zanyatiy-dlya-studentov.html";
    private TextView lastUpdate;
    private RecyclerView recyclerView;
    private TimetableAdapter adapter;
    private Timetable.Presenter presenter;
    private String studyGroup;
    private CircularProgressButton updateButton;
    private TextInputLayout studyGroupLayout;
    private AlertDialog ad;
    private ProgressBar progressBar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable_activity);

        presenter = new TimetablePresenter();
        presenter.attachView(this);
        presenter.viewIsReady();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        lastUpdate = findViewById(R.id.lastUpdate);
        updateButton = findViewById(R.id.updateButton);
        studyGroupLayout = findViewById(R.id.studyGroup);
        progressBar = findViewById(R.id.progressBar);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        runOnUiThread(() -> lastUpdate.setText(sharedPreferences.getString("LASTUPDATE", null)));
        recyclerView.setAdapter(adapter);
        studyGroup = sharedPreferences.getString("STUDY_GROUP", null);
        updateLastAuthorization();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        presenter.checkForUpdates();
    }

    @Override
    public void setText(String timetableText) {
        runOnUiThread(() -> {
            String[] days = timetableText.split("\n\n\n");
            List<String> timetableDataList = new ArrayList<>();
            for (String day : days) {
                if (!day.trim().isEmpty()) {
                    timetableDataList.add(day.trim());
                }
            }
            adapter = new TimetableAdapter(timetableDataList);
            recyclerView.setAdapter(adapter);
        });
    }

    @Override
    public void updatePressed(View view) {
        Animation rotate = AnimationUtils.loadAnimation(this, R.anim.update_rotate);
        updateButton.startAnimation(rotate);
        presenter.getLoginAndPassword();
    }

    @Override
    public void updateLastAuthorization() {
        updateButton.clearAnimation();
        Calendar calendar = Calendar.getInstance();
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd", Locale.getDefault());
        String dateText = dateFormat.format(currentDate);
        DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String timeText = timeFormat.format(currentDate);
        String month = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG_FORMAT, new Locale("ru"));
        }
        String updated = "Обновлено: " + dateText + " " + month + " " + timeText;
        runOnUiThread(() -> lastUpdate.setText(updated));
        setText(presenter.getAllTimetable().toString());
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("LASTUPDATE", updated);
        editor.apply();
    }

    public void logoutPressed() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("UserLogin");
        editor.remove("UserPassword");
        editor.remove("hasStudentInfo");
        editor.apply();
        Intent intent = new Intent(TimetableActivity.this, AuthorizationActivity.class);
        startActivity(intent);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        //запрет возвращения назад
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.editGroup) {
            showEditGroupWindow();
            return true;
        } else if (item.getItemId() == R.id.logout) {
            logoutPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void showEditGroupWindow() {
        View dialogView = getLayoutInflater().inflate(R.layout.alert, null);

        studyGroupLayout = dialogView.findViewById(R.id.studyGroup);

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setView(dialogView);
        ad = adb.create();
        ad.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ad.show();
    }


    public void saveGroup(View view) {
        if (studyGroupLayout == null || studyGroupLayout.getEditText() == null) {
            Toast.makeText(this, "Ошибка инициализации", Toast.LENGTH_SHORT).show();
        } else {

            String inputText = studyGroupLayout.getEditText().getText().toString();

            if (!inputText.isEmpty()) {
                studyGroup = inputText;
                saveGroupToPreferences(studyGroup);
                Toast.makeText(this, "Группа сохранена: " + studyGroup, Toast.LENGTH_SHORT).show();
                hideEditGroupWindow();
            } else {
                Toast.makeText(this, "Группа не может быть пустой", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void closeEditGroup(View view) {
        hideEditGroupWindow();
    }

    public void hideEditGroupWindow() {
        ad.dismiss();
    }

    private void saveGroupToPreferences(String group) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("STUDY_GROUP", group);
        editor.apply();
    }

    public void downloadPDF(View view) {
        if (studyGroup == null || studyGroup.isEmpty()) {
            showEditGroupWindow();
        } else {
            String fileName = studyGroup + "_timetable.pdf";
            File pdfFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);

            if (!isNetworkAvailable()) {
                if (pdfFile.exists()) {
                    openPDF(fileName);
                } else {
                    Toast.makeText(this, "Нет подключения к интернету и отсутствует локальная копия PDF.", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            runOnUiThread(() -> {
                progressBar.setProgress(80);
                Toast.makeText(this, "Скачивание началось...", Toast.LENGTH_SHORT).show();
            });

            new Thread(() -> {
                String pdfUrl = GetActualTimetable();
                if (pdfUrl != null) {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(pdfUrl + "/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    DownloadService downloadService = retrofit.create(DownloadService.class);

                    try {
                        Response<ResponseBody> response = downloadService.downloadFile(pdfUrl).execute();

                        if (response.isSuccessful()) {
                            boolean writtenToDisk = writeResponseBodyToDisk(response.body(), fileName);

                            runOnUiThread(() -> {
                                if (writtenToDisk) {
                                    openPDF(fileName);
                                } else {
                                    Toast.makeText(TimetableActivity.this, "Ошибка при сохранении PDF.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(TimetableActivity.this, "Не удалось загрузить PDF.", Toast.LENGTH_SHORT).show();
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> {
                            Toast.makeText(TimetableActivity.this, "Ошибка при загрузке PDF.", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(TimetableActivity.this, "Не удалось найти расписание для группы.", Toast.LENGTH_SHORT).show();
                    });
                }
            }).start();
        }
    }


    public String GetActualTimetable() {
        try {
            Document document = Jsoup.connect(VyatsuURL)
                    .maxBodySize(0)
                    .get();

            Elements groupElements = document.select("div.grpPeriod");

            for (Element groupElement : groupElements) {
                String groupName = groupElement.text().trim();

                if (groupName.contains(this.studyGroup)) {
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

    private boolean writeResponseBodyToDisk(ResponseBody body, String fileName) {
        try {
            File pdfFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
            if (pdfFile.exists()) {
                pdfFile.delete();
            }

            InputStream inputStream = body.byteStream();
            FileOutputStream outputStream = new FileOutputStream(pdfFile);
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

                // Обновление прогресса
                final int progress = (int) (500 * fileSizeDownloaded / fileSize);
                runOnUiThread(() -> progressBar.setProgress(progress));
            }
            outputStream.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    private void openPDF(String fileName) {
        File pdfFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
        Uri pdfUri = FileProvider.getUriForFile(this, "com.example.vyatsuapp.fileprovider", pdfFile);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent chooser = Intent.createChooser(intent, "Открыть PDF");
        startActivity(chooser);
        progressBar.setProgress(0);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
