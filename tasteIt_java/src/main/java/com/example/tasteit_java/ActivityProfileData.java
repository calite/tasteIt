package com.example.tasteit_java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.tasteit_java.ApiService.ApiClient;
import com.example.tasteit_java.ApiUtils.RecipeLoader;
import com.example.tasteit_java.ApiUtils.UserLoader;
import com.example.tasteit_java.adapters.AdapterRecyclerProfileData;
import com.example.tasteit_java.clases.OnLoadMoreListener;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.User;
import com.example.tasteit_java.clases.Utils;
import com.example.tasteit_java.fragments.FragmentMyBook;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

public class ActivityProfileData extends AppCompatActivity {
    private RecyclerView rvListData;
    private AdapterRecyclerProfileData adapter;
    private String accessToken;
    private int skipper;
    private String uidProfile;
    private int dataType;
    private ShimmerFrameLayout shimmer;
    private int allItemsCount;
    private boolean allItemsLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_data);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        shimmer = findViewById(R.id.shimmer);
        shimmer.startShimmer();

        accessToken = Utils.getUserAcessToken();
        skipper = 0;
        allItemsCount = 0;
        allItemsLoaded = false;

        if (getIntent().getExtras() != null) {
            uidProfile = getIntent().getExtras().getString("uid");
            dataType = getIntent().getExtras().getInt("dataType");
        } else {
            Toast.makeText(this, "Error al cargar los datos, volviendo atrás ...", Toast.LENGTH_SHORT).show();
            finish();
        }

        retrieveData();

        rvListData = findViewById(R.id.rvListData);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvListData.setLayoutManager(linearLayoutManager);

        adapter = new AdapterRecyclerProfileData(this, this, rvListData, dataType);
        rvListData.setAdapter(adapter);

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if(allItemsLoaded) { //habra que ponerle un limite (que en principio puede ser el total de recipes en la bbdd o algo fijo para no sobrecargar el terminal)
                    Toast.makeText(ActivityProfileData.this, "Finiquitao con " + adapter.getItemCount() + " items", Toast.LENGTH_SHORT).show();
                } else {
                    adapter.dataList.add(null);
                    adapter.notifyItemInserted(adapter.getItemCount() - 1);

                    skipper += 10;
                    retrieveData();
                }
            }

            @Override
            public void update() {
                adapter.dataList.add(0, null);
                adapter.notifyItemInserted(0);

                skipper = 0;
                allItemsCount = 0;
                allItemsLoaded = false;
                adapter.dataList.clear();
                retrieveData();
            }
        });

        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                //Toast.makeText(this, "Aqui finalizamos", Toast.LENGTH_SHORT).show();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void retrieveData() {
        switch (dataType) {
            case 1: { //Recipes
                getSupportActionBar().setTitle("Recipes");
                bringUserRecipes();
                break;
            }
            case 2: { //Followers
                getSupportActionBar().setTitle("Followers");
                bringUserFollowers();
                break;
            }
            case 3: { //Following
                getSupportActionBar().setTitle("Following");
                bringUserFollowing();
                break;
            }
            case 4: { //Following
                getSupportActionBar().setTitle("Recipes Liked");
                bringRecipesLikedByUser();
                break;
            }
        }
    }

    @Override
    protected void onRestart() {
        skipper = 0;
        allItemsCount = 0;
        allItemsLoaded = false;
        adapter.dataList.clear();
        retrieveData();
        super.onRestart();
    }

    private void bringUserRecipes() {
        RecipeLoader recipesLoader = new RecipeLoader(ApiClient.getInstance(accessToken).getService(), this, uidProfile, skipper);
        recipesLoader.getRecipesByUser().observe(this, this::onRecipesLoaded);
        recipesLoader.loadRecipesByUser();
    }

    private void bringRecipesLikedByUser() {
        RecipeLoader recipesLoader = new RecipeLoader(ApiClient.getInstance(accessToken).getService(), this, uidProfile, skipper);
        recipesLoader.getRecipesLikedByUser().observe(this, this::onRecipesLoaded);
        recipesLoader.loadRecipesLikedByUser();
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
        shimmer.stopShimmer();
        shimmer.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();

        if(adapter.dataList.size() != allItemsCount) {
            allItemsCount = adapter.dataList.size();
        } else {
            allItemsLoaded = true;
        }
    }

    private void bringUserFollowing() {
        UserLoader userLoader = new UserLoader(ApiClient.getInstance(accessToken).getService(), this, uidProfile, skipper);
        userLoader.getFollowingByUser().observe(this, this::onUsersLoaded);
        userLoader.loadFollowingByUser();
    }

    private void bringUserFollowers() {
        UserLoader userLoader = new UserLoader(ApiClient.getInstance(accessToken).getService(), this, uidProfile, skipper);
        userLoader.getFollowersToUser().observe(this, this::onUsersLoaded);
        userLoader.loadFollowersToUser();
    }

    private void onUsersLoaded(List<User> users) {
        if(adapter.getItemCount() > 0) {
            if(adapter.getItemViewType(adapter.getItemCount() - 1) != 0) {
                adapter.dataList.remove(adapter.getItemCount() - 1);
            } else if(adapter.getItemViewType(0) != 0) {
                adapter.dataList.remove(0);
            }
        }

        adapter.dataList.addAll(users);
        adapter.setLoaded();
        adapter.notifyDataSetChanged();

        if(adapter.dataList.size() != allItemsCount) {
            allItemsCount = adapter.dataList.size();
        } else {
            allItemsLoaded = true;
        }

        shimmer.stopShimmer();
        shimmer.setVisibility(View.GONE);
    }
}