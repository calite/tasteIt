package com.example.tasteit_java.adapters;

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

    public AdapterFragmentProfile(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, User user, Boolean myProfile) {
        super(fragmentManager, lifecycle);

        this.biography = user.getBiography();
        this.recipes = user.getUserRecipes();
        this.comments = user.getUserComments();
        this.myProfile = myProfile;
    }

    public AdapterFragmentProfile(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, User user, Boolean myProfile, String uid) {
        super(fragmentManager, lifecycle);

        this.biography = user.getBiography();
        this.recipes = user.getUserRecipes();
        this.comments = user.getUserComments();
        this.myProfile = myProfile;
        this.uidProfile = uid;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 0:
                fragmentBio = FragmentBio.newInstance(biography);
                return fragmentBio;
            case 1:
                return FragmentPhotos.newInstance(recipes);
            case 2:
                if(myProfile) {
                    fragmentComments = FragmentComments.newInstance(comments, myProfile);
                } else {
                    fragmentComments = FragmentComments.newInstance(comments, myProfile, uidProfile);
                }
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
}
