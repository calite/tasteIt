package com.example.tasteit_java.adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasteit_java.ActivityRecipe;
import com.example.tasteit_java.R;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.Utils;

import java.util.ArrayList;

public class AdapterRecyclerMain extends RecyclerView.Adapter<AdapterRecyclerMain.ViewHolder> {

    private ArrayList<Recipe> listRecipes;

    private static final int TYPE_RECIPE = 0;
    private static final int TYPE_FOOTER = 1;

    public AdapterRecyclerMain(ArrayList<Recipe> listRecipes) {
        this.listRecipes = listRecipes;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivPhotoRecipe;
        private final TextView tvRating;
        private final TextView tvNameRecipe;
        private final TextView tvNameCreator;
        private final TextView tvDescriptionRecipe;

        private int recipeId;
        private String creatorToken;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            ivPhotoRecipe = view.findViewById(R.id.ivPhotoRecipe);
            tvRating = view.findViewById(R.id.tvRating);
            tvNameRecipe = view.findViewById(R.id.tvNameRecipe);
            tvNameCreator = view.findViewById(R.id.tvNameCreator);
            tvDescriptionRecipe = view.findViewById(R.id.tvDescriptionRecipe);

            //Toast.makeText(view.getContext(), creatorToken, Toast.LENGTH_SHORT).show();

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(view.getContext(), ActivityRecipe.class);
                    i.putExtra("creatorToken", creatorToken);
                    i.putExtra("recipeId", recipeId);
                    view.getContext().startActivity(i);
                }
            });
        }

        void bindRow(@NonNull Recipe recipe) {
            tvNameRecipe.setText(recipe.getName());
            tvNameCreator.setText(recipe.getCreator());
            tvDescriptionRecipe.setText(recipe.getDescription());
            tvRating.setText(recipe.getRating());
            Bitmap bitmap = Utils.decodeBase64(recipe.getImage());
            ivPhotoRecipe.setImageBitmap(bitmap);
            recipeId = recipe.getId();
            creatorToken = recipe.getCreatorToken();
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

        if (viewType == TYPE_RECIPE) {
            View row = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_main_recycler, viewGroup, false);
            return new ViewHolder(row);
        } else {
            View row = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.loading_footer,
                    viewGroup, false);
            return new ViewHolder(row);
        }

        /*LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.item_main_recycler, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;*/
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        /*viewHolder.tvNameRecipe.setText(listRecipes.get(position).getName());
        viewHolder.tvNameCreator.setText(listRecipes.get(position).getCreator());
        viewHolder.tvDescriptionRecipe.setText(listRecipes.get(position).getDescription());
        viewHolder.tvRating.setText(listRecipes.get(position).getRating());
        Bitmap bitmap = Utils.decodeBase64(listRecipes.get(position).getImage());
        viewHolder.ivPhotoRecipe.setImageBitmap(bitmap);
        viewHolder.recipeId = listRecipes.get(position).getId();
        viewHolder.creatorToken = listRecipes.get(position).getCreatorToken();*/

        if (viewHolder instanceof AdapterRecyclerMain.ViewHolder) {
            viewHolder.bindRow(listRecipes.get(position));
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return listRecipes.size();
    }


}
