package com.example.tasteit_java.adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tasteit_java.R;

import java.util.ArrayList;

public class AdapterListViewSteps extends BaseAdapter {
    private Context context;
    public ArrayList<String> arrayListSteps;

    public AdapterListViewSteps(Context context, ArrayList<String> arrayListSteps) {
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
        view = inflater.inflate(R.layout.item_steps_recipe, null);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvText = view.findViewById(R.id.tvText);

        tvText.setText(arrayListSteps.get(i));
        tvTitle.setText(R.string.step + (i + 1) );

        return view;
    }

}
