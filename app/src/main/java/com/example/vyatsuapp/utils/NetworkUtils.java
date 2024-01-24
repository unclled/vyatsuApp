package com.example.vyatsuapp.utils;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {
    private static final String VyatsuTimeTableURL = "";
    private static final String fullTimeTimeTable = "";

    public static URL generateURL() {
        Uri builtUri = Uri.parse(VyatsuTimeTableURL + fullTimeTimeTable)
                .buildUpon()
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return url;
    }

    public static String getResponseFromURL(URL url) {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();

            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            urlConnection.disconnect();
        }
    }
}
