package com.example.tasteit_java.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tasteit_java.fragments.FragmentFollowedRecipes;
import com.example.tasteit_java.fragments.FragmentLikedRecipes;
import com.example.tasteit_java.fragments.FragmentMyRecipes;

public class AdapterFragmentMyBook extends FragmentStateAdapter {

    private String token;

    public AdapterFragmentMyBook(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, String token) {
        super(fragmentManager, lifecycle);
        this.token = token;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return FragmentMyRecipes.newInstance(token);
            case 1:
                return FragmentLikedRecipes.newInstance(token);
            case 2:
                return FragmentFollowedRecipes.newInstance(token);
            default:
                return FragmentMyRecipes.newInstance(token);
        }

    }

    @Override
    public int getItemCount() {
        return 3;
    }
}