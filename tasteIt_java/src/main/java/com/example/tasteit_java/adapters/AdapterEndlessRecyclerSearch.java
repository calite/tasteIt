package com.example.tasteit_java.adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.tasteit_java.ActivityProfile;
import com.example.tasteit_java.ActivityRecipe;
import com.example.tasteit_java.R;
import com.example.tasteit_java.clases.OnLoadMoreListener;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.User;
import com.example.tasteit_java.clases.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class AdapterEndlessRecyclerSearch extends Adapter {

    public ArrayList<Object> dataList;
    private static final int TYPE_ITEM_RECIPE = 0;
    private static final int TYPE_ITEM_USER = 1;
    private static final int TYPE_FOOTER = 2;
    private int visibleThreshold = 4;
    private int lastVisibleItem, totalItemCount, firstVisibleItem;
    private RecyclerView recyclerView;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    public AdapterEndlessRecyclerSearch(RecyclerView recyclerView) {
        dataList = new ArrayList<>();
        this.recyclerView = recyclerView;

        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {

            final GridLayoutManager gridLayoutManager = (GridLayoutManager) recyclerView.getLayoutManager();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                private int totalDistanceScrolled = 0;
                private int threshold = 50; // umbral de distancia a recorrer en px
                @Override
                public void onScrolled(RecyclerView recyclerView,
                                       int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    if(recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_SETTLING) {
                        totalDistanceScrolled += dy;

                        if (gridLayoutManager.findFirstVisibleItemPosition() > 0 && dy > 0) {
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
        if(dataList.get(position) instanceof Recipe) {
            return TYPE_ITEM_RECIPE;
        } else if (dataList.get(position) instanceof User) {
            return TYPE_ITEM_USER;
        } else {
            return TYPE_FOOTER;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_ITEM_RECIPE) {
            View row = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_card_search_recipe,
                    viewGroup, false);
            return new RecipeViewHolder(row);
        } else if (viewType == TYPE_ITEM_USER) {
            View row = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_card_search_user,
                    viewGroup, false);
            return new UserViewHolder(row);
        } else {
            View row = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.loading_footer,
                    viewGroup, false);
            return new FooterViewHolder(row);
        }
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof UserViewHolder) {
            ((UserViewHolder) viewHolder).bindRow((User) dataList.get(position));
        } else if (viewHolder instanceof RecipeViewHolder) {
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

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class RecipeViewHolder extends ViewHolder {

        private final ImageView ivPhotoRecipe;
        private final TextView tvDifficulty;
        private final TextView tvNameRecipe;
        private int recipeId;

        public RecipeViewHolder(View view) {
            super(view);
            ivPhotoRecipe = view.findViewById(R.id.ivPhotoRecipe);
            tvDifficulty = view.findViewById(R.id.tvDifficulty);
            tvNameRecipe = view.findViewById(R.id.tvNameRecipe);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(view.getContext(), ActivityRecipe.class);
                    i.putExtra("recipeId", recipeId);
                    view.getContext().startActivity(i);
                }
            });
        }

        void bindRow(@NonNull Recipe recipe) {
            tvNameRecipe.setText(recipe.getName());
            tvDifficulty.setText(String.valueOf(recipe.getDifficulty()));
            try{
                Picasso.with(itemView.getContext()).load(recipe.getImage()).into(ivPhotoRecipe);
            }catch(IllegalArgumentException iae){}
            recipeId = recipe.getId();
        }
    }

    class UserViewHolder extends ViewHolder {
        private final ImageView ivPhotoUser;
        private final TextView tvNameUser;
        private String token;

        public UserViewHolder(View view) {
            super(view);
            ivPhotoUser = view.findViewById(R.id.ivPhotoUser);
            tvNameUser = view.findViewById(R.id.tvNameUser);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(view.getContext(), ActivityProfile.class);
                    i.putExtra("uid", token);
                    view.getContext().startActivity(i);
                }
            });
        }

        void bindRow(@NonNull User user) {
            tvNameUser.setText(user.getUsername());
            try{
                Picasso.with(recyclerView.getContext()).load(user.getImgProfile()).into(ivPhotoUser);
            }catch(IllegalArgumentException iae){}
            token = user.getUid();
        }
    }

    class FooterViewHolder extends ViewHolder {

        ProgressBar progressBar;

        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.footer);
        }
    }

}