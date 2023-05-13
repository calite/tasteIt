package com.example.tasteit_java;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tasteit_java.ApiService.ApiClient;
import com.example.tasteit_java.ApiUtils.RecipeLoader;
import com.example.tasteit_java.adapters.AdapterFragmentProfile;
import com.example.tasteit_java.adapters.AdapterFragmentRecipe;
import com.example.tasteit_java.clases.OnItemNavSelectedListener;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.Utils;
import com.example.tasteit_java.request.RecipeCommentRequest;
import com.example.tasteit_java.request.RecipeLikeRequest;
import com.example.tasteit_java.request.RecipeReportRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityRecipe extends AppCompatActivity {
    private ImageView ivRecipePhoto;
    private TextView tvRecipeName;
    private RatingBar rbRating;
    private TextView tvNameCreator;
    private TabLayout tlRecipe;
    private ViewPager2 vpPaginator;
    private ShimmerFrameLayout shimmer;
    private FloatingActionButton bLike;

    private FloatingActionButton bComment;
    private int recipeId;
    private String token;
    private Recipe recipe;
    private String creatorToken;
    private String accessToken;
    boolean seeEdit;
    private AdapterFragmentRecipe adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        accessToken = Utils.getUserAcessToken();
        token = Utils.getUserToken();
        recipeId = 0;
        creatorToken = "";
        seeEdit = false;
        initializeViews();

        //Recogemos la receta pasada como parametro y el uid
        if (getIntent().getExtras() != null) {
            Bundle params = getIntent().getExtras();
            if (params.size() == 2) {
                recipeId = params.getInt("recipeId");
                creatorToken = params.getString("creatorToken");
            } else {
                recipeId = params.getInt("recipeId");
            }
        }

        getIsLiked();

        //menu superior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initializeViews() {
        BottomNavigationView fcMainMenu = findViewById(R.id.fcMainMenu);
        fcMainMenu.setOnItemSelectedListener(new OnItemNavSelectedListener(this));

        ivRecipePhoto = findViewById(R.id.ivRecipePhoto);
        tvRecipeName = findViewById(R.id.tvRecipeName);
        rbRating = findViewById(R.id.rbRating);
        tvNameCreator = findViewById(R.id.tvNameCreator);
        tlRecipe = findViewById(R.id.tlRecipe);
        vpPaginator = findViewById(R.id.vpPaginator);

        shimmer = findViewById(R.id.shimmer);
        shimmer.startShimmer();

        if (creatorToken.equals(token)) {
            seeEdit = true;
        } else {
            seeEdit = false;
        }

        tlRecipe.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() != vpPaginator.getCurrentItem()){
                    if(tab.getPosition() == 2 && vpPaginator.getCurrentItem() != 2){
                        bLike.setVisibility(View.GONE);
                        bComment.setVisibility(View.VISIBLE);
                    }else if(tab.getPosition() != 2 && vpPaginator.getCurrentItem() == 2){
                        bComment.setVisibility(View.GONE);
                        bLike.setVisibility(View.VISIBLE);
                    }}
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

        //boton de me gusta
        bLike = findViewById(R.id.bLike);
        bComment = findViewById(R.id.bComment);
        bLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecipeLikeRequest recipeLikeRequest = new RecipeLikeRequest(String.valueOf(recipeId), token);
                ApiClient apiClient = ApiClient.getInstance(accessToken);
                Call<Void> call = apiClient.getService().likeOnRecipe(recipeLikeRequest);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            getIsLiked();
                        } else {
                            // Handle the error
                            Log.e("API_ERROR", "Response error: " + response.code() + " " + response.message());
                            Toast.makeText(ActivityRecipe.this, "bad!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        // Handle the error
                        Toast.makeText(ActivityRecipe.this, "bad!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        bComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View rate = View.inflate(ActivityRecipe.this, R.layout.item_rate, null);
                RatingBar rbRating = rate.findViewById(R.id.rbRating);
                EditText etComment = rate.findViewById(R.id.etCommentRate);

                AlertDialog.Builder builderRate = new AlertDialog.Builder(ActivityRecipe.this);
                builderRate.setView(rate);

                builderRate.setPositiveButton("Send!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //comentario en receta segun ID
                        RecipeCommentRequest recipeCommentRequest = new RecipeCommentRequest(String.valueOf(recipeId), token, String.valueOf(etComment.getText()), String.valueOf(rbRating.getRating()));
                        ApiClient apiClient = ApiClient.getInstance(accessToken);
                        Call<Void> call = apiClient.getService().commentRecipe(recipeCommentRequest);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    getIsLiked();
                                } else {
                                    // Handle the error
                                    Log.e("API_ERROR", "Response error: " + response.code() + " " + response.message());
                                    Toast.makeText(ActivityRecipe.this, "bad!", Toast.LENGTH_SHORT).show();

                                }
                            }
                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                // Handle the error
                                Toast.makeText(ActivityRecipe.this, "bad!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                builderRate.setNegativeButton("Cancel", null);
                builderRate.create().show();
            }
        });

        tvNameCreator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(creatorToken != null && creatorToken != "")  {
                    Intent i = new Intent(getApplicationContext(), ActivityProfile.class);
                    i.putExtra("uid", creatorToken);
                    startActivity(i);
                }
            }
        });
    }

    //MENU superior
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_menu, menu);
        if (seeEdit) {
            menu.findItem(R.id.iEditRecipe).setVisible(true);
        } else {
            menu.findItem(R.id.iEditRecipe).setVisible(false);
        }
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
            case R.id.iReport:
                View report = View.inflate(ActivityRecipe.this, R.layout.item_report, null);
                EditText etCommentReport = report.findViewById(R.id.etCommentReport);
                AlertDialog.Builder builderReport = new AlertDialog.Builder(ActivityRecipe.this);
                builderReport.setView(report);
                builderReport.setPositiveButton("Send!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //report receta segun ID
                        RecipeReportRequest recipeReportRequest = new RecipeReportRequest(String.valueOf(recipeId), token, String.valueOf(etCommentReport.getText()));
                        ApiClient apiClient = ApiClient.getInstance(accessToken);
                        Call<Void> call = apiClient.getService().reportRecipe(recipeReportRequest);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    getIsLiked();
                                } else {
                                    // Handle the error
                                    Log.e("API_ERROR", "Response error: " + response.code() + " " + response.message());
                                    Toast.makeText(ActivityRecipe.this, "bad!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                // Handle the error
                                Toast.makeText(ActivityRecipe.this, "bad!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                builderReport.setNegativeButton("Cancel", null);
                builderReport.create().show();
                return true;
            case R.id.iEditRecipe:
                Intent i = new Intent(getApplicationContext(), ActivityNewRecipe.class);
                i.putExtra("recipeId", recipeId);
                i.putExtra("creatorToken", creatorToken);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void bringRecipe() {
        RecipeLoader recipesLoader = new RecipeLoader(ApiClient.getInstance(accessToken).getService(), this, recipeId);
        recipesLoader.getRecipeById().observe(this, this::onRecipeLoaded);
        recipesLoader.loadRecipeById();
    }

    private void onRecipeLoaded(Recipe recipes) {
        recipe = recipes;

        try {
            Picasso.with(this).load(recipe.getImage()).into(ivRecipePhoto);
        }catch(IllegalArgumentException iae){
            Toast.makeText(this, "Image not retrieved", Toast.LENGTH_SHORT).show();
        }

        tvRecipeName.setText(recipe.getName());
        tvNameCreator.setText(recipe.getCreator());
        rbRating.setRating(recipe.getRating());

        if(recipe.getCreatorToken() != null && recipe.getCreatorToken() != "") {
            creatorToken = recipe.getCreatorToken();
        }

        if(adapter == null) {
            adapter = new AdapterFragmentRecipe(getSupportFragmentManager(), getLifecycle(), recipe.getId());
            vpPaginator.setAdapter(adapter);
            shimmer.stopShimmer();
            shimmer.hideShimmer();
        }

        shimmer.stopShimmer();
        shimmer.hideShimmer();
    }

    private void getIsLiked() {
        RecipeLoader isFollowing = new RecipeLoader(ApiClient.getInstance(Utils.getUserAcessToken()).getService(), this, token, recipeId);
        isFollowing.getIsLiked().observe(this, this::onIsLikedLoaded);
        isFollowing.loadIsLiked();
    }

    private void onIsLikedLoaded(Boolean isLiked) {
        initializeViews();
        bringRecipe();

        if (isLiked) {
            bLike.setRotationX(180);
        } else {
            bLike.setRotationX(0);
        }
    }
}