package com.example.tasteit_java;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tasteit_java.ApiService.ApiClient;
import com.example.tasteit_java.ApiService.ApiRequests;
import com.example.tasteit_java.ApiService.RecipeId_Recipe_User;
import com.example.tasteit_java.adapters.AdapterFragmentSearch;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.OnItemNavSelectedListener;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.User;
import com.example.tasteit_java.clases.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivitySearch extends AppCompatActivity {
    private TabLayout tlSearch;
    private ViewPager2 vpPaginator;
    private ImageButton ivsearch;
    private EditText tvSearch;
    private AdapterFragmentSearch adapter;
    private ArrayList<Object> dataListAux;
    private ShimmerFrameLayout shimmer;
    private String busqueda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        BottomNavigationView fcMainMenu = findViewById(R.id.fcMainMenu);
        fcMainMenu.setSelectedItemId(R.id.bSearch);
        fcMainMenu.setOnItemSelectedListener(new OnItemNavSelectedListener(this));

        initializeViews();
        bringRecipes(); //Llamariamos a una peticion para sacar las recetas mas comentadas o populares de primeras y luego ya cuando se busca algo lo cambiamos

        tlSearch.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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

        ivsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                busqueda = tvSearch.getText().toString();
                if (busqueda.length() > 0) { //Aqui sacariamos los resultados de la búsqueda y creariamos el adapter de nuevo
                    vpPaginator.setVisibility(View.GONE);
                    shimmer.setVisibility(View.VISIBLE);
                    shimmer.startShimmer();
                    dataListAux.clear();
                    bringRecipes();
                    new TaskLoadUser().execute();
                }
            }
        });

        vpPaginator.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                //super.onPageSelected(position);
                tlSearch.selectTab(tlSearch.getTabAt(position));
            }
        });

        //menu superior
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search");
    }

    private void initializeViews() {
        busqueda = "";
        dataListAux = new ArrayList<>();
        tvSearch = findViewById(R.id.tvSearch);
        tlSearch = findViewById(R.id.tlSearch);
        vpPaginator = findViewById(R.id.vpPaginator);
        ivsearch = findViewById(R.id.ivsearch);
        shimmer = findViewById(R.id.shimmer);
        shimmer.startShimmer();
    }

    //MENU superior
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.getItem(1).setVisible(false);
        menu.getItem(2).setVisible(false);

        Bitmap originalBitmap = Utils.uriToBitmap(getApplicationContext(),"https://firebasestorage.googleapis.com/v0/b/tasteit-java.appspot.com/o/images%2F035d70df-1048-4c15-ba6a-c4d81d44a026?alt=media&token=d2c0ebf1-3b4e-40a4-9162-94fbc2070008");
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
            case R.id.iCloseSesion:
                signOut();
            case R.id.iDarkMode:
                if (AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //END MENU superior
    //LOGOUT
    public void callSignOut(View view) {
        signOut();
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, ActivityLogin.class));
    }

    class TaskLoadUser extends AsyncTask<User, Void, User> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected User doInBackground(User... hashMaps) {
            return new BdConnection().retrieveAllUserbyUid(Utils.getUserToken());
        }

        @Override
        protected void onPostExecute(User user) {
            //super.onPostExecute(recipes);
            dataListAux.add(user);
        }
    }

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
            apiRequests.getRecipes(10).enqueue(new Callback<List<RecipeId_Recipe_User>>() {
                @Override
                public void onResponse(Call<List<RecipeId_Recipe_User>> call, Response<List<RecipeId_Recipe_User>> response) {
                    if (response.isSuccessful()) {
                        List<RecipeId_Recipe_User> recipeApis = response.body();
                        List<Recipe> recipes = new ArrayList<>();

                        //tratamos los datos
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
                    }
                }

                @Override
                public void onFailure(Call<List<RecipeId_Recipe_User>> call, Throwable t) {
                    // Hubo un error en la solicitud
                    Toast.makeText(ActivitySearch.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void onRecipesLoaded(List<Recipe> recipes) {
        // Actualizar la UI con la lista de recetas
        dataListAux.addAll(recipes);

        adapter = new AdapterFragmentSearch(getSupportFragmentManager(), getLifecycle(), busqueda, dataListAux);
        vpPaginator.setAdapter(adapter);

        //if (adapter.getItemCount() > 0) {
            //adapter.dataList.remove(adapter.getItemCount() - 1);
        //}

        shimmer.stopShimmer();
        shimmer.setVisibility(View.GONE);
        vpPaginator.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
        //adapter.setLoaded();
    }

    private void bringRecipes() {
        //olvidamos asynctask y metemos lifecycle, que es mas actual y esta mejor optimizado
        RecipesLoader recipesLoader = new RecipesLoader(ApiClient.getInstance().getService());
        recipesLoader.getRecipes().observe(ActivitySearch.this, this::onRecipesLoaded);
        recipesLoader.loadRecipes();
    }

}