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

    private int idRecipe;

    public AdapterFragmentRecipe(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, int idRecipe) {
        super(fragmentManager, lifecycle);
        this.idRecipe = idRecipe;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 1:
                return FragmentStepsRecipe.newInstance(idRecipe);
            case 2:
                return FragmentCommentsRecipe.newInstance(idRecipe);
            default:
                return FragmentInfoRecipe.newInstance(idRecipe);
        }

    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
