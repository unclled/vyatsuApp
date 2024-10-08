package com.example.vyatsuapp.Utils.MethodsForMVP;

//общие методы по работе с View
public abstract class PresenterBase<T extends MVPView> implements MVPPresenter<T> { //наследуется всеми презентерами
    private T view;

    public void attachView(T MVPView) {
        view = MVPView;
    }

    public void detachView() {
        view = null;
    }

    public T getView() {
        return view;
    }

    @Override
    public void destroy() {
        /* TODO */
    }
}
