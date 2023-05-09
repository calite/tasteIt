package com.example.tasteit_java.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasteit_java.ActivityProfile;
import com.example.tasteit_java.ActivityRecipe;
import com.example.tasteit_java.ApiService.ApiClient;
import com.example.tasteit_java.ApiUtils.UserLoader;
import com.example.tasteit_java.R;
import com.example.tasteit_java.clases.OnLoadMoreListener;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.User;
import com.example.tasteit_java.clases.Utils;
import com.example.tasteit_java.request.UserFollowRequest;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterRecyclerProfileData extends RecyclerView.Adapter {
    private Context context;
    public ArrayList<Object> dataList;
    private int dataType;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private int lastVisibleItem, totalItemCount, firstVisibleItem;
    private int visibleThreshold = 4;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private LifecycleOwner lifecycleOwner;

    public AdapterRecyclerProfileData(Context context, LifecycleOwner lifecycleOwner, RecyclerView recyclerView, int dataType) {
        this.context = context;
        this.dataType = dataType;
        this.lifecycleOwner = lifecycleOwner;
        dataList = new ArrayList<>();

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                private int totalDistanceScrolled = 0;
                private int threshold = 100; // umbral de distancia a recorrer en px
                private boolean isScrollingUp = false;
                @Override
                public void onScrolled(RecyclerView recyclerView,
                                       int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    //Toast.makeText(recyclerView.getContext(), "Distancia: " + totalDistanceScrolled, Toast.LENGTH_SHORT).show();

                    if(recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_SETTLING) {
                        if (dy < 0) {
                            // User is scrolling up
                            isScrollingUp = true;
                        } else if (dy > 0) {
                            // User is scrolling down
                            isScrollingUp = false;
                        }

                        totalDistanceScrolled += dy;

                        if (linearLayoutManager.findFirstVisibleItemPosition() > 0 && dy > 0) {
                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();

                            if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                // End has been reached
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                                totalDistanceScrolled = 0;
                            }
                        } /*else if (isScrollingUp && linearLayoutManager.findFirstVisibleItemPosition() == 0) {
                            // Beginning has been reached
                            if (!loading && totalDistanceScrolled > threshold) {
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.update();
                                }
                                loading = true;
                                totalDistanceScrolled = 0;
                            }
                        }*/
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(dataList.get(position) instanceof Recipe) {
            return TYPE_ITEM;
        } else if (dataList.get(position) instanceof User) {
            return TYPE_ITEM;
        } else {
            return TYPE_FOOTER;
        }
    }

    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_ITEM) {
            View row = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_profile_data,
                    viewGroup, false);
            return new DataViewHolder(row, dataType, context);
        } else {
            View row = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.loading_footer,
                    viewGroup, false);
            return new FooterViewHolder(row);
        }
        //return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof DataViewHolder) {
            ((DataViewHolder) viewHolder).getInformation(dataList.get(position));
        } else if (viewHolder instanceof AdapterEndlessRecyclerMain.FooterViewHolder) {
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
    public long getItemId(int i) {
        return dataList.get(i).hashCode();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class DataViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView ivAuthor;
        TextView tvAuthor, tvComment, tvDateCreated, tvCreator;
        Button btnFollow;
        ChipGroup cgTags;
        ConstraintLayout llComment;
        int dataType;
        Context context;
        Object obj;

        public DataViewHolder(@NonNull View itemView, int dataType, Context context) {
            super(itemView);
            this.dataType = dataType;
            this.context = context;
            ivAuthor = itemView.findViewById(R.id.ivAuthor);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvComment = itemView.findViewById(R.id.tvComment);
            llComment = itemView.findViewById(R.id.llComment);

            switch (this.dataType) {
                case 1:
                case 4: { //Recipes & Liked
                    tvDateCreated = itemView.findViewById(R.id.tvDateCreated);
                    tvDateCreated.setEnabled(true);
                    tvDateCreated.setVisibility(View.VISIBLE);

                    cgTags = itemView.findViewById(R.id.cgTags);
                    cgTags.setEnabled(true);
                    cgTags.setVisibility(View.VISIBLE);

                    tvCreator = itemView.findViewById(R.id.tvCreator);
                    tvCreator.setEnabled(true);
                    tvCreator.setVisibility(View.VISIBLE);
                    break;
                }
                case 2:
                case 3: { //Followers & Followings
                    btnFollow = itemView.findViewById(R.id.btnFollow);
                    btnFollow.setEnabled(true);
                    btnFollow.setVisibility(View.VISIBLE);

                    break;
                }
            }

        }

        void bindRow(@NonNull Object data, Boolean isFollow) {
            switch (dataType) {
                case 1:
                case 4: { //Recipes & Liked
                    cgTags.removeAllViews();

                    Recipe temp = ((Recipe) data);

                    tvAuthor.setText(temp.getName());
                    tvComment.setText(temp.getDescription());
                    tvDateCreated.setText(temp.getDateCreated());
                    tvCreator.setText(temp.getCreator());

                    cgTags.setChipSpacingHorizontal(8);

                    for (String s : temp.getTags()) {
                        Chip chip = new Chip(context);
                        chip.setText(s);
                        chip.setChipBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.maroon)));
                        chip.setTextColor(Color.WHITE);
                        cgTags.addView(chip);
                    }

                    llComment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, ActivityRecipe.class);
                            intent.putExtra("recipeId", ((Recipe) data).getId());
                            intent.putExtra("creatorToken", ((Recipe) data).getCreatorToken());
                            context.startActivity(intent);
                        }
                    });

                    ivAuthor.setShapeAppearanceModel(ShapeAppearanceModel.builder(context, R.style.cornerRoundImageView, R.style.cornerRoundImageView).build());
                    try{
                        //Picasso.with(itemView.getContext()).load(temp.getImage()).into(ivAuthor);
                    }catch(IllegalArgumentException iae){}
                    //ivAuthor.setImageBitmap(Utils.decodeBase64(temp.getImage()));
                    break;
                }
                case 2:
                case 3: { //Followers & Followings
                    User temp = ((User) data);

                    String token = Utils.getUserToken();

                    tvAuthor.setText(temp.getUsername());
                    tvComment.setText(temp.getBiography());
                    try{
                        Picasso.with(itemView.getContext()).load(temp.getImgProfile()).into(ivAuthor);
                    }catch(IllegalArgumentException iae){}
                    //ivAuthor.setImageBitmap(Utils.decodeBase64(temp.getImgProfile()));

                    if (isFollow) {
                        btnFollow.setText("UNFOLLOW");
                    } else if (token.equals(temp.getUid())) {
                        btnFollow.setVisibility(View.INVISIBLE);
                        btnFollow.setEnabled(false);
                    } else {
                        btnFollow.setText("FOLLOW");
                    }

                    btnFollow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            UserFollowRequest userFollowRequest = new UserFollowRequest(Utils.getUserToken(), temp.getUid());
                            ApiClient apiClient = ApiClient.getInstance(Utils.getUserAcessToken());
                            Call<Void> call = apiClient.getService().followUser(userFollowRequest);
                            call.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        getInformation(temp);
                                    } else {
                                        // Handle the error
                                        Log.e("API_ERROR", "Response error: " + response.code() + " " + response.message());
                                        Toast.makeText(context, "bad!", Toast.LENGTH_SHORT).show();

                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    // Handle the error
                                    Toast.makeText(context, "bad!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                    llComment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, ActivityProfile.class);
                            intent.putExtra("uid", temp.getUid());
                            context.startActivity(intent);
                        }
                    });
                    break;
                }
            }
        }

        private void getInformation(@NonNull Object data) {
            obj = data;
            if(obj instanceof User) {
                User temp = (User) obj;
                String token = Utils.getUserToken();
                UserLoader isFollowing = new UserLoader(ApiClient.getInstance(Utils.getUserAcessToken()).getService(), context, token, temp.getUid());
                isFollowing.getIsFollow().observe(lifecycleOwner, this::onFollowingLoaded);
                isFollowing.loadIsFollow();
            } else if (obj instanceof Recipe) {
                bindRow(obj, false);
            }
        }

        private void onFollowingLoaded(Boolean isFollow) {
            bindRow(obj, isFollow);
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
