package com.example.vyatsuapp.Pages.Authorization;

import com.example.vyatsuapp.Pages.PresenterBase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthorizationPresenter extends PresenterBase<Authorization.View> implements Authorization.Presenter {

    @Override
    public void viewIsReady() {
    }

    @Override
    public void checkLogin(String login) {
        Pattern pattern = Pattern.compile("^(stud\\\\?[0-9]{6})|^(cstud\\\\?[0-9]{6})|^(usr\\\\?[0-9]{6})/gm");
        Matcher matcher = pattern.matcher(login);
        if (matcher.find()) {
            getView().startLoading();
            getView().getAuthorization();
        } else {
            getView().tryAgain("Неверно указан логин!");
        }
    }

    @Override
    public void checkPassword(String password) {
        /* TODO проверка пароля */
    }
}
