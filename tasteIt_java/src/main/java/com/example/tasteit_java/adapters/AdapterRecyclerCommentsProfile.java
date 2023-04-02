package com.example.tasteit_java.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasteit_java.R;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.User;
import com.example.tasteit_java.clases.Utils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class AdapterRecyclerCommentsProfile extends RecyclerView.Adapter<AdapterRecyclerCommentsProfile.ViewHolder> {

    private Context context;
    private String uidProfile;
    private ArrayList<String> uidsComments;
    private ArrayList<String> comments;

    public AdapterRecyclerCommentsProfile(Context context, String uid) {
        this.context = context;
        this.uidProfile = uid;

        uidsComments = new ArrayList<>();
        comments = new ArrayList<>();

        new TaskLoadUserComments().execute();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAuthor;
        TextView tvAuthor;
        TextView tvComment;
        LinearLayout llComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAuthor = itemView.findViewById(R.id.ivAuthor);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvComment = itemView.findViewById(R.id.tvComment);
            llComment = itemView.findViewById(R.id.llComment);
        }
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_profile_comments, null, false);
        return new ViewHolder(view);
        //return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = new BdConnection().retrieveUserbyUid(uidsComments.get(position));

        if(uidsComments.get(position).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            /*llComment.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Toast.makeText(context, "Este comentario es tuyo y pronto lo podras editar!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });*/
        }

        holder.tvAuthor.setText(user.getUsername());
        holder.tvComment.setText(comments.get(position));

        Bitmap bitmap = Utils.decodeBase64(user.getImgProfile());
        holder.ivAuthor.setImageBitmap(bitmap);
    }

    @Override
    public long getItemId(int i) {
        return uidsComments.get(i).hashCode();
    }

    @Override
    public int getItemCount() {
        return uidsComments.size();
    }

    public void updateComments() {
        new TaskLoadUserComments().execute();
        notifyDataSetChanged();
    }

    class TaskLoadUserComments extends AsyncTask<HashMap<String, String>, Void,HashMap<String, String>> {
        @Override
        protected void onPreExecute() {

        }
        @Override
        protected HashMap<String, String> doInBackground(HashMap<String, String>... hashMaps) {
            return new BdConnection().retrieveCommentsbyUid(uidProfile);
        }
        @Override
        protected void onPostExecute(HashMap<String, String> userComments) {
            //super.onPostExecute(recipes);
            Set<String> keySet = userComments.keySet();
            uidsComments.addAll(keySet);

            Collection<String> values = userComments.values();
            comments.addAll(values);
            notifyDataSetChanged();
        }
    }
}
