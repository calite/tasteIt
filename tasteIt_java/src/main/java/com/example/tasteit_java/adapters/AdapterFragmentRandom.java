package com.example.tasteit_java.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.fragments.FragmentRandom;

import java.util.ArrayList;

public class AdapterFragmentRandom extends FragmentStateAdapter {
    private static final int NUM_PAGES = 6;
    private ArrayList<Object> recipes;
    private ViewPager2 vpRandom;

    public AdapterFragmentRandom(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, ArrayList<Recipe> recipes, ViewPager2 vpRandom) {
        super(fragmentManager, lifecycle);
        this.recipes = new ArrayList<>(recipes);
        this.recipes.add(null);
        this.vpRandom = vpRandom;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new FragmentRandom(recipes.get(position), vpRandom);
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }

}
