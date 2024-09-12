package com.example.vyatsuapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vyatsuapp.Pages.Authorization.AuthorizationActivity;
import com.example.vyatsuapp.Pages.Timetable.TimetableActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private String loginText = null;
    private String passwordText = null;
    private String htmlResponse = null;
    private static final String CURRENT_VERSION = "1.0.0"; // текущая версия приложения
    private static final String GITHUB_API_URL = "https://api.github.com/repos/unclled/vyatsuApp/releases/latest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        loginText = sharedPreferences.getString("UserLogin", null);
        passwordText = sharedPreferences.getString("UserPassword", null);
        htmlResponse = sharedPreferences.getString("HTMLResponse", null);

        checkForUpdates();
        isFirstStart();
    }

    private void isFirstStart() {
        SharedPreferences sp = getSharedPreferences("hasStudentInfo", Context.MODE_PRIVATE);
        boolean hasStudentInfo = sp.getBoolean("hasStudentInfo", false);

        if (!hasStudentInfo || loginText == null || passwordText == null || htmlResponse == null) {
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("hasStudentInfo", true);
            editor.apply();
            Intent intent = new Intent(MainActivity.this, AuthorizationActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        } else {
            Intent intent = new Intent(MainActivity.this, TimetableActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }

    private void checkForUpdates() {
        new CheckUpdateTask().execute();
    }

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

                    if (!CURRENT_VERSION.equals(latestVersion)) {
                        // Если текущая версия отличается от последней версии на GitHub
                        String downloadUrl = jsonObject.getJSONArray("assets").getJSONObject(0).getString("browser_download_url");

                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Доступно обновление")
                                .setMessage("Доступна новая версия приложения. Хотите обновить?")
                                .setPositiveButton("Обновить", (dialog, which) -> {
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl));
                                    startActivity(intent);
                                })
                                .setNegativeButton("Позже", null)
                                .show();
                    }
                } catch (Exception e) {
                    Log.e("UpdateCheck", "Ошибка при разборе данных обновления", e);
                }
            }
        }
    }
}
