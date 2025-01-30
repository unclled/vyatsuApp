package com.example.vyatsuapp.Pages.Timetable;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vyatsuapp.Pages.Authorization.AuthorizationActivity;
import com.example.vyatsuapp.R;
import com.example.vyatsuapp.Utils.Adapters.TimetableAdapter;
import com.example.vyatsuapp.Utils.UtilsClass;
import com.github.leandroborgesferreira.loadingbutton.customViews.CircularProgressButton;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TimetableActivity extends AppCompatActivity implements Timetable.View {
    private TextView lastUpdate;
    private TextView noDataText;
    private RecyclerView recyclerView;
    private TimetableAdapter adapter;
    private Timetable.Presenter presenter;
    private String studyGroup;
    private CircularProgressButton updateButton;
    private TextInputLayout studyGroupLayout;
    private AlertDialog ad;
    private ProgressBar progressBar;
    private final UtilsClass utils = new UtilsClass();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timetable_activity);

        recyclerView = findViewById(R.id.recyclerView);
        noDataText = findViewById(R.id.noDataText);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        lastUpdate = findViewById(R.id.lastUpdate);
        updateButton = findViewById(R.id.updateButton);
        studyGroupLayout = findViewById(R.id.studyGroup);
        progressBar = findViewById(R.id.progressBar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prepareStep();
    }

    private void prepareStep() {
        presenter = new TimetablePresenter();
        presenter.attachView(this);
        presenter.setContext(this);
        presenter.viewIsReady();

        List<String> keys = new ArrayList<>();
        keys.add("STUDY_GROUP");
        keys.add("LAST_UPDATE");
        List<String> value = utils.loadFromPreferences(keys, this);
        studyGroup = value.isEmpty() ? null : value.get(0);
        if (value.get(1) == null) {
            updateLastAuthorization();
        } else {
            lastUpdate.setText(value.get(1));
        }

        recyclerView.setAdapter(adapter);

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
            if (adapter.getItemCount() == 0) {
                noDataText.setVisibility(View.VISIBLE);
            } else {
                noDataText.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void updatePressed(View view) {
        if (!isNetworkAvailable()) {
            Animation rotate = AnimationUtils.loadAnimation(this, R.anim.update_rotate);
            updateButton.startAnimation(rotate);
            presenter.getLoginAndPassword();
        } else {
            utils.showToastShort("Отсутствует подключение к интернету!", this);
        }
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
        runOnUiThread(() -> {
            lastUpdate.setText(updated);
            setText(presenter.getAllTimetable().toString());
        });

        utils.toMapAndSaveSP("LAST_UPDATE", updated, this);
    }

    @Override
    public void logoutPressed() {
        presenter.logout();
        Intent intent = new Intent(TimetableActivity.this, AuthorizationActivity.class);
        startActivity(intent);
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

    public void showEditGroupWindow() {
        View dialogView = getLayoutInflater().inflate(R.layout.edit_group, null);

        studyGroupLayout = dialogView.findViewById(R.id.studyGroup);

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setView(dialogView);
        ad = adb.create();
        Objects.requireNonNull(ad.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ad.show();
    }


    public void saveGroup(View view) {
        if (studyGroupLayout == null || studyGroupLayout.getEditText() == null) {
            utils.showToastShort("Ошибка инициализации", this);
        } else {
            String inputText = studyGroupLayout.getEditText().getText().toString();

            if (!inputText.isEmpty()) {
                studyGroup = inputText;
                presenter.saveGroupToPreferences(studyGroup);
                utils.showToastShort("Группа сохранена: " + studyGroup, this);
                hideEditGroupWindow();
            } else {
                utils.showToastShort("Группа не может быть пустой", this);
            }
        }
    }

    public void closeEditGroup(View view) {
        hideEditGroupWindow();
    }

    public void hideEditGroupWindow() {
        ad.dismiss();
    }

    public void downloadPDF(View view) {
        presenter.downloadPDF(studyGroup);
    }

    @Override
    public void animateDownload(int progress) {
        runOnUiThread(() -> {
            Animator anim = ObjectAnimator.ofInt(progressBar, "progress", progressBar.getProgress(), progress);

            anim.setDuration(350);
            anim.setInterpolator(new DecelerateInterpolator());
            anim.start();
            anim.addListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationStart(@NonNull Animator animation) {
                }

                @Override
                public void onAnimationEnd(@NonNull Animator animation) {
                    progressBar.setProgress(0);
                }

                @Override
                public void onAnimationCancel(@NonNull Animator animation) {
                }

                @Override
                public void onAnimationRepeat(@NonNull Animator animation) {
                }
            });
        });
    }

    @Override
    public void openPDF(String fileName) {
        File pdfFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
        Uri pdfUri = FileProvider.getUriForFile(this, "com.example.vyatsuapp.fileprovider", pdfFile);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Intent chooser = Intent.createChooser(intent, "Открыть PDF");
        startActivity(chooser);
    }

    @Override
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo == null || !networkInfo.isConnected();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        //запрет возвращения назад
    }
}