package com.example.tasteit_java.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tasteit_java.R;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.User;
import com.example.tasteit_java.clases.Utils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class AdapterFragmentComments extends BaseAdapter {

    private Context context;
    private String uidProfile;
    private ArrayList<String> uidsComments;
    private ArrayList<String> comments;

    public AdapterFragmentComments(Context context, String uid) {
        this.context = context;
        this.uidProfile = uid;

        uidsComments = new ArrayList<>();
        comments = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int i) {
        return comments.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.item_profile_comments, null);

        ImageView ivAuthor = view.findViewById(R.id.ivAuthor);
        TextView tvAuthor = view.findViewById(R.id.tvAuthor);
        TextView tvComment = view.findViewById(R.id.tvComment);
        LinearLayout llComment = view.findViewById(R.id.llComment);

        User user = new BdConnection().retrieveUserbyUid(uidsComments.get(i));

        if(uidsComments.get(i).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            llComment.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Toast.makeText(context, "Este comentario es tuyo y pronto lo podras editar!", Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }

        tvAuthor.setText(user.getUsername());
        tvComment.setText(comments.get(i));

        Bitmap bitmap = Utils.decodeBase64(user.getImgProfile());
        ivAuthor.setImageBitmap(bitmap);

        return view;
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
