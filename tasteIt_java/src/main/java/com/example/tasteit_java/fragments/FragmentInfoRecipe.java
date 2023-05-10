package com.example.tasteit_java.fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.tasteit_java.ApiService.ApiClient;
import com.example.tasteit_java.ApiUtils.RecipeLoader;
import com.example.tasteit_java.R;
import com.example.tasteit_java.adapters.AdapterFragmentRecipe;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.Utils;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.squareup.picasso.Picasso;

public class FragmentInfoRecipe extends Fragment {

    private static int recipeId;
    private TextView tvDescription;
    private TextView tvDifficulty;
    private TextView tvCountry;
    private ChipGroup cgTags;
    private ChipGroup cgIngredients;

    public FragmentInfoRecipe() {
        // Required empty public constructor
    }

    public static FragmentInfoRecipe newInstance(int recipeId) {
        FragmentInfoRecipe fragment = new FragmentInfoRecipe();
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
        View view = inflater.inflate(R.layout.fragment_info_recipe, container, false);

        tvDescription = view.findViewById(R.id.tvDescription);
        tvDifficulty = view.findViewById(R.id.tvDifficulty);
        tvCountry = view.findViewById(R.id.tvCountry);
        cgTags = view.findViewById(R.id.cgTags);
        cgIngredients = view.findViewById(R.id.cgIngredients);

        bringInfoRecipe();

        return view;
    }

    private void bringInfoRecipe() {
        RecipeLoader recipesLoader = new RecipeLoader(ApiClient.getInstance(Utils.getUserAcessToken()).getService(), getContext(), recipeId);
        recipesLoader.getRecipeById().observe(getViewLifecycleOwner(), this::onInfoRecipeLoaded);
        recipesLoader.loadRecipeById();
    }

    private void onInfoRecipeLoaded(Recipe recipe) {
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
    }
}