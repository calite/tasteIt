package com.example.tasteit_java.fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.tasteit_java.ApiService.ApiClient;
import com.example.tasteit_java.ApiGetters.RecipeLoader;
import com.example.tasteit_java.R;
import com.example.tasteit_java.adapters.AdapterListViewSteps;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.SharedPreferencesSaved;
import com.example.tasteit_java.clases.Utils;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;


public class FragmentStepsRecipe extends Fragment {

    private int recipeId;
    private ListView lvSteps;
    private AdapterListViewSteps adapter;

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

        lvSteps = view.findViewById(R.id.lvSteps);
        adapter = new AdapterListViewSteps(getContext(), new ArrayList<>());
        lvSteps.setAdapter(adapter);
        bringSteps();

        return view;
    }

    private void bringSteps() {
        String accessToken = new SharedPreferencesSaved(getContext()).getSharedPreferences().getString("accessToken", "null");
        RecipeLoader recipesLoader = new RecipeLoader(ApiClient.getInstance(accessToken).getService(), getContext(), recipeId);
        recipesLoader.getRecipeById().observe(getViewLifecycleOwner(), this::onStepsLoaded);
        recipesLoader.loadRecipeById();
    }

    private void onStepsLoaded(Recipe recipes) {
        adapter.arrayListSteps.addAll(recipes.getSteps());
        adapter.notifyDataSetChanged();
    }
}