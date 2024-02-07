package com.example.vyatsuapp.utils;

import android.content.res.Resources;

import androidx.annotation.NonNull;

import java.net.SocketTimeoutException;

public class ErrorBundle {
    private final String appMessage;
    private final String rawMessage;

    private ErrorBundle(String message, String reason) {
        this.appMessage = message;
        this.rawMessage = reason;
    }

    @NonNull
    public static ErrorBundle adapt(Throwable throwable) {
        if (throwable instanceof SocketTimeoutException) {
            String reason = null;
            if (throwable.getCause() != null) {
                reason = throwable.getCause().getMessage();
            }
            if (reason == null) {
                reason = "Connection timed out";
            }
            return new ErrorBundle("Socket timeout", reason);
        }
        /*if (throwable instanceof AuthorizeException) {
            return new ErrorBundle("Authorization exception", null);
        }*/
        if (throwable instanceof Resources.NotFoundException) {
            return new ErrorBundle("NotFoundException", null);
        }
        return new ErrorBundle("Unknown exception", throwable.getMessage());
    }

    @NonNull
    public String getAppMessage() {
        return appMessage;
    }
    //@Nullable
    public String getRawMessage() {
        return rawMessage;
    }
}
