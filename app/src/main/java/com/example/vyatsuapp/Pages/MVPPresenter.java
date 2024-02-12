package com.example.vyatsuapp.Pages;

public interface MVPPresenter<V extends MVPView> { //интерфейс для всех презентеров
    void attachView(V MVPView); //передача View презентеру

    void viewIsReady(); //сигнал, что View готов к работе

    void detachView(); //обнуление ссылки на Activity

    void destroy(); //View завершил работу
}
