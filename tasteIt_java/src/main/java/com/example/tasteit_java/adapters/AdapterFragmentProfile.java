package com.example.tasteit_java.adapters;

import android.os.AsyncTask;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.fragments.FragmentBio;
import com.example.tasteit_java.fragments.FragmentComments;
import com.example.tasteit_java.fragments.FragmentPhotos;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.User;

import java.util.ArrayList;
import java.util.HashMap;

public class AdapterFragmentProfile extends FragmentStateAdapter {

    private Boolean myProfile;
    private String uidProfile;
    private FragmentComments fragmentComments;

    public AdapterFragmentProfile(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, String uid, Boolean myProfile) {
        super(fragmentManager, lifecycle);
        this.uidProfile = uid;
        this.myProfile = myProfile;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 0:
                return FragmentBio.newInstance(uidProfile);
            case 1:
                return FragmentPhotos.newInstance(uidProfile);
            case 2:
                fragmentComments = FragmentComments.newInstance(uidProfile, myProfile);
                return fragmentComments;
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

}
