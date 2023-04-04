package com.example.tasteit_java;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasteit_java.ApiService.ApiClient;
import com.example.tasteit_java.ApiService.ApiRequests;
import com.example.tasteit_java.ApiService.RecipeApi;
import com.example.tasteit_java.adapters.AdapterGridViewMain;
import com.example.tasteit_java.adapters.AdapterRecyclerMain;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityMain extends AppCompatActivity {

    private FloatingActionButton bCreate;
    //TEMPORAL GALERIA
    private GridView gvRecipes;
    private AdapterGridViewMain adapter;
    private RecyclerView rvRecipes;
    private AdapterRecyclerMain adapterRecyclerView;
    public static ArrayList<Recipe> listRecipes = new ArrayList<>();
    private ProgressBar pgMain;

    private String token;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pgMain = findViewById(R.id.pbMain);
        gvRecipes = findViewById(R.id.gvRecipes);
        bCreate = findViewById(R.id.bCreate);

        bringRecipes();

        //menu superior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //boton crear receta
        bCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ActivityMain.this, ActivityNewRecipe.class);
                startActivity(i);
            }
        });
        //recoger token usuario firebase
        token = Utils.getUserToken();

        //grid view
        gvRecipes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int posicion, long l) {
                Intent i = new Intent(ActivityMain.this, ActivityRecipe.class);
                i.putExtra("recipeId", listRecipes.get(posicion).getId());
                startActivity(i);
            }
        });

        gvRecipes.setOnScrollListener(new AbsListView.OnScrollListener(){
            int currentScrollState, currentVisibleItemCount, currentFirstVisibleItem, currentTotalItemCount, mLastFirstVisibleItem;
            boolean canScrollV, scrollingUp;
            @Override
            public void onScrollStateChanged (AbsListView view,int scrollState){
                currentScrollState = scrollState;

                if (scrollingUp && !canScrollV) {
                    if (currentFirstVisibleItem == 0) {

                        bringRecipes();
                        Toast.makeText(ActivityMain.this, "Refreshing...", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                        scrollingUp = false;

                    }
                }
            }

            @Override
            public void onScroll (AbsListView view,int firstVisibleItem, int visibleItemCount,
                                  int totalItemCount){
                canScrollV = gvRecipes.canScrollVertically(-1);
                if(mLastFirstVisibleItem < firstVisibleItem){
                    // Scrolling down
                    scrollingUp = false;
                }
                if(mLastFirstVisibleItem > firstVisibleItem){
                    // scrolling up
                    scrollingUp = true;
                }
                mLastFirstVisibleItem = firstVisibleItem;
                currentFirstVisibleItem = firstVisibleItem;
                currentVisibleItemCount = visibleItemCount;
                currentTotalItemCount = totalItemCount;
            }
        });

        //ya funciona, pero hace cosas raras en el scroll
        //TEMPORAL - recycler view
        /*
        rvRecipes = findViewById(R.id.rvRecipes);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvRecipes.setLayoutManager(layoutManager);
        adapterRecyclerView = new AdapterRecyclerMain(listRecipes);
        rvRecipes.setAdapter(adapterRecyclerView);
        */

    }

    //MENU superior
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
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
            case R.id.iProfile:
                //NEO4J
                Intent i = new Intent(ActivityMain.this, ActivityProfile.class);
                startActivity(i);
                //FIN NEO4J

                return true;
            case R.id.iCloseSesion:
                signOut();
                return true;

            case R.id.iDarkMode:
                if(AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }else{AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);}
        }
        return super.onOptionsItemSelected(item);
    }
    //LOGOUT
    public void callSignOut(View view){
        signOut();
    }

    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        startActivity (new Intent(this, ActivityLogin.class));
    }

    //carga de recetas asyncrona
    private class RecipesLoader {

        private final ApiRequests apiRequests;
        private final MutableLiveData<List<Recipe>> recipeLiveData;

        public RecipesLoader(ApiRequests apiRequests) {
            this.apiRequests = apiRequests;
            recipeLiveData = new MutableLiveData<>();
        }

        public LiveData<List<Recipe>> getRecipes() {
            return recipeLiveData;
        }

        public void loadRecipes() {
            apiRequests.getAllRecipes().enqueue(new Callback<List<RecipeApi>>() {
                @Override
                public void onResponse(Call<List<RecipeApi>> call, Response<List<RecipeApi>> response) {
                    if (response.isSuccessful()) {
                        List<RecipeApi> recipeApis = response.body();
                        List<Recipe> recipes = new ArrayList<>();

                        //tratamos los datos
                        for (RecipeApi recipeApi : recipeApis) {
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
                                    recipeApi.getRecipeId()
                            );
                            recipes.add(recipe);
                        }
                        recipeLiveData.postValue(recipes);
                    } else {
                        // La solicitud no fue exitosa
                    }
                }

                @Override
                public void onFailure(Call<List<RecipeApi>> call, Throwable t) {
                    // Hubo un error en la solicitud
                    Toast.makeText(ActivityMain.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void onRecipesLoaded(List<Recipe> recipes) {
        // Actualizar la UI con la lista de recetas
        pgMain.setVisibility(View.GONE);

        listRecipes = (ArrayList<Recipe>) recipes;

        adapter = new AdapterGridViewMain(getApplicationContext(), listRecipes);

        gvRecipes.setAdapter(adapter);

        adapter.notifyDataSetChanged();
    }

    private void bringRecipes() {
        //olvidamos asynctask y metemos lifecycle, que es mas actual y esta mejor optimizado
        RecipesLoader recipesLoader = new RecipesLoader(ApiClient.getInstance().getService());

        recipesLoader.getRecipes().observe(this, this::onRecipesLoaded);

        recipesLoader.loadRecipes();
    }

}

