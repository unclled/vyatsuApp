package com.example.vyatsuapp.Pages.Timetable;

import android.content.Context;

import com.example.vyatsuapp.Utils.MethodsForMVP.MVPPresenter;
import com.example.vyatsuapp.Utils.MethodsForMVP.MVPView;

public interface Timetable {

    interface View extends MVPView {
        void updatePressed(android.view.View view);

        void updateLastAuthorization();

        void logoutPressed();

        void setText(String timetableText);


        void showEditGroupWindow();

        boolean isNetworkAvailable();

        void openPDF(String fileName);

        void animateDownload(int progress);

    }

    interface Presenter extends MVPPresenter<View> {
        void getLoginAndPassword();

        void getAuthorization(String login, String password);

        String getHTMLTimetable();

        StringBuilder parseTimetable(String timetable);

        String getCurrentDay();

        StringBuilder getAllTimetable();

        void logout();

        void checkForUpdates();

        void saveGroupToPreferences(String group);

        String getActualTimetable(String studyGroup);

        void setContext(Context context);

        void downloadPDF(String studyGroup);
    }
}
