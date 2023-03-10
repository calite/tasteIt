package com.example.tasteit_java;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.tasteit_java.clases.Recipe;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class FragmentInfoRecipe extends Fragment {

    private static Recipe recipe;

    public FragmentInfoRecipe() {
        // Required empty public constructor
    }



    public static FragmentInfoRecipe newInstance(Recipe recipe) {
        FragmentInfoRecipe fragment = new FragmentInfoRecipe();
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
        View view = inflater.inflate(R.layout.fragment_info_recipe, container, false);

        TextView tvDescription = view.findViewById(R.id.tvDescription);
        TextView tvDifficulty = view.findViewById(R.id.tvDifficulty);
        TextView tvCountry = view.findViewById(R.id.tvCountry);
        ChipGroup cgTags = view.findViewById(R.id.cgTags);
        ChipGroup cgIngredients = view.findViewById(R.id.cgIngredients);

        tvDescription.setText(recipe.getDescription());
        tvDifficulty.setText(recipe.getDifficulty() + "");
        tvCountry.setText(recipe.getCountry());
        for(String s : recipe.getTags()) {
            Chip chip = new Chip(getContext());
            chip.setText(s);
            chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.maroon)));
            chip.setTextColor(Color.WHITE);
            cgTags.addView(chip);
        }
        for(String s : recipe.getIngredients()) {
            Chip chip = new Chip(getContext());
            chip.setText(s);
            chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.maroon)));
            chip.setTextColor(Color.WHITE);
            cgIngredients.addView(chip);
        }



        return view;
    }
}