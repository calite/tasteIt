package com.example.tasteit_java.clases;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesSaved {

    private final SharedPreferences sharedPreferences;

    public SharedPreferencesSaved(Activity activity) {
        this.sharedPreferences = activity.getSharedPreferences("userDataTasteIt", Context.MODE_PRIVATE);
    }

    public SharedPreferencesSaved(Context activity) {
        this.sharedPreferences = activity.getSharedPreferences("userDataTasteIt", Context.MODE_PRIVATE);
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public SharedPreferences.Editor getEditer() {
        return sharedPreferences.edit();
    }

    public void removeValue(String key) {
        SharedPreferences.Editor editer = sharedPreferences.edit();
        editer.remove(key);
        editer.commit();
    }
}
