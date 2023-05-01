package com.example.tasteit_java.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasteit_java.ActivityProfile;
import com.example.tasteit_java.R;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.Comment;
import com.example.tasteit_java.clases.User;
import com.example.tasteit_java.clases.Utils;
import com.example.tasteit_java.fragments.FragmentComments;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;

public class AdapterRecyclerCommentsRecipe extends RecyclerView.Adapter<AdapterRecyclerCommentsRecipe.ViewHolder> {

    private Context context;
    private int recipeId;
    private ShimmerFrameLayout shimmer;
    private ArrayList<Comment> comments;

    public AdapterRecyclerCommentsRecipe(Context context, int recipeId, ShimmerFrameLayout shimmer) {
        this.context = context;
        this.recipeId = recipeId;
        comments = new ArrayList<>();
        this.shimmer = shimmer;

        new TaskLoadRecipeComments().execute();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUserPicture;
        TextView tvAuthor, tvComment, tvDateCreated, tvRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserPicture = itemView.findViewById(R.id.ivUserPicture);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvDateCreated = itemView.findViewById(R.id.tvDateCreated);
        }
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recipe_comment, null, false);
        return new ViewHolder(view);
        //return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int pos = position;
        User user = new BdConnection().retrieveAllUserbyUid(comments.get(position).getTokenUser());
        String token = Utils.getUserToken();

        /*PopupMenu.OnMenuItemClickListener popupListener = new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.iEditComment: {
                        FragmentComments.editComment(comments.get(pos).getId(), comments.get(pos).getComment());
                        break;
                    }
                    case R.id.iReportComment: {
                        Toast.makeText(context, "Esta opcion pronto estara disponible!", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case R.id.iRemoveComment: {
                        new BdConnection().removeComment(comments.get(pos).getId());
                        break;
                    }
                }
                updateComments();
                return false;
            }
        };

        if(comments.get(position).getTokenUser().equals(token) & !myProfile) {
            holder.ibtnOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(context, view);
                    MenuInflater menuInflater = popupMenu.getMenuInflater();
                    menuInflater.inflate(R.menu.profile_comments_menu, popupMenu.getMenu());
                    popupMenu.getMenu().getItem(2).setVisible(false);
                    popupMenu.setOnMenuItemClickListener(popupListener);
                    popupMenu.show();
                }
            });
        } else {
            holder.ibtnOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(context, view);
                    MenuInflater menuInflater = popupMenu.getMenuInflater();
                    menuInflater.inflate(R.menu.profile_comments_menu, popupMenu.getMenu());
                    popupMenu.getMenu().getItem(0).setVisible(false);
                    popupMenu.setOnMenuItemClickListener(popupListener);
                    popupMenu.show();
                }
            });
        }*/

        holder.tvAuthor.setText(user.getUsername());
        holder.tvComment.setText(comments.get(position).getComment());
        holder.tvDateCreated.setText(comments.get(position).getDateCreated());

        Bitmap bitmap = Utils.decodeBase64(user.getImgProfile());
        holder.ivUserPicture.setImageBitmap(bitmap);

        View.OnClickListener listenerProfile = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ActivityProfile.class);
                intent.putExtra("uid", comments.get(pos).getTokenUser());
                context.startActivity(intent);
            }
        };

        holder.ivUserPicture.setOnClickListener(listenerProfile);
        holder.tvComment.setOnClickListener(listenerProfile);
        holder.tvAuthor.setOnClickListener(listenerProfile);
    }

    @Override
    public long getItemId(int i) {
        return comments.get(i).hashCode();
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void updateComments() {
        new TaskLoadRecipeComments().execute();
        notifyDataSetChanged();
    }

    class TaskLoadRecipeComments extends AsyncTask<ArrayList<Comment>, Void,ArrayList<Comment>> {
        @Override
        protected void onPreExecute() {

        }
        @Override
        protected ArrayList<Comment> doInBackground(ArrayList<Comment>... hashMaps) {
            return new BdConnection().getCommentsOnRecipe(recipeId);
        }
        @Override
        protected void onPostExecute(ArrayList<Comment> recipeComments) {
            //super.onPostExecute(recipes);
            comments = new ArrayList<>(recipeComments);
            notifyDataSetChanged();
            shimmer.stopShimmer();
            shimmer.setVisibility(View.GONE);
        }
    }

}
