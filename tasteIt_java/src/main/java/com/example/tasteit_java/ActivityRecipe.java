package com.example.tasteit_java;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tasteit_java.ApiService.ApiClient;
import com.example.tasteit_java.ApiService.ApiRequests;
import com.example.tasteit_java.ApiService.RecipeId_Recipe_User;
import com.example.tasteit_java.adapters.AdapterFragmentRecipe;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.OnItemNavSelectedListener;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.neo4j.driver.Query;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private int recipeId;
    private String token;
    private Recipe recipe;
    private ArrayList<Recipe> listRecipes;
    private BdConnection connection;

    String creatorToken = "";

    boolean seeEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        BottomNavigationView fcMainMenu = findViewById(R.id.fcMainMenu);
        fcMainMenu.setSelectedItemId(R.id.bHome);
        fcMainMenu.setOnItemSelectedListener(new OnItemNavSelectedListener(this));

        listRecipes = new ArrayList<>();

        ivRecipePhoto = findViewById(R.id.ivRecipePhoto);
        tvRecipeName = findViewById(R.id.tvRecipeName);
        rbRating = findViewById(R.id.rbRating);
        tvNameCreator = findViewById(R.id.tvNameCreator);
        tlRecipe = findViewById(R.id.tlRecipe);
        vpPaginator = findViewById(R.id.vpPaginator);
        shimmer = findViewById(R.id.shimmer);
        shimmer.startShimmer();

        //recogemos la receta pasada como parametro y el uid
        if(getIntent().getExtras() != null) {
            Bundle params = getIntent().getExtras();
            if(params.size() == 2) {
                recipeId = params.getInt("recipeId");
                creatorToken = params.getString("creatorToken");
            } else {
                recipeId = params.getInt("recipeId");
            }
        }

        token = Utils.getUserToken();

        if(creatorToken.equals(token)) {
            seeEdit = true;
        }else{
            seeEdit = false;
        }

        bringRecipe();

        connection = new BdConnection();

        //menu superior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //boton de me gusta
        bLike = findViewById(R.id.bLike);

        bLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(connection.likeRecipe(recipe.getId(),token)){
                    Toast.makeText(ActivityRecipe.this, "Liked", Toast.LENGTH_SHORT).show();
                    bLike.setRotationX(180);
                }else{
                    Toast.makeText(ActivityRecipe.this, "Disliked", Toast.LENGTH_SHORT).show();
                    bLike.setRotationX(0);
                };
            }
        });

        tvNameCreator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TaskLoadUser().execute();
            }
        });
    }

    //MENU superior
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.recipe_menu, menu);
        if(creatorToken.equals(token)){
            menu.findItem(R.id.iEditRecipe).setVisible(true);
        } else{
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
            case R.id.iRate:

                View rate = View.inflate(ActivityRecipe.this,R.layout.item_rate,null);
                RatingBar rbRating = rate.findViewById(R.id.rbRating);
                EditText etComment = rate.findViewById(R.id.etCommentRate);

                AlertDialog.Builder builderRate = new AlertDialog.Builder(ActivityRecipe.this);
                builderRate.setView(rate);
                builderRate.setPositiveButton("Send!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //comentario en receta segun ID


                        //connection.commentRecipe(recipe.getId(),uid, etComment.getText().toString(), rbRating.getRating());


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
                        //report receta segun ID

                        //connection.reportRecipe(recipe.getId(),uid,etCommentReport.getText().toString());


                    }
                });
                builderReport.setNegativeButton("Cancel",null);
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

    class TaskLoadUser extends AsyncTask<Void, Void,String> {

        @Override
        protected String doInBackground(Void... voids) {
            Session session = connection.openSession();
            Query query = new Query("MATCH (r:Recipe)-[:Created]->(u:User) where ID(r) = "+recipe.getId()+" RETURN u.token;");
            Result result = session.run(query);

            String userid = result.single().get(0).asString();
            connection.closeSession(session);
            return userid;
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String userid) {
            //super.onPostExecute(recipes);
            Logger log = Logger.getLogger(ActivityRecipe.class.getName());
            log.log(Level.INFO, "RECIPE ID: "+String.valueOf(recipe.getId()));
            Intent i = new Intent(getApplicationContext(), ActivityProfile.class);
            i.putExtra("uid", userid);
            startActivity(i);
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

            apiRequests.getRecipeById(recipeId).enqueue(new Callback<List<RecipeId_Recipe_User>>() {
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
                            creatorToken = recipeApi.getUser().getToken();
                        }
                        recipeLiveData.postValue(recipes);
                    } else {
                        // La solicitud no fue exitosa
                        Toast.makeText(ActivityRecipe.this, "something went wrong", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<List<RecipeId_Recipe_User>> call, Throwable t) {
                    Toast.makeText(ActivityRecipe.this, "something went wrong", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void onRecipeLoaded(List<Recipe> recipes) {
        // Actualizar la UI con la info de la recetas

        listRecipes = (ArrayList<Recipe>) recipes;

        recipe = listRecipes.get(0);

        //UNA VEZ SE HACE LA CARGA DE LA RECETA SE RELLENA LA INFORMACION Y SE HA MOVIDO EL CODIGO QUE DEPENDIESE DE LA MISMA(PAGINATOR Y EL BOTON DE LIKE)

        Bitmap bitmap = Utils.decodeBase64(recipe.getImage());
        ivRecipePhoto.setImageBitmap(bitmap);
        tvRecipeName.setText(recipe.getName());
        tvNameCreator.setText(recipe.getCreator());

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

        if(connection.isLiked(recipe.getId(),token)){
            bLike.setRotationX(180);
        }else{bLike.setRotationX(0);}

        shimmer.stopShimmer();
        shimmer.hideShimmer();
    }

    private void bringRecipe() {
        RecipeLoader recipesLoader = new RecipeLoader(ApiClient.getInstance().getService());
        recipesLoader.getRecipe().observe(this, this::onRecipeLoaded);
        recipesLoader.loadRecipe();
    }

}