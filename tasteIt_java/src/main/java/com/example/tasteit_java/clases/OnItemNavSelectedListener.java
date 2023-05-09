package com.example.tasteit_java.clases;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.tasteit_java.ActivityMain;
import com.example.tasteit_java.ActivityMyBook;
import com.example.tasteit_java.ActivityRandom;
import com.example.tasteit_java.ActivitySearch;
import com.example.tasteit_java.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class OnItemNavSelectedListener implements BottomNavigationView.OnItemSelectedListener {

    private Activity activity;

    public OnItemNavSelectedListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bHome: {
                if (!activity.getLocalClassName().equals("ActivityMain")) {
                    Intent i = new Intent(activity.getApplicationContext(), ActivityMain.class);
                    activity.finish();
                    activity.startActivity(i);
                } else {
                    ((ActivityMain) activity).updateList();
                }
                return true;
            }
            case R.id.bSearch: {
                if (!activity.getLocalClassName().equals("ActivitySearch")) {
                    Intent i = new Intent(activity.getApplicationContext(), ActivitySearch.class);
                    if (!activity.getLocalClassName().equals("ActivityMain")) {
                        activity.finish();
                        activity.startActivity(i);
                    } else {
                        activity.startActivity(i);
                    }
                }
                return true;
            }
            case R.id.bRandom: {
                Intent i = new Intent(activity.getApplicationContext(), ActivityRandom.class);
                if (!activity.getLocalClassName().equals("ActivityMain")) {
                    activity.finish();
                    activity.startActivity(i);
                } else {
                    activity.startActivity(i);
                }
                return true;
            }
            case R.id.bMyBook: {
                if (!activity.getLocalClassName().equals("ActivityMyBook")) {
                    Intent i = new Intent(activity.getApplicationContext(), ActivityMyBook.class);
                    if (!activity.getLocalClassName().equals("ActivityMain")) {
                        activity.finish();
                        activity.startActivity(i);
                    } else {
                        activity.startActivity(i);
                    }
                }
                return true;
            }
            default: {
                return false;
            }
        }
    }
}
