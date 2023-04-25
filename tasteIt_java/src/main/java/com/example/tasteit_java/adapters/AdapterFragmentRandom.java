package com.example.tasteit_java.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.fragments.FragmentRandom;
import com.example.tasteit_java.fragments.FragmentSearch;

import java.util.ArrayList;

public class AdapterFragmentRandom extends FragmentStateAdapter {
    private static final int NUM_PAGES = 6;
    private ArrayList<Recipe> recipes;

    public AdapterFragmentRandom(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, ArrayList<Recipe> recipes) {
        super(fragmentManager, lifecycle);
        this.recipes = new ArrayList<>(recipes);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new FragmentRandom(recipes.get(position));
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }

}
