package com.example.tasteit_java;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tasteit_java.clases.Recipe;

import java.util.ArrayList;

public class AdapterRecyclerMain extends RecyclerView.Adapter<AdapterRecyclerMain.ViewHolder> {

    private ArrayList<Recipe> listRecipes;

    public AdapterRecyclerMain(ArrayList<Recipe> listRecipes) {
        this.listRecipes = listRecipes;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivPhotoRecipe;
        private final TextView tvRating;
        private final TextView tvNameRecipe;
        private final TextView tvNameCreator;
        private final TextView tvDescriptionRecipe;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            ivPhotoRecipe = view.findViewById(R.id.ivPhotoRecipe);
            tvRating = view.findViewById(R.id.tvRating);
            tvNameRecipe = view.findViewById(R.id.tvNameRecipe);
            tvNameCreator = view.findViewById(R.id.tvNameCreator);
            tvDescriptionRecipe = view.findViewById(R.id.tvDescriptionRecipe);
        }

        public TextView getTvNameRecipe() {
            return tvNameRecipe;
        }

        public TextView getTvDescriptionRecipe() {
            return tvDescriptionRecipe;
        }

        public TextView getTvNameCreator() {
            return tvNameCreator;
        }

        public TextView getTvRating() {
            return tvRating;
        }

        public ImageView getIvPhotoRecipe() {
            return ivPhotoRecipe;
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.item_main_recycler,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        viewHolder.tvNameRecipe.setText(listRecipes.get(position).getName());
        viewHolder.tvNameCreator.setText(listRecipes.get(position).getCreator());
        viewHolder.tvDescriptionRecipe.setText(listRecipes.get(position).getDescription());
        viewHolder.tvRating.setText(listRecipes.get(position).getRating());
        viewHolder.ivPhotoRecipe.setImageResource(listRecipes.get(position).getImg());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return listRecipes.size();
    }


}
