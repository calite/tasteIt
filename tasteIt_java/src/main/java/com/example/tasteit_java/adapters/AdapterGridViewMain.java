package com.example.tasteit_java.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tasteit_java.R;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.Utils;

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

        view = inflater.inflate(R.layout.item_card_recipes, null);

        ImageView ivPhoto = view.findViewById(R.id.ivPhotoRecipe);
        TextView tvNameRecipe = view.findViewById(R.id.tvNameRecipe);
        TextView tvNameCreator = view.findViewById(R.id.tvNameCreator);
        TextView tvDescription = view.findViewById(R.id.tvDescriptionRecipe);

        Bitmap bitmap = Utils.decodeBase64(arrayListRecipes.get(i).getImage());
        ivPhoto.setImageBitmap(bitmap);
        tvNameRecipe.setText(arrayListRecipes.get(i).getName());
        tvNameCreator.setText(arrayListRecipes.get(i).getCreator());
        tvDescription.setText(arrayListRecipes.get(i).getDescription());

        return view;
    }

}
