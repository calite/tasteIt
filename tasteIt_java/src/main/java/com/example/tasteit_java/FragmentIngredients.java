package com.example.tasteit_java;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

public class FragmentIngredients extends Fragment {

    private static EditText etIngredientName;
    private ChipGroup cgIngredients;
    private static ArrayList<String> ingredients;
    private Button bAddIngredient;

    public FragmentIngredients() {
        // Required empty public constructor
    }

    public static FragmentIngredients newInstance() {
        FragmentIngredients fragment = new FragmentIngredients();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ingredients, container, false);

        //ingredientes
        ingredients = new ArrayList<>();
        cgIngredients = view.findViewById(R.id.cgIngredients);
        bAddIngredient = view.findViewById(R.id.bAddIngredient);
        etIngredientName = view.findViewById(R.id.etIngredientName);
        bAddIngredient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Chip chip = new Chip(getContext());
                chip.setText(etIngredientName.getText().toString());
                chip.setCloseIconResource(R.drawable.ic_close);
                chip.setCloseIconVisible(true);
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ingredients.remove(etIngredientName.getText().toString());
                        cgIngredients.removeView(chip);
                    }
                });
                cgIngredients.addView(chip);
                ingredients.add(etIngredientName.getText().toString());
            }
        });

        return view;
    }

    public static ArrayList<String> getIngredients() {
        return ingredients;
    }
}