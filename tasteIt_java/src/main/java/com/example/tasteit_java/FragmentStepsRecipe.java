package com.example.tasteit_java;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.tasteit_java.clases.Recipe;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;


public class FragmentStepsRecipe extends Fragment {

    private static Recipe recipe;

    public FragmentStepsRecipe() {
        // Required empty public constructor
    }

    public static FragmentStepsRecipe newInstance(Recipe recipe) {
        FragmentStepsRecipe fragment = new FragmentStepsRecipe();
        Bundle args = new Bundle();
        args.putSerializable("recipe", recipe);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipe = (Recipe) getArguments().getSerializable("recipe");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_steps_recipe, container, false);

        ChipGroup cgSteps = view.findViewById(R.id.cgSteps);
        cgSteps.setChipSpacingHorizontal(3000);

        for(String s : recipe.getSteps()) {
            Chip chip = new Chip(getContext());
            chip.setText(s);
            chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.maroon)));
            chip.setTextColor(Color.WHITE);
            cgSteps.addView(chip);
        }


        return view;
    }
}