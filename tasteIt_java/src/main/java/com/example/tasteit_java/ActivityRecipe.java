package com.example.tasteit_java;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.Utils;
import com.google.android.material.tabs.TabLayout;

public class ActivityRecipe extends AppCompatActivity {

    private ImageView ivRecipePhoto;
    private TextView tvRecipeName;
    private RatingBar rbRating;
    private TextView tvNameCreator;

    private TabLayout tlRecipe;
    private ViewPager2 vpPaginator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        //recogemos la receta pasada como parametro
        Bundle params = getIntent().getExtras();
        Recipe recipe = (Recipe) params.getSerializable("recipe");

        //menu superior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ivRecipePhoto = findViewById(R.id.ivRecipePhoto);
        tvRecipeName = findViewById(R.id.tvRecipeName);
        rbRating = findViewById(R.id.rbRating);
        tvNameCreator = findViewById(R.id.tvNameCreator);

        Bitmap bitmap = Utils.decodeBase64(recipe.getImage());
        ivRecipePhoto.setImageBitmap(bitmap);
        tvRecipeName.setText(recipe.getName());
        tvNameCreator.setText(recipe.getCreator());

        tlRecipe = findViewById(R.id.tlRecipe);
        vpPaginator = findViewById(R.id.vpPaginator);

        vpPaginator.setAdapter(new AdapterFragmentRecipe(getSupportFragmentManager(),getLifecycle(), recipe));
        tlRecipe.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vpPaginator.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        vpPaginator.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                //super.onPageSelected(position);
                tlRecipe.selectTab(tlRecipe.getTabAt(position));
            }
        });



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