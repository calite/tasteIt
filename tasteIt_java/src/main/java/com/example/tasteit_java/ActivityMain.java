package com.example.tasteit_java;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.tasteit_java.ApiService.ApiClient;
import com.example.tasteit_java.ApiGetters.RecipeLoader;
import com.example.tasteit_java.ApiGetters.UserLoader;
import com.example.tasteit_java.adapters.AdapterEndlessRecyclerMain;
import com.example.tasteit_java.clases.OnItemNavSelectedListener;
import com.example.tasteit_java.clases.OnLoadMoreListener;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.SharedPreferencesSaved;
import com.example.tasteit_java.clases.User;
import com.example.tasteit_java.clases.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ActivityMain extends AppCompatActivity {
    private AdapterEndlessRecyclerMain adapter;
    private int skipper;
    private int allItemsCount;
    private boolean allItemsLoaded;
    private FloatingActionButton bCreate;
    private ShimmerFrameLayout shimmer;
    private MenuItem profileImg;
    private RecyclerView rvRecipes;
    private String accessToken;
    private String uid;
    private HashSet<String> idRecipes;
    private SharedPreferencesSaved sharedPreferences;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setLogo(R.drawable.logo4alphadarkpeque);

        shimmer = findViewById(R.id.shimmer);
        shimmer.startShimmer();

        sharedPreferences = new SharedPreferencesSaved(this);
        accessToken = sharedPreferences.getSharedPreferences().getString("accessToken", "null");
        uid = sharedPreferences.getSharedPreferences().getString("uid", "null");

        rvRecipes = findViewById(R.id.rvRecipes);
        bCreate = findViewById(R.id.bCreate);
        skipper = 0;
        allItemsCount = 0;
        allItemsLoaded = false;
        idRecipes = new HashSet<>();

        rvRecipes.setHasFixedSize(true);
        bringRecipes();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvRecipes.setLayoutManager(linearLayoutManager);

        adapter = new AdapterEndlessRecyclerMain(rvRecipes);
        rvRecipes.setAdapter(adapter);

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if(allItemsLoaded) { //habra que ponerle un limite (que en principio puede ser el total de recipes en la bbdd o algo fijo para no sobrecargar el terminal)
                    Toast.makeText(ActivityMain.this, "Finiquitao con " + adapter.getItemCount() + " recetas", Toast.LENGTH_SHORT).show();
                } else {
                    adapter.dataList.add(null);
                    adapter.notifyItemInserted(adapter.getItemCount() - 1);

                    skipper += 10;
                    bringRecipes();
                }
            }
            @Override
            public void update() {
                updateList();
            }
        });

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

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Utils.refreshToken();
                Logger.getGlobal().log(Level.INFO,"Attempting to refresh token");
                accessToken = Utils.getUserAcessToken();
                SharedPreferences.Editor editor = sharedPreferences.getEditer();
                editor.putString("accessToken", Utils.getUserAcessToken());
                editor.commit();
            }
        },1,1800000);
    }

    //MENU superior
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        profileImg = menu.getItem(0);
        retrieveProfileImg();

        BottomNavigationView fcMainMenu = findViewById(R.id.fcMainMenu);
        fcMainMenu.setSelectedItemId(R.id.bHome);
        fcMainMenu.setOnItemSelectedListener(new OnItemNavSelectedListener(this));

        menu.getItem(1).setVisible(false);

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
                Intent i = new Intent(ActivityMain.this, ActivityProfile.class);
                startActivity(i);
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
        SharedPreferences.Editor editor = sharedPreferences.getEditer();
        editor.remove("uid");
        editor.remove("accessToken");
        editor.commit();

        startActivity(new Intent(this, ActivityLogin.class));
        finish();
    }

    public void updateList() {
        skipper = 0;
        allItemsCount = 0;
        allItemsLoaded = false;
        adapter.dataList.clear();
        adapter.notifyDataSetChanged();

        shimmer.setVisibility(View.VISIBLE);
        shimmer.startShimmer();

        bringRecipes();
    }

    private void bringRecipes() {
        RecipeLoader recipesLoader = new RecipeLoader(ApiClient.getInstance(accessToken).getService(), this, skipper);
        recipesLoader.getAllRecipesWSkipper().observe(this, this::onRecipesLoaded);
        recipesLoader.loadAllRecipesWSkipper();
    }

    private void onRecipesLoaded(List<Recipe> recipes) {
        if(adapter.getItemCount() > 0) {
            if(adapter.getItemViewType(adapter.getItemCount() - 1) != 0) {
                adapter.dataList.remove(adapter.getItemCount() - 1);
            } else if(adapter.getItemViewType(0) != 0) {
                adapter.dataList.remove(0);
            }
        }

        adapter.dataList.addAll(recipes);
        adapter.setLoaded();
        adapter.notifyDataSetChanged();

        if(adapter.dataList.size() != allItemsCount) {
            allItemsCount = adapter.dataList.size();

            for (Recipe temp:recipes) {
                idRecipes.add(String.valueOf(temp.getId()));
            }
            SharedPreferences.Editor editor = sharedPreferences.getEditer();
            editor.putStringSet("idRecipes", idRecipes);
            editor.commit();

        } else {
            allItemsLoaded = true;
        }

        shimmer.stopShimmer();
        shimmer.setVisibility(View.GONE);
    }

    private void retrieveProfileImg() {
        UserLoader userLoader = new UserLoader(ApiClient.getInstance(accessToken).getService(), this, uid);
        userLoader.getAllUser().observe(this, this::onProfileImgLoaded);
        userLoader.loadAllUser();
    }

    private void onProfileImgLoaded(HashMap<String, Object> temp) {
        User user = (User) temp.get("user");
        Glide.with(this)
                .load(user.getImgProfile())
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        Bitmap bitmap = Bitmap.createBitmap(resource.getIntrinsicWidth(),
                                resource.getIntrinsicHeight(),
                                Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        resource.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                        resource.draw(canvas);

                        Drawable roundedDrawable = new BitmapDrawable(getResources(), Utils.getRoundBitmapWithImage(bitmap));
                        profileImg.setIcon(roundedDrawable);
                    }
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });

        if(!sharedPreferences.getSharedPreferences().contains("urlImgProfile") || !sharedPreferences.getSharedPreferences().getString("urlImgProfile", "null").equals(user.getImgProfile())) {
            SharedPreferences.Editor editor = sharedPreferences.getEditer();
            editor.putString("urlImgProfile", user.getImgProfile());
            editor.commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = sharedPreferences.getEditer();
        editor.remove("idRecipes");
        editor.commit();
    }

    @Override
    protected void onRestart() {
        updateList();
        super.onRestart();
    }
}