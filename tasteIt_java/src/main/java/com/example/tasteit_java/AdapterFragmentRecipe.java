package com.example.tasteit_java;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AdapterFragmentRecipe extends FragmentStateAdapter {

    public AdapterFragmentRecipe(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 0:
                return new FragmentInfo();
            case 1:
                return new FragmentSteps();
            case 2:
                return new FragmentIngredients();
            default:
                return new FragmentInfo();
        }

    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
