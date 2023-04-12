package com.example.tasteit_java.adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.*;

import com.example.tasteit_java.ActivityRecipe;
import com.example.tasteit_java.R;
import com.example.tasteit_java.clases.OnLoadMoreListener;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.Utils;

import java.util.ArrayList;

public class AdapterEndlessRecyclerMain extends RecyclerView.Adapter {

    public ArrayList<Object> dataList;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    public AdapterEndlessRecyclerMain(ArrayList<Object> listRecipes, RecyclerView recyclerView) {
        this.dataList = new ArrayList<>(listRecipes);

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView,
                                       int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager
                            .findLastVisibleItemPosition();
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }

    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position) instanceof Recipe ? TYPE_ITEM : TYPE_FOOTER;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_ITEM) {
            View row = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_main_recycler,
                    viewGroup, false);
            return new RecipeViewHolder(row);
        } else {
            View row = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.loading_footer,
                    viewGroup, false);
            return new FooterViewHolder(row);
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof RecipeViewHolder) {
            ((RecipeViewHolder) viewHolder).bindRow((Recipe) dataList.get(position));
        } else if (viewHolder instanceof FooterViewHolder) {
            ((FooterViewHolder) viewHolder).progressBar.setIndeterminate(true);
        }
    }

    public void setLoaded() {
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivPhotoRecipe;
        private final TextView tvRating;
        private final TextView tvNameRecipe;
        private final TextView tvNameCreator;
        private final TextView tvDescriptionRecipe;
        private int recipeId;
        private String creatorToken;

        public RecipeViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            ivPhotoRecipe = view.findViewById(R.id.ivPhotoRecipe);
            tvRating = view.findViewById(R.id.tvRating);
            tvNameRecipe = view.findViewById(R.id.tvNameRecipe);
            tvNameCreator = view.findViewById(R.id.tvNameCreator);
            tvDescriptionRecipe = view.findViewById(R.id.tvDescriptionRecipe);

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

    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.footer);
        }
    }

}