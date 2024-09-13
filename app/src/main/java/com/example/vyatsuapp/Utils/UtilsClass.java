package com.example.vyatsuapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UtilsClass {
    public void showToastLong(String text, Context context) {
        Toast toast = Toast.makeText(
                context,
                text,
                Toast.LENGTH_LONG);
        toast.show();
    }

    public void showToastShort(String text, Context context) {
        Toast toast = Toast.makeText(
                context,
                text,
                Toast.LENGTH_LONG);
        toast.show();
    }

    public void saveToPreferences(Map<String, String> value, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (var entry : value.entrySet()) {
            editor.putString(entry.getKey(), entry.getValue());
        }
        editor.apply();
    }

    public List<String> loadFromPreferences(List<String> keys, Context context) {
        List<String> loadedList = new ArrayList<>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        for (var key : keys) {
            loadedList.add(sharedPreferences.getString(key, null));
        }

        return loadedList;
    }

    public void toMapAndSaveSP(String key, String value, Context context) {
        Map<String, String> values = new HashMap<>();
        values.put(key, value);
        saveToPreferences(values, context);
    }
}
