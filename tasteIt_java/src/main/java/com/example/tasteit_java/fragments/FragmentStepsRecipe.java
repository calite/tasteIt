package com.example.tasteit_java.fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.tasteit_java.ApiService.ApiClient;
import com.example.tasteit_java.ApiGetters.RecipeLoader;
import com.example.tasteit_java.R;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.Utils;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;


public class FragmentStepsRecipe extends Fragment {

    private int recipeId;
    private ChipGroup cgSteps;

    public FragmentStepsRecipe() {
        // Required empty public constructor
    }

    public static FragmentStepsRecipe newInstance(int recipeId) {
        FragmentStepsRecipe fragment = new FragmentStepsRecipe();
        Bundle args = new Bundle();
        args.putInt("recipeId", recipeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipeId = getArguments().getInt("recipeId");
            getArguments().clear();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_steps_recipe, container, false);

        cgSteps = view.findViewById(R.id.cgSteps);
        cgSteps.setChipSpacingHorizontal(3000);

        bringSteps();

        return view;
    }

    private void bringSteps() {
        RecipeLoader recipesLoader = new RecipeLoader(ApiClient.getInstance(Utils.getUserAcessToken()).getService(), getContext(), recipeId);
        recipesLoader.getRecipeById().observe(getViewLifecycleOwner(), this::onStepsLoaded);
        recipesLoader.loadRecipeById();
    }

    private void onStepsLoaded(Recipe recipes) {
        for(String s : recipes.getSteps()) {
            Chip chip = new Chip(getContext());
            chip.setText(s);
            chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.maroon)));
            chip.setTextColor(Color.WHITE);
            cgSteps.addView(chip);
        }
    }
}