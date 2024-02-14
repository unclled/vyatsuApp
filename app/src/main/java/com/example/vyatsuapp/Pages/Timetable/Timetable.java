package com.example.vyatsuapp.Pages.Timetable;

import android.content.Context;

import com.example.vyatsuapp.Pages.MVPPresenter;
import com.example.vyatsuapp.Pages.MVPView;

public interface Timetable {

    interface View extends MVPView {
        void updatePressed(android.view.View view);
        void updateLastAuthorization();
        void timetablePressed(android.view.View view);
        void settingsPressed(android.view.View view);
        void personalDataPressed(android.view.View view);
        void setText(String timetableText);
        Context getContext();
        void setHeaderText(String text);
    }

    interface Presenter extends MVPPresenter<View> {
        void getLoginAndPassword();
        void getAuthorization(String login, String password);
        String getHTMLTimetable();
        StringBuilder parseTimetable(String timetable);
        String getCurrentDay();
    }
}
