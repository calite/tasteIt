package com.example.tasteit_java.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tasteit_java.fragments.FragmentInfoNewRecipe;
import com.example.tasteit_java.fragments.FragmentIngredientsNewRecipe;
import com.example.tasteit_java.fragments.FragmentStepsNewRecipe;

public class AdapterFragmentNewRecipe extends FragmentStateAdapter {

    public AdapterFragmentNewRecipe(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 0:
                return new FragmentInfoNewRecipe();
            case 1:
                return new FragmentStepsNewRecipe();
            case 2:
                return new FragmentIngredientsNewRecipe();
            default:
                return new FragmentInfoNewRecipe();
        }

    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
