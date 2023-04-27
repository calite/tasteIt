package com.example.tasteit_java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.tasteit_java.adapters.AdapterRecyclerProfileData;
import com.facebook.shimmer.ShimmerFrameLayout;

public class ActivityProfileData extends AppCompatActivity {
    private RecyclerView rvListData;
    private AdapterRecyclerProfileData adapter;
    private String uidProfile;
    private ShimmerFrameLayout shimmer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_data);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        shimmer = findViewById(R.id.shimmer);
        shimmer.startShimmer();

        Bundle params = getIntent().getExtras();
        uidProfile = params.getString("uid");
        int dataType = params.getInt("dataType");

        switch (dataType) {
            case 1: { //Recipes
                getSupportActionBar().setTitle("Recipes");
                break;
            }
            case 2: { //Followers
                getSupportActionBar().setTitle("Followers");
                break;
            }
            case 3: { //Following
                getSupportActionBar().setTitle("Following");
                break;
            }
            case 4: { //Following
                getSupportActionBar().setTitle("Recipes Liked");
                break;
            }
        }

        rvListData = findViewById(R.id.rvListData);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvListData.setLayoutManager(linearLayoutManager);

        adapter = new AdapterRecyclerProfileData(this, uidProfile, dataType, this, shimmer);
        rvListData.setAdapter(adapter);

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
}