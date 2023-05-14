package com.example.tasteit_java;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.tasteit_java.ApiService.ApiClient;
import com.example.tasteit_java.ApiUtils.RecipeLoader;
import com.example.tasteit_java.ApiUtils.UserLoader;
import com.example.tasteit_java.adapters.AdapterFragmentSearch;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.OnItemNavSelectedListener;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.SharedPreferencesSaved;
import com.example.tasteit_java.clases.User;
import com.example.tasteit_java.clases.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActivitySearch extends AppCompatActivity {
    private TabLayout tlSearch;
    private ViewPager2 vpPaginator;
    private MenuItem profileImg;
    private ImageButton ivsearch;
    private EditText tvSearch;
    private AdapterFragmentSearch adapter;
    private String stringSearch;
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        accessToken = new SharedPreferencesSaved(this).getSharedPreferences().getString("accessToken", "null");

        BottomNavigationView fcMainMenu = findViewById(R.id.fcMainMenu);
        fcMainMenu.setSelectedItemId(R.id.bSearch);
        fcMainMenu.setOnItemSelectedListener(new OnItemNavSelectedListener(this));

        initializeViews();
        //Llamariamos a una peticion para sacar las recetas mas comentadas o populares de primeras y algunos usuarios populares y luego ya cuando se busca algo lo cambiamos

        tlSearch.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //tvSearch.setText("");
                vpPaginator.setCurrentItem(tab.getPosition());
                /*switch (tab.getPosition()){
                    case 0: tvSearch.setHint("Type something...");
                        break;
                    case 1: tvSearch.setHint("Profiles...");
                        break;
                    case 2: tvSearch.setHint("Tags...");
                        break;
                    case 3: tvSearch.setHint("Ingredients...");
                        break;
                }*/
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
                stringSearch = tvSearch.getText().toString();
                if (stringSearch.length() > 0) { //Aqui sacariamos los resultados de la búsqueda y creariamos el adapter de nuevo
                    adapter = new AdapterFragmentSearch(getSupportFragmentManager(), getLifecycle(), stringSearch);
                    vpPaginator.setAdapter(adapter);
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
        stringSearch = "";
        tvSearch = findViewById(R.id.tvSearch);
        tlSearch = findViewById(R.id.tlSearch);
        vpPaginator = findViewById(R.id.vpPaginator);
        ivsearch = findViewById(R.id.ivsearch);

        adapter = new AdapterFragmentSearch(getSupportFragmentManager(), getLifecycle(), stringSearch);
        vpPaginator.setAdapter(adapter);
    }

    //MENU superior
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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

    private void retrieveProfileImg() {
        UserLoader userLoader = new UserLoader(ApiClient.getInstance(Utils.getUserAcessToken()).getService(), this, Utils.getUserToken());
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
    }
}