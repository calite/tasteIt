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

    private String biography;
    private Boolean myProfile;
    private ArrayList<Recipe> recipes;
    private HashMap<String, String> comments;
    private String uidProfile;
    private FragmentBio fragmentBio;
    private FragmentComments fragmentComments;

    public AdapterFragmentProfile(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, String uid, Boolean myProfile) {
        super(fragmentManager, lifecycle);
        this.uidProfile = uid;
        new TaskLoadUser().execute();
        this.myProfile = myProfile;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 0:
                fragmentBio = new FragmentBio();
                return fragmentBio;
            case 1:
                return FragmentPhotos.newInstance(uidProfile);
            case 2:
                fragmentComments = FragmentComments.newInstance(uidProfile, myProfile);
                return fragmentComments;
            default:
                return new FragmentBio();
        }

    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public void updateFragments(String biography) {
        fragmentBio.updateBio(biography);
        this.notifyDataSetChanged();
    }

    class TaskLoadUser extends AsyncTask<User, Void, User> {
        @Override
        protected void onPreExecute() {

        }
        @Override
        protected User doInBackground(User... hashMaps) {
            return new BdConnection().retrieveAllUserbyUid(uidProfile);
        }
        @Override
        protected void onPostExecute(User user) {
            //super.onPostExecute(recipes);
            fragmentBio.updateBio(user.getBiography());
        }
    }

}
