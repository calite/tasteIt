package com.example.tasteit_java.adapters;

import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.adapter.FragmentViewHolder;

import com.example.tasteit_java.fragments.FragmentBio;
import com.example.tasteit_java.fragments.FragmentComments;
import com.example.tasteit_java.fragments.FragmentPhotos;
import com.example.tasteit_java.fragments.FragmentSearch;

import java.util.ArrayList;
import java.util.List;

public class AdapterFragmentSearch extends FragmentStateAdapter {
    private static final int NUM_PAGES = 4;
    private ArrayList<Object> dataListAux;
    private String busqueda;

    public AdapterFragmentSearch(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, String busqueda, ArrayList<Object> dataListAux) {
        super(fragmentManager, lifecycle);
        this.busqueda = busqueda;
        this.dataListAux = dataListAux;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new FragmentSearch(busqueda, position, dataListAux);
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }

}
