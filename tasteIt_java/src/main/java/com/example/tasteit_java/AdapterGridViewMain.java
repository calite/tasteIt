package com.example.tasteit_java;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.tasteit_java.R;
import com.example.tasteit_java.clases.Recipe;

import java.util.ArrayList;

public class AdapterGridViewMain extends BaseAdapter {

    private Context context;
    private ArrayList<Recipe> arrayListRecipes;

    public AdapterGridViewMain(Context context, ArrayList<Recipe> arrayListRecipes) {
        this.context = context;
        this.arrayListRecipes = arrayListRecipes;
    }

    @Override
    public int getCount() {
        return arrayListRecipes.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayListRecipes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.item_main_gridview, null);

        ImageView ivPhoto = view.findViewById(R.id.ivPhotoRecipe);

        ivPhoto.setImageResource(arrayListRecipes.get(i).getImg());

        return view;
    }
}
