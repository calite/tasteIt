package com.example.tasteit_java.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tasteit_java.fragments.FragmentMyBook;

public class AdapterFragmentMyBook extends FragmentStateAdapter {
    private static final int NUM_PAGES = 3;
    private String token;

    public AdapterFragmentMyBook(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, String token) {
        super(fragmentManager, lifecycle);
        this.token = token;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new FragmentMyBook(token, position);
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}