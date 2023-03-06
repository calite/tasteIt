package com.example.tasteit_java;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tasteit_java.R;
import com.example.tasteit_java.clases.Recipe;

import java.util.ArrayList;

public class AdapterListViewNewRecipe extends BaseAdapter {

    private Context context;
    private ArrayList<String> arrayListSteps;

    public AdapterListViewNewRecipe(Context context, ArrayList<String> arrayListSteps) {
        this.context = context;
        this.arrayListSteps = arrayListSteps;
    }

    @Override
    public int getCount() {
        return arrayListSteps.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayListSteps.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(R.layout.item_recipe_listview, null);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        EditText etText = view.findViewById(R.id.etText);

        tvTitle.setText("Step " + (i + 1) );
        etText.setText(arrayListSteps.get(i));

        return view;
    }
}
