package com.example.tasteit_java.fragments;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tasteit_java.ActivityRecipe;
import com.example.tasteit_java.R;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.squareup.picasso.Picasso;

public class FragmentRandom extends Fragment {
    private Recipe recipe;
    private ShimmerFrameLayout shimmer;
    private ImageButton btnNext, btnView;
    private ViewPager2 vpRandom;

    public FragmentRandom() {
        // Required empty public constructor
    }

    public FragmentRandom(Object recipe, ViewPager2 vpRandom) {
        if(recipe instanceof Recipe) {
            this.recipe = (Recipe) recipe;
        } else {
            this.recipe = null;
        }
        this.vpRandom = vpRandom;
    }

    public static FragmentRandom newInstance(Recipe recipe) {
        FragmentRandom fragment = new FragmentRandom();
        Bundle args = new Bundle();
        args.putParcelable("recipe", recipe);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipe = (Recipe) getArguments().getParcelable("recipe");
            getArguments().clear();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;

        if(recipe instanceof Recipe){
            view = inflater.inflate(R.layout.fragment_random, container, false);

            btnNext = view.findViewById(R.id.btnNext);
            btnView = view.findViewById(R.id.btnView);
            ImageView ivPhoto = view.findViewById(R.id.ivRecipePhoto);
            TextView tvRecipeName = view.findViewById(R.id.tvRecipeName);
            TextView tvNameCreator = view.findViewById(R.id.tvNameCreator);
            TextView tvDescription = view.findViewById(R.id.tvDescription);
            TextView tvDifficulty = view.findViewById(R.id.tvDifficulty);
            TextView tvCountry = view.findViewById(R.id.tvCountry);
            ChipGroup cgIngredients = view.findViewById(R.id.cgIngredients);
            RatingBar rbRating = view.findViewById(R.id.rbRating);

            try{
                Picasso.with(getContext()).load(recipe.getImage()).into(ivPhoto);
            }catch(IllegalArgumentException iae){}

            tvRecipeName.setText(recipe.getName());
            tvNameCreator.setText(recipe.getCreator());
            tvDescription.setText(recipe.getDescription());
            tvDifficulty.setText(recipe.getDifficulty());
            tvCountry.setText(recipe.getCountry());
            rbRating.setRating(recipe.getRating());

            for(String s : recipe.getIngredients()) {
                Chip chip = new Chip(getContext());
                chip.setText(s);
                chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.maroon)));
                chip.setTextColor(Color.WHITE);
                cgIngredients.addView(chip);
            }

            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vpRandom.setCurrentItem(vpRandom.getCurrentItem() + 1, true);
                }
            });

            btnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), ActivityRecipe.class);
                    intent.putExtra("creatorToken", recipe.getCreatorToken());
                    intent.putExtra("recipeId", recipe.getId());
                    getContext().startActivity(intent);
                }
            });

        } else {
            view = inflater.inflate(R.layout.hint_fragment_random, container, false);
            shimmer = view.findViewById(R.id.shimmer);
            shimmer.startShimmer();
        }

        return view;
    }
}