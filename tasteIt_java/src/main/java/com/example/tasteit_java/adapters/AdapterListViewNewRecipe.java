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
        tvTitle.setText(context.getString(R.string.step) + (i + 1));

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
        View rate = View.inflate(context, R.layout.alert_dialog_modifysteps, null);
        EditText etStep = rate.findViewById(R.id.etStep);
        Button btnAccept = rate.findViewById(R.id.btnAccept);
        Button btnCancel = rate.findViewById(R.id.btnCancel);

        etStep.setText(arrayListSteps.get(pos));

        Dialog dialog = new Dialog(context);
        dialog.setContentView(rate);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
        window.setWindowAnimations(Animation.INFINITE);

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String step = etStep.getText().toString();
                if(!step.trim().equals("")) {
                    arrayListSteps.set(pos, step);
                    notifyDataSetChanged();
                    Toast.makeText(context, R.string.step_succ_mod, Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                } else {
                    Toast.makeText(context, R.string.error_step, Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void deleteStepAlertDialog(int pos) {
        View rate = View.inflate(context, R.layout.alert_dialog_deletesteps, null);
        TextView tvStep = rate.findViewById(R.id.tvStep);
        Button btnAccept = rate.findViewById(R.id.btnAccept);
        Button btnCancel = rate.findViewById(R.id.btnCancel);

        tvStep.setText(arrayListSteps.get(pos));

        Dialog dialog = new Dialog(context);
        dialog.setContentView(rate);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);
        window.setWindowAnimations(Animation.INFINITE);

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayListSteps.remove(pos);
                notifyDataSetChanged();
                Toast.makeText(context, R.string.step_removed, Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

}
