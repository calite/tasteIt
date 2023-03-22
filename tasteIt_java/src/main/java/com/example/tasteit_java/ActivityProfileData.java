package com.example.tasteit_java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.tasteit_java.adapters.AdapterGridViewMain;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ActivityProfileData extends AppCompatActivity {

    private GridView gvData;
    private AdapterGridViewMain adapter;
    public static ArrayList<Recipe> listRecipes = new ArrayList<>();
    private BdConnection connection;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_data);

        Bundle params = getIntent().getExtras();
        uid = params.getString("uid");

        connection = new BdConnection();

        listRecipes = connection.retrieveAllRecipesbyUid(uid);
        gvData = findViewById(R.id.gvData);
        adapter = new AdapterGridViewMain(this, listRecipes);
        gvData.setAdapter(adapter);

        gvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int posicion, long l) {
                Intent i = new Intent(getApplicationContext(), ActivityRecipe.class);
                i.putExtra("recipe", listRecipes.get(posicion));
                startActivity(i);
            }
        });
    }
}