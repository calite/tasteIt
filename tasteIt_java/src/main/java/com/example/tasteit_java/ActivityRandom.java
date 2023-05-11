package com.example.tasteit_java;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.tasteit_java.ApiService.ApiClient;
import com.example.tasteit_java.ApiUtils.RecipeLoader;
import com.example.tasteit_java.ApiUtils.UserLoader;
import com.example.tasteit_java.adapters.AdapterFragmentRandom;
import com.example.tasteit_java.clases.OnItemNavSelectedListener;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.User;
import com.example.tasteit_java.clases.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActivityRandom extends AppCompatActivity {
    private Button btnShuffle;
    private ShimmerFrameLayout shimmer;
    private MenuItem profileImg;
    private ArrayList<Recipe> someRecipes;
    private ArrayList<Integer> lastIdRecipes;
    private AdapterFragmentRandom adapter;
    private ViewPager2 vpRandom;
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random);

        accessToken = Utils.getUserAcessToken();

        BottomNavigationView fcMainMenu = findViewById(R.id.fcMainMenu);
        fcMainMenu.setSelectedItemId(R.id.bRandom);
        fcMainMenu.setOnItemSelectedListener(new OnItemNavSelectedListener(this));

        getSupportActionBar().setTitle("Random");
        btnShuffle = findViewById(R.id.btnShuffle);
        vpRandom = findViewById(R.id.vpRandom);
        vpRandom.setVisibility(View.INVISIBLE);
        shimmer = findViewById(R.id.shimmer);
        someRecipes = new ArrayList<>();
        lastIdRecipes = new ArrayList<>();

        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnShuffle.setVisibility(View.INVISIBLE);
                btnShuffle.setEnabled(false);
                findViewById(R.id.textView11).setVisibility(View.INVISIBLE);
                shimmer.setVisibility(View.VISIBLE);
                shimmer.startShimmer();

                bringRecipes();
            }
        });

        vpRandom.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position == 5) {
                    bringRecipes();
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

        profileImg = menu.getItem(0);
        retrieveProfileImg();

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

    private void bringRecipes() {
        RecipeLoader recipesLoader = new RecipeLoader(ApiClient.getInstance(accessToken).getService(), this, 5);
        recipesLoader.getRandomRecipes().observe(this, this::onRecipeLoaded);
        recipesLoader.loadRandomRecipes();
    }

    private void onRecipeLoaded(List<Recipe> recipes) {
        adapter = new AdapterFragmentRandom(getSupportFragmentManager(), getLifecycle(), someRecipes, vpRandom);
        vpRandom.setAdapter(adapter);
        shimmer.stopShimmer();
        shimmer.setVisibility(View.GONE);
        vpRandom.setVisibility(View.VISIBLE);

        adapter.notifyDataSetChanged();
        someRecipes.clear();
    }

    private void retrieveProfileImg() {
        UserLoader userLoader = new UserLoader(ApiClient.getInstance(accessToken).getService(), this, Utils.getUserToken());
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
                        Toast.makeText(ActivityRandom.this, "Something was wrong loading ...", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}