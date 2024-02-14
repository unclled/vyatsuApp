package com.example.vyatsuapp.Pages.Authorization;

import android.content.Context;

import com.example.vyatsuapp.Pages.MVPPresenter;
import com.example.vyatsuapp.Pages.MVPView;
import com.google.android.material.textfield.TextInputLayout;

public interface Authorization {

    interface View extends MVPView {
        Context getContext();
        String getLogin();
        String getPassword();

        //получение самих полей для проверки на пустоту
        TextInputLayout getLoginField();
        TextInputLayout getPasswordField();

        void loginPressed(android.view.View view);

        void startLoading();

        //тостер
        void tryAgain(String error);

        void toNextPage(String html);

        boolean editTextIsNull(TextInputLayout text);

        boolean isOnline();

        //блок/анлок нажатий
        void blockWindow();
        void unlockWindow();
    }

    interface Presenter extends MVPPresenter<View> {

        void getAuthorization();
        void checkLogin(String login);
        void checkPassword(String password);

        void applyHTMLResponse(String htmlContent);

        void saveUserInfo();
    }
}
