package com.example.tasteit_java.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.tasteit_java.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.TreeMap;

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

        view = inflater.inflate(R.layout.item_recipe_stepslist, null);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyStepAlertDialog(i);
                notifyDataSetChanged();
            }
        });

        ImageButton btnDelete = view.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteStepAlertDialog(i);
                notifyDataSetChanged();
            }
        });

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        TextView tvText = view.findViewById(R.id.tvText);

        tvText.setText(arrayListSteps.get(i));
        tvTitle.setText("Step " + (i + 1) );

        tvText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyStepAlertDialog(i);
                notifyDataSetChanged();
            }
        });

        return view;
    }

    private void modifyStepAlertDialog(int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Modify Step: ");
        EditText etStep = new EditText(context);
        builder.setView(etStep);
        etStep.setText(arrayListSteps.get(pos));
        builder.setPositiveButton("Done!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String step = etStep.getText().toString();
                if(!step.trim().equals("")) {
                    arrayListSteps.set(pos, step);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Step successfully modified", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "You must indicate a step", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void deleteStepAlertDialog(int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Remove step?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                arrayListSteps.remove(pos);
                notifyDataSetChanged();
                Toast.makeText(context, "Step removed", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

}
