package com.example.tasteit_java.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.tasteit_java.fragments.FragmentSearch;

public class AdapterFragmentSearch extends FragmentStateAdapter {
    private static final int NUM_PAGES = 4;
    private String busqueda;

    public AdapterFragmentSearch(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, String busqueda) {
        super(fragmentManager, lifecycle);
        this.busqueda = busqueda;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new FragmentSearch(busqueda, position);
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }

}
