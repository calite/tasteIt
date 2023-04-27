package com.example.tasteit_java.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasteit_java.ActivityProfile;
import com.example.tasteit_java.ActivityRecipe;
import com.example.tasteit_java.ApiService.ApiClient;
import com.example.tasteit_java.ApiService.ApiRequests;
import com.example.tasteit_java.ApiService.RecipeId_Recipe_User;
import com.example.tasteit_java.R;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.User;
import com.example.tasteit_java.clases.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterRecyclerProfileData extends RecyclerView.Adapter<AdapterRecyclerProfileData.ViewHolder> {

    private ShimmerFrameLayout shimmer;
    private Context context;
    private String uidProfile;
    private int dataType;
    private ArrayList<Object> data;
    private LifecycleOwner lifecycleOwner;

    public AdapterRecyclerProfileData(Context context, String uid, int dataType, LifecycleOwner lifecycleOwner, ShimmerFrameLayout shimmer) {
        this.context = context;
        this.uidProfile = uid;
        this.dataType = dataType;
        data = new ArrayList<>();
        this.shimmer = shimmer;
        this.lifecycleOwner = lifecycleOwner;

        new TaskLoadUser().execute();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView ivAuthor;
        TextView tvAuthor, tvComment, tvDateCreated, tvCreator;
        Button btnFollow;
        ChipGroup cgTags;
        ConstraintLayout llComment;
        int dataType;
        Context context;

        public ViewHolder(@NonNull View itemView, int dataType, Context context) {
            super(itemView);
            this.dataType = dataType;
            this.context = context;
            ivAuthor = itemView.findViewById(R.id.ivAuthor);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvComment = itemView.findViewById(R.id.tvComment);
            llComment = itemView.findViewById(R.id.llComment);

            switch (dataType) {
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

        void bindRow(@NonNull Object data) {
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
                    ivAuthor.setImageBitmap(Utils.decodeBase64(temp.getImage()));
                    break;
                }
                case 2:
                case 3: { //Followers & Followings
                    User temp = ((User) data);

                    String token = Utils.getUserToken();

                    tvAuthor.setText(temp.getUsername());
                    tvComment.setText(temp.getBiography());
                    ivAuthor.setImageBitmap(Utils.decodeBase64(temp.getImgProfile()));

                    if (new BdConnection().isFollowing(token, temp.getUid())) {
                        btnFollow.setText("UNFOLLOW");
                    } else if (token.equals(temp.getUid())) {
                        btnFollow.setVisibility(View.INVISIBLE);
                        btnFollow.setEnabled(false);
                    }

                    btnFollow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (new BdConnection().isFollowing(token, temp.getUid())) {
                                new BdConnection().unFollowUser(token, temp.getUid());
                                btnFollow.setText("FOLLOW");
                            } else {
                                new BdConnection().followUser(token, temp.getUid());
                                btnFollow.setText("UNFOLLOW");
                            }
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
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_profile_data, null, false);
        return new ViewHolder(view, dataType, context);
        //return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Object temp = data.get(position);
        holder.bindRow(temp);
    }

    @Override
    public long getItemId(int i) {
        return data.get(i).hashCode();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class TaskLoadUser extends AsyncTask<ArrayList<Object>, Void, ArrayList<Object>> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected ArrayList<Object> doInBackground(ArrayList<Object>... hashMaps) {
            ArrayList<Object> supp = new ArrayList<>();
            switch (dataType) {
                case 1: { //Recipes
                    supp.addAll(new BdConnection().retrieveAllRecipesbyUid(uidProfile));
                    break;
                }
                case 2: { //Followers
                    supp.addAll(new BdConnection().retrieveFollowersByUid(uidProfile));
                    break;
                }
                case 3: { //Followings
                    supp.addAll(new BdConnection().retrieveFollowingByUid(uidProfile));
                    break;
                }
                case 4: { //Recipes Liked
                    supp.addAll(new BdConnection().recipesLiked(uidProfile));
                    break;
                }
            }
            return supp;
        }

        @Override
        protected void onPostExecute(ArrayList<Object> dataCol) {
            //super.onPostExecute(recipes);
            data.addAll(dataCol);
            shimmer.stopShimmer();
            shimmer.setVisibility(View.GONE);
            notifyDataSetChanged();
        }
    }

}
