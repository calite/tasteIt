package com.example.tasteit_java.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasteit_java.ActivityRecipe;
import com.example.tasteit_java.R;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.OnLoadMoreListener;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterRecyclerPhotosProfile extends RecyclerView.Adapter {

    private Context context;
    public ArrayList<Object> dataList;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private int visibleThreshold = 3;
    private int lastVisibleItem, totalItemCount, firstVisibleItem;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    public AdapterRecyclerPhotosProfile(Context context, RecyclerView recyclerView) {
        this.context = context;
        dataList = new ArrayList<>();

        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {

            final GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                private int totalDistanceScrolled = 0;
                private int threshold = 200; // umbral de distancia a recorrer en px
                @Override
                public void onScrolled(RecyclerView recyclerView,
                                       int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    if(recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_SETTLING) {
                        totalDistanceScrolled += dy;

                        if (gridLayoutManager.findFirstVisibleItemPosition() > 0 && dy > 0 && getItemCount() > 9) {
                            totalItemCount = gridLayoutManager.getItemCount();
                            lastVisibleItem = gridLayoutManager.findLastCompletelyVisibleItemPosition();

                            if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                // End has been reached
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                                totalDistanceScrolled = 0;
                            }
                        } else if (dy < 0 && gridLayoutManager.findFirstVisibleItemPosition() == 0) {
                            // Beginning has been reached
                            if (!loading && totalDistanceScrolled > threshold) {
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.update();
                                }
                                loading = true;
                                totalDistanceScrolled = 0;
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position) instanceof Recipe ? TYPE_ITEM : TYPE_FOOTER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_ITEM) {
            View row = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_card_profile_rec,
                    viewGroup, false);
            return new RecipeViewHolder(row);
        } else {
            View row = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.loading_footer,
                    viewGroup, false);
            return new FooterViewHolder(row);
        }
        //return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof RecipeViewHolder) {
            ((RecipeViewHolder) viewHolder).bindRow((Recipe) dataList.get(position));
        } else if (viewHolder instanceof FooterViewHolder) {
            ((FooterViewHolder) viewHolder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public long getItemId(int i) {
        return dataList.get(i).hashCode();
    }

    public void setLoaded() {
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivPhoto;
        private int recipeId;
        private String creatorToken;

        public RecipeViewHolder(@NonNull View itemView) {
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
            try{
                Picasso.with(itemView.getContext()).load(recipe.getImage()).into(ivPhoto);
            }catch(IllegalArgumentException iae){}

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
