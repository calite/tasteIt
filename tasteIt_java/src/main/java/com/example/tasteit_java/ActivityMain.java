package com.example.tasteit_java;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasteit_java.adapters.AdapterEndlessRecyclerMain;
import com.example.tasteit_java.adapters.AdapterGridViewMain;
import com.example.tasteit_java.adapters.AdapterRecyclerMain;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.OnLoadMoreListener;
import com.example.tasteit_java.clases.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ActivityMain extends AppCompatActivity {

    //PRUEBAS
    private AdapterEndlessRecyclerMain adapterEndlessRecyclerMain;
    private int skipper;
    protected Handler handler;

    private FloatingActionButton bCreate;
    //TEMPORAL GALERIA
    private GridView gvRecipes;
    private AdapterGridViewMain adapter;
    private RecyclerView rvRecipes;
    private AdapterRecyclerMain adapterRecyclerView;
    public static ArrayList<Object> listRecipes;
    private ProgressBar pgMain;

    private String token;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pgMain = findViewById(R.id.pbMain);
        rvRecipes = findViewById(R.id.rvRecipes);
        bCreate = findViewById(R.id.bCreate);

        //PRUEBAS
        skipper = 0;
        handler = new Handler();
        initializeRetrieveData();

        rvRecipes.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvRecipes.setLayoutManager(linearLayoutManager);

        adapterEndlessRecyclerMain = new AdapterEndlessRecyclerMain(listRecipes, rvRecipes);
        rvRecipes.setAdapter(adapterEndlessRecyclerMain);

        if (listRecipes.isEmpty()) {
            rvRecipes.setVisibility(View.GONE);
            pgMain.setVisibility(View.VISIBLE);

        } else {
            rvRecipes.setVisibility(View.VISIBLE);
            pgMain.setVisibility(View.GONE);
        }

        adapterEndlessRecyclerMain.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if(adapterEndlessRecyclerMain.dataList.size() > 28) { //habra que ponerle un limite (que en principio puede ser el total de recipes en la bbdd o algo fijo para no sobrecargar el terminal)
                    Toast.makeText(ActivityMain.this, "Finiquitao", Toast.LENGTH_SHORT).show();
                } else {
                    adapterEndlessRecyclerMain.dataList.add(null);
                    adapterEndlessRecyclerMain.notifyItemInserted(adapterEndlessRecyclerMain.dataList.size() - 1);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //remove progress item
                            adapterEndlessRecyclerMain.dataList.remove(adapterEndlessRecyclerMain.dataList.size() - 1);
                            //adapterEndlessRecyclerMain.notifyItemRemoved(adapterEndlessRecyclerMain.dataList.size());
                            skipper += 10;
                            adapterEndlessRecyclerMain.dataList.addAll(new BdConnection().retrieveAllRecipes10by10(skipper));
                            adapterEndlessRecyclerMain.setLoaded();
                            adapterEndlessRecyclerMain.notifyDataSetChanged();
                        }
                    }, 5000);
                }
            }
        });

        //FIN PRUEBAS

        //bringRecipes();

        //menu superior
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

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



        /*
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
        */
    }

    //MENU superior
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);

        Bitmap originalBitmap = Utils.decodeBase64(new BdConnection().retrieveUserbyUid(token).getImgProfile());
        BitmapDrawable roundedBitmapDrawable = new BitmapDrawable(getResources(), Utils.getRoundBitmapWithImage(originalBitmap));
        menu.getItem(0).setIcon(roundedBitmapDrawable);

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

                //PRUEBAS!!!!!!!!!!!!

    private void initializeRetrieveData() {
        listRecipes = new ArrayList<>(new BdConnection().retrieveAllRecipes10by10(skipper));
    }

    //carga de recetas asyncrona
    /*private class RecipesLoader {

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

            apiRequests.getAllRecipes().enqueue(new Callback<List<RecipeId_Recipe_User>>() {
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
                    Toast.makeText(ActivityMain.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void onRecipesLoaded(List<Recipe> recipes) {
        // Actualizar la UI con la lista de recetas
        //pgMain.setVisibility(View.GONE);
        displayLoadingIndicator();

        //listRecipes.addAll(recipes);
        displayData(Collections.singletonList(recipes));
        /*
        adapter = new AdapterGridViewMain(getApplicationContext(), listRecipes);
        gvRecipes.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        */

        /*LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvRecipes.setLayoutManager(linearLayoutManager);


        //PRUEBAS
        adapterEndlessRecyclerMain = new AdapterEndlessRecyclerMain(listRecipes);
        rvRecipes.setAdapter(adapterEndlessRecyclerMain);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvRecipes.getContext(),
                ((LinearLayoutManager) rvRecipes.getLayoutManager()).getOrientation());
        rvRecipes.addItemDecoration(dividerItemDecoration);

        endlessScrollListener = new EndlessScrollListener((EndlessScrollListener.ScrollerClient) ActivityMain.this);
        rvRecipes.addOnScrollListener(endlessScrollListener);


        //adapterRecyclerView = new AdapterRecyclerMain(listRecipes);
        //rvRecipes.setAdapter(adapterRecyclerView);

        /*
        rvRecipes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Verificar si el usuario se desplaza hacia arriba y está cerca del principio de la lista
                if (dy < 0 && linearLayoutManager.findFirstVisibleItemPosition() < 10) {
                    // Recargar el RecyclerView
                    Toast.makeText(ActivityMain.this, "Refreshing...", Toast.LENGTH_SHORT).show();
                    adapterRecyclerView.notifyDataSetChanged();
                }
            }
        });
        */

    /*}

    private void bringRecipes() {
        //olvidamos asynctask y metemos lifecycle, que es mas actual y esta mejor optimizado
        RecipesLoader recipesLoader = new RecipesLoader(ApiClient.getInstance().getService());

        recipesLoader.getRecipes().observe(this, this::onRecipesLoaded);

        recipesLoader.loadRecipes();
    }*/

}