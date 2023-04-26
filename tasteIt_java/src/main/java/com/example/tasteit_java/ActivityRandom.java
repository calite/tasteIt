package com.example.tasteit_java;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tasteit_java.ApiService.ApiClient;
import com.example.tasteit_java.ApiService.ApiRequests;
import com.example.tasteit_java.ApiService.RecipeId_Recipe_User;
import com.example.tasteit_java.adapters.AdapterFragmentRandom;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.OnItemNavSelectedListener;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityRandom extends AppCompatActivity {
    private Button btnShuffle;
    private ShimmerFrameLayout shimmer;
    private int recipeId;
    private ArrayList<Recipe> someRecipes;
    private ArrayList<Integer> lastIdRecipes;
    private AdapterFragmentRandom adapter;
    private ViewPager2 vpRandom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random);

        BottomNavigationView fcMainMenu = findViewById(R.id.fcMainMenu);
        fcMainMenu.setSelectedItemId(R.id.bRandom);
        fcMainMenu.setOnItemSelectedListener(new OnItemNavSelectedListener(this));

        getSupportActionBar().setTitle("Random");
        btnShuffle = findViewById(R.id.btnShuffle);
        vpRandom = findViewById(R.id.vpRandom);
        shimmer = findViewById(R.id.shimmer);
        someRecipes = new ArrayList<>();
        lastIdRecipes = new ArrayList<>();

        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bringRecipe();
                btnShuffle.setVisibility(View.INVISIBLE);
                btnShuffle.setEnabled(false);
                findViewById(R.id.textView11).setVisibility(View.INVISIBLE);
                shimmer.setVisibility(View.VISIBLE);
                shimmer.startShimmer();
            }
        });

        vpRandom.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if(position == 5) {
                    bringRecipe();
                }
            }
        });

    }

    //MENU superior
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        menu.getItem(1).setVisible(false);
        menu.getItem(2).setVisible(false);

        Bitmap originalBitmap = Utils.decodeBase64(new BdConnection().retrieveUserbyUid(Utils.getUserToken()).getImgProfile());
        BitmapDrawable roundedBitmapDrawable = new BitmapDrawable(getResources(), Utils.getRoundBitmapWithImage(originalBitmap));
        menu.getItem(0).setIcon(roundedBitmapDrawable);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.iProfile:
                startActivity(new Intent(getApplicationContext(), ActivityProfile.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class RecipeLoader {
        private final ApiRequests apiRequests;
        private final MutableLiveData<List<Recipe>> recipeLiveData;
        public RecipeLoader(ApiRequests apiRequests) {
            this.apiRequests = apiRequests;
            recipeLiveData = new MutableLiveData<>();
        }
        public LiveData<List<Recipe>> getRecipe() {
            return recipeLiveData;
        }
        public void loadRecipe() {
            do {
                recipeId = (int) (Math.random() * 50) + 1; //PDTE CAMBIAR POR LA CANTIDAD TOTAL DE RECETAS (PET API)
                Toast.makeText(ActivityRandom.this, "ID: " + recipeId + " - Size: " + someRecipes.size(), Toast.LENGTH_SHORT).show();
            } while(lastIdRecipes.contains(recipeId));
            apiRequests.getRecipeById(recipeId).enqueue(new Callback<List<RecipeId_Recipe_User>>() {
                @Override
                public void onResponse(Call<List<RecipeId_Recipe_User>> call, Response<List<RecipeId_Recipe_User>> response) {
                    if (response.isSuccessful()) {
                        List<RecipeId_Recipe_User> recipeApis = response.body();
                        List<Recipe> recipes = new ArrayList<>();

                        for (RecipeId_Recipe_User recipeApi : recipeApis) {
                            Recipe recipe = new Recipe(
                                    recipeApi.getRecipeDetails().getName(),
                                    recipeApi.getRecipeDetails().getDescription(),
                                    (ArrayList<String>) recipeApi.getRecipeDetails().getSteps(),
                                    recipeApi.getRecipeDetails().getDateCreated(),
                                    recipeApi.getRecipeDetails().getDifficulty(),
                                    recipeApi.getUser().getUsername(),
                                    recipeApi.getRecipeDetails().getImage(),
                                    recipeApi.getRecipeDetails().getCountry(),
                                    (ArrayList<String>) recipeApi.getRecipeDetails().getTags(),
                                    (ArrayList<String>) recipeApi.getRecipeDetails().getIngredients(),
                                    recipeApi.getRecipeId(),
                                    recipeApi.getUser().getToken()
                            );
                            recipes.add(recipe);
                        }
                        recipeLiveData.postValue(recipes);
                    } else {
                        // La solicitud no fue exitosa
                        //Toast.makeText(ActivityRandom.this, "Bad id, trying again ... " + someRecipes.size(), Toast.LENGTH_SHORT).show();
                        loadRecipe();
                    }
                }
                @Override
                public void onFailure(Call<List<RecipeId_Recipe_User>> call, Throwable t) {
                    Toast.makeText(ActivityRandom.this, "Something went wrong - " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void onRecipeLoaded(List<Recipe> recipes) {
        if(someRecipes.size() <= 4) {
            someRecipes.add(recipes.get(0));
            lastIdRecipes.add(recipeId);
            bringRecipe();
        } else {
            adapter = new AdapterFragmentRandom(getSupportFragmentManager(), getLifecycle(), someRecipes);
            vpRandom.setAdapter(adapter);
            shimmer.stopShimmer();
            shimmer.setVisibility(View.GONE);
            vpRandom.setVisibility(View.VISIBLE);

            adapter.notifyDataSetChanged();
            someRecipes.clear();
        }
    }

    private void bringRecipe() {
        RecipeLoader recipesLoader = new RecipeLoader(ApiClient.getInstance().getService());
        recipesLoader.getRecipe().observe(this, this::onRecipeLoaded);
        recipesLoader.loadRecipe();
    }
}