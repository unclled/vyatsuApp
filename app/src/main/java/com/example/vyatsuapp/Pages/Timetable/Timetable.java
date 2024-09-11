package com.example.vyatsuapp.Pages.Timetable;

import android.content.Context;

import com.example.vyatsuapp.Pages.MVPPresenter;
import com.example.vyatsuapp.Pages.MVPView;

public interface Timetable {

    interface View extends MVPView {
        void updatePressed(android.view.View view);
        void updateLastAuthorization();

        void logoutPressed();
        void setText(String timetableText);
        Context getContext();
    }

    interface Presenter extends MVPPresenter<View> {
        void getLoginAndPassword();
        void getAuthorization(String login, String password);
        String getHTMLTimetable();
        StringBuilder parseTimetable(String timetable);
        String getCurrentDay();

        StringBuilder getAllTimetable();
    }
}
