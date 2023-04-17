package com.example.tasteit_java.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasteit_java.ActivityRecipe;
import com.example.tasteit_java.R;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.Utils;

import java.util.ArrayList;

public class AdapterRecyclerPhotosProfile extends RecyclerView.Adapter<AdapterRecyclerPhotosProfile.ViewHolder> {

    private Context context;
    public ArrayList<Recipe> arrayListPhotos;

    public AdapterRecyclerPhotosProfile(Context context) {
        this.context = context;
        arrayListPhotos = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_profile_gridview, null, false);
        return new ViewHolder(view);
        //return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindRow(arrayListPhotos.get(position));
    }

    @Override
    public long getItemId(int i) {
        return arrayListPhotos.get(i).hashCode();
    }

    @Override
    public int getItemCount() {
        return arrayListPhotos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivPhoto;
        private int recipeId;
        private String creatorToken;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);

            ivPhoto.setOnClickListener(new View.OnClickListener() {
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
            Bitmap bitmap = Utils.decodeBase64(recipe.getImage());
            ivPhoto.setImageBitmap(bitmap);
            recipeId = recipe.getId();
            creatorToken = recipe.getCreatorToken();
        }
    }

}
