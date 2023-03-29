package com.example.tasteit_java.adapters;

import com.example.tasteit_java.bdConnection.BdConnection;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tasteit_java.R;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.Comment;
import com.example.tasteit_java.clases.User;
import com.example.tasteit_java.clases.Utils;

import java.util.ArrayList;

public class AdapterListViewComments extends BaseAdapter {

    private Context context;
    private ArrayList<Comment> arrayListComments;
    private BdConnection connection;

    public AdapterListViewComments(Context context, ArrayList<Comment> arrayListComments) {
        this.context = context;
        this.arrayListComments = arrayListComments;
        connection = new BdConnection();
    }

    @Override
    public int getCount() {
        return arrayListComments.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayListComments.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.item_recipe_comment, null);

        TextView tvComment = view.findViewById(R.id.tvComment);
        TextView tvDateCreated = view.findViewById(R.id.tvDateCreated);
        TextView tvRating = view.findViewById(R.id.tvRating);
        TextView tvUserName = view.findViewById(R.id.tvUserName);
        ImageView ivUserPicture = view.findViewById(R.id.ivUserPicture);

        User user = new BdConnection().retrieveUserbyUid(arrayListComments.get(i).getTokenUser());

        tvComment.setText(arrayListComments.get(i).getComment());
        tvDateCreated.setText(arrayListComments.get(i).getDateCreated());
        tvRating.setText(arrayListComments.get(i).getRating() + "");
        tvUserName.setText(user.getUsername());
        Bitmap bitmap = Utils.decodeBase64(user.getImgProfile());
        ivUserPicture.setImageBitmap(bitmap);

        return view;
    }

}