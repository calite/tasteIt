package com.example.tasteit_java;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityRecipe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        //menu superior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //MENU superior
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //ojo que puede petar
                //NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;
            case R.id.iRate:

                View rate = View.inflate(ActivityRecipe.this,R.layout.item_rate,null);
                RatingBar rbRating = rate.findViewById(R.id.rbRating);
                EditText etComment = rate.findViewById(R.id.etCommentRate);

                AlertDialog.Builder builderRate = new AlertDialog.Builder(ActivityRecipe.this);
                builderRate.setView(rate);
                builderRate.setPositiveButton("Send!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //aqui hacemos cositas
                        Toast.makeText(ActivityRecipe.this, rbRating.getRating() + " " + etComment.getText(), Toast.LENGTH_SHORT).show();
                    }
                });
                builderRate.setNegativeButton("Cancel",null);
                builderRate.create().show();
                return true;
            case R.id.iReport:

                View report = View.inflate(ActivityRecipe.this,R.layout.item_report,null);
                EditText etCommentReport = report.findViewById(R.id.etCommentReport);
                AlertDialog.Builder builderReport = new AlertDialog.Builder(ActivityRecipe.this);
                builderReport.setView(report);
                builderReport.setPositiveButton("Send!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //aqui hacemos cositas
                        Toast.makeText(ActivityRecipe.this, etCommentReport.getText(), Toast.LENGTH_SHORT).show();
                    }
                });
                builderReport.setNegativeButton("Cancel",null);
                builderReport.create().show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}