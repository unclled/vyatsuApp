package com.example.vyatsuapp.Pages.Authorization;

import com.example.vyatsuapp.Pages.MVPPresenter;
import com.example.vyatsuapp.Pages.MVPView;

public interface Authorization {

    interface View extends MVPView {

        void loginPressed(android.view.View view);

        String getLogin();
        String getPassword();

        void setLogin(String login);
        void setPassword(String password);

        void startLoading();

        void getAuthorization();

        void tryAgain(String error);

        void toNextPage(String html);

        void saveUserInfo();

        boolean isOnline();

    }

    interface Presenter extends MVPPresenter<View> {
        void checkLogin(String login);
        void checkPassword(String password);
    }
}
