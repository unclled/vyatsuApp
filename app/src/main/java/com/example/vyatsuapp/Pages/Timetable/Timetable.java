package com.example.vyatsuapp.Pages.Timetable;

import com.example.vyatsuapp.Pages.MVPPresenter;
import com.example.vyatsuapp.Pages.MVPView;

public interface Timetable {

    interface View extends MVPView {

        StringBuilder parseTimetable(String timetable);

        String getCurrentDay();

        String getHTMLTimetable();

        void timetablePressed(android.view.View view);

        void settingsPressed(android.view.View view);

        void personalDataPressed(android.view.View view);

        void surveysPressed(android.view.View view);

        void documentsPressed(android.view.View view);
    }

    interface Presenter extends MVPPresenter<View> {
        boolean hasHTML();
    }
}
