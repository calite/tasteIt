package com.example.tasteit_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.tasteit_java.adapters.AdapterGridViewMain;
import com.example.tasteit_java.adapters.AdapterRecyclerCommentsProfile;
import com.example.tasteit_java.adapters.AdapterRecyclerProfileData;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ActivityProfileData extends AppCompatActivity {
    private RecyclerView rvListData;
    private AdapterRecyclerProfileData adapter;
    private String uidProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_data);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle params = getIntent().getExtras();
        uidProfile = params.getString("uid");
        int dataType = params.getInt("dataType");

        switch (dataType) {
            case 1: { //Recipes
                getSupportActionBar().setTitle("My Recipes");
                break;
            }
            case 2: { //Followers
                getSupportActionBar().setTitle("My Followers");
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
        adapter = new AdapterRecyclerProfileData(this, uidProfile, dataType, this);
        rvListData.setAdapter(adapter);

        rvListData.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration divider = new DividerItemDecoration(rvListData.getContext(), DividerItemDecoration.VERTICAL);
        rvListData.addItemDecoration(divider);

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