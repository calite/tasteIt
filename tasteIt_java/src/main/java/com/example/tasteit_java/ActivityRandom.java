package com.example.tasteit_java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.Recipe;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ActivityRandom extends AppCompatActivity {
    private GridView gvRecipes;
    private AdapterGridViewMain adapter;
    private Button btnShuffle;
    Logger log = Logger.getLogger(ActivityMain.class.getName());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random);

        //NEO4J
        String uri = "neo4j+s://dc95b24b.databases.neo4j.io"; //URL conexion Neo4j
        String user = "neo4j";
        String pass = "sBQ6Fj2oXaFltjizpmTDhyEO9GDiqGM1rG-zelf17kg"; //PDTE CIFRAR
        BdConnection app = new BdConnection(uri, user, pass);  //Instanciamos la conexion
        //FIN NEO
        ArrayList<Recipe> recipes = app.retrieveAllRecipes();
        ArrayList<Recipe> listRecipes = new ArrayList<>();
        gvRecipes = findViewById(R.id.gvRecipes);
        adapter = new AdapterGridViewMain(this, listRecipes);
        gvRecipes.setAdapter(adapter);

        //menu superior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Random");
        //fragment menu inferior
        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        FragmentMainMenu mainMenuFargment = new FragmentMainMenu();
        //comprobamos si existe
        if(savedInstanceState == null) {
            ft.add(R.id.fcMainMenu, mainMenuFargment);
            ft.commit();
        }

        btnShuffle = findViewById(R.id.btnShuffle);
        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int recipeIndex = (int) (Math.random()*recipes.size());
                log.log(Level.INFO, ""+recipeIndex);
                listRecipes.clear();
                listRecipes.add(recipes.get(recipeIndex));
                adapter.notifyDataSetChanged();
            }
        });
    }

    //MENU superior
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //END MENU superior
    //LOGOUT
    public void callSignOut(View view){
        signOut();
    }
    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        startActivity (new Intent(this, ActivityLogin.class));
    }
}