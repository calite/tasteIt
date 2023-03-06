package com.example.tasteit_java;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.tasteit_java.clases.Recipe;

import java.util.ArrayList;

public class AdapterGridViewProfile extends BaseAdapter {

    private Context context;
    private ArrayList<Recipe> arrayListPhotos;

    public AdapterGridViewProfile(Context context, ArrayList<Recipe> arrayListRecipes) {
        this.context = context;
        this.arrayListPhotos = arrayListRecipes;
    }

    @Override
    public int getCount() {
        return arrayListPhotos.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayListPhotos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.item_profile_gridview, null);

        ImageView ivPhoto = view.findViewById(R.id.ivPhoto);

        ivPhoto.setImageResource(arrayListPhotos.get(i).getImg());

        return view;
    }

}
