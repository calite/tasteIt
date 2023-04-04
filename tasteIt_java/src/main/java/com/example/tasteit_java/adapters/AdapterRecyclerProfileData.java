package com.example.tasteit_java.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasteit_java.R;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.User;
import com.example.tasteit_java.clases.Utils;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class AdapterRecyclerProfileData extends RecyclerView.Adapter<AdapterRecyclerProfileData.ViewHolder> {

    private Context context;
    private String uidProfile;
    private int dataType;
    private ArrayList<Object> data;

    public AdapterRecyclerProfileData(Context context, String uid, int dataType) {
        this.context = context;
        this.uidProfile = uid;
        this.dataType = dataType;
        data = new ArrayList<>();

        new TaskLoadUser().execute();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAuthor;
        TextView tvAuthor, tvComment, tvDateCreated, symbol;
        Button btnFollow;
        ChipGroup cgTags;
        LinearLayout llComment;

        public ViewHolder(@NonNull View itemView, int dataType) {
            super(itemView);
            ivAuthor = itemView.findViewById(R.id.ivAuthor);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvComment = itemView.findViewById(R.id.tvComment);
            llComment = itemView.findViewById(R.id.llComment);

            switch (dataType) {
                case 1:
                case 4: { //Recipes & Likes
                    tvDateCreated = itemView.findViewById(R.id.tvDateCreated);
                    tvDateCreated.setEnabled(true);
                    tvDateCreated.setVisibility(View.VISIBLE);

                    cgTags = itemView.findViewById(R.id.cgTags);
                    cgTags.setEnabled(true);
                    cgTags.setVisibility(View.VISIBLE);
                    break;
                }
                case 2:
                case 3: { //Followers & FollowingsL
                    btnFollow = itemView.findViewById(R.id.btnFollow);
                    btnFollow.setEnabled(true);
                    btnFollow.setVisibility(View.VISIBLE);

                    symbol = itemView.findViewById(R.id.symbol);
                    symbol.setEnabled(true);
                    symbol.setVisibility(View.VISIBLE);

                    break;
                }
            }

        }
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_profile_data, null, false);
        return new ViewHolder(view, dataType);
        //return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        switch (dataType) {
            case 1:
            case 4: { //Recipes & Likes
                Recipe temp = ((Recipe) data.get(position));

                holder.tvAuthor.setText(temp.getName());
                holder.tvComment.setText(temp.getDescription());
                holder.tvDateCreated.setText(temp.getDateCreated());

                holder.cgTags.setChipSpacingHorizontal(8);

                for (String s : temp.getTags()) {
                    Chip chip = new Chip(context);
                    chip.setText(s);
                    chip.setChipBackgroundColor(ColorStateList.valueOf(context.getResources().getColor(R.color.maroon)));
                    chip.setTextColor(Color.WHITE);
                    holder.cgTags.addView(chip);
                }
                holder.ivAuthor.setImageBitmap(Utils.decodeBase64(temp.getImage()));
                break;
            }
            case 2:
            case 3: { //Followers & Followings
                User temp = ((User) data.get(position));

                holder.tvAuthor.setText(temp.getUsername());
                holder.tvComment.setText(temp.getBiography());
                holder.ivAuthor.setImageBitmap(Utils.decodeBase64(temp.getImgProfile()));

                if (new BdConnection().isFollowing(FirebaseAuth.getInstance().getCurrentUser().getUid(), temp.getUid())) {
                    holder.btnFollow.setText("UNFOLLOW");
                }

                holder.btnFollow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (new BdConnection().isFollowing(FirebaseAuth.getInstance().getCurrentUser().getUid(), temp.getUid())) {
                            new BdConnection().unFollowUser(FirebaseAuth.getInstance().getCurrentUser().getUid(), temp.getUid());
                            holder.btnFollow.setText("FOLLOW");
                        } else {
                            new BdConnection().followUser(FirebaseAuth.getInstance().getCurrentUser().getUid(), temp.getUid());
                            holder.btnFollow.setText("UNFOLLOW");
                        }
                    }
                });
                break;
            }
        }
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

                    break;
                }
            }
            return supp;
        }

        @Override
        protected void onPostExecute(ArrayList<Object> dataCol) {
            //super.onPostExecute(recipes);
            data.addAll(dataCol);
            notifyDataSetChanged();
        }
    }
}
