package com.example.tasteit_java.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tasteit_java.fragments.FragmentCommentsRecipe;
import com.example.tasteit_java.fragments.FragmentInfoRecipe;
import com.example.tasteit_java.fragments.FragmentStepsRecipe;
import com.example.tasteit_java.clases.Recipe;

public class AdapterFragmentRecipe extends FragmentStateAdapter {

    private Recipe recipe;

    public AdapterFragmentRecipe(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Recipe recipe) {
        super(fragmentManager, lifecycle);
        this.recipe = recipe;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 0:
                return FragmentInfoRecipe.newInstance(recipe);
            case 1:
                return FragmentStepsRecipe.newInstance(recipe);
            case 2:
                return FragmentCommentsRecipe.newInstance(recipe.getId());
            default:
                return FragmentInfoRecipe.newInstance(recipe);
        }

    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
