package com.example.tasteit_java.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.tasteit_java.R;
import com.example.tasteit_java.clases.Recipe;

public class FragmentRandom extends Fragment {


    private Recipe recipe;

    public FragmentRandom() {
        // Required empty public constructor
    }

    public static FragmentRandom newInstance(Recipe recipe) {
        FragmentRandom fragment = new FragmentRandom();
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
        View view = inflater.inflate(R.layout.fragment_random, container, false);

        TextView tvRecipeName = view.findViewById(R.id.tvRecipeName);

        tvRecipeName.setText(recipe.getName());


        return view;
    }
}