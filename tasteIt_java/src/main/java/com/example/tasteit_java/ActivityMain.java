package com.example.tasteit_java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ActivityMain extends AppCompatActivity {

    private FloatingActionButton bCreate;
    private FragmentTransaction ft;
    //TEMPORAL GALERIA
    private GridView gvRecipes;
    private AdapterGridViewMain adapter;
    private RecyclerView rvRecipes;
    private AdapterRecyclerMain adapter2;
    private ArrayList<Recipe> listRecipes = new ArrayList<>();

    private User user;
    private MenuItem iProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //NEO4J
        /*
        iProfile = findViewById(R.id.iProfile);
        Bundle params = getIntent().getExtras();
        user = (User) params.getSerializable("user");
        iProfile.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //return false;
                Intent i = new Intent(ActivityMain.this, ActivityProfile.class);
                i.putExtra("username", user.getUsername());
                i.putExtra("email", user.getEmail());

                startActivity(i);
                return true;
            }
        });
        */

        //NEO4J

        //menu superior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //boton crear receta
        bCreate = findViewById(R.id.bCreate);
        bCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ActivityMain.this, ActivityNewRecipe.class));
            }
        });

        //fragment menu inferior
        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        FragmentMainMenu mainMenuFargment = new FragmentMainMenu();
        //comprobamos si existe
        if(savedInstanceState == null) {
            ft.add(R.id.fcMainMenu, mainMenuFargment,"main_menu");
            ft.commit();
        }

        //TEMPORAL - grid view
        for(int i = 0; i < 5; i++) {
            listRecipes.add(new Recipe("Pinneaple pizza","etc etc etc","chuck norris", "5.0", R.drawable.recipe_demo, new ArrayList<String>()));
        }

        gvRecipes = findViewById(R.id.gvRecipes);
        adapter = new AdapterGridViewMain(this, listRecipes);
        gvRecipes.setAdapter(adapter);

        gvRecipes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int posicion, long l) {
                Intent i = new Intent(ActivityMain.this, ActivityRecipe.class);
                i.putExtra("recipe", listRecipes.get(posicion));
                startActivity(i);
            }
        });

        //ya funciona, pero hace cosas raras en el scroll

        /*
        //TEMPORAL - recycler view
        rvRecipes = findViewById(R.id.rvRecipes);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvRecipes.setLayoutManager(layoutManager);
        adapter2 = new AdapterRecyclerMain(listRecipes);
        rvRecipes.setAdapter(adapter2);
        */

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
                //ojo que puede petar
                //NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;
            case R.id.iProfile:
                //NEO4J
                /*
                iProfile = findViewById(R.id.iProfile);
                Bundle params = getIntent().getExtras();
                user = (User) params.getSerializable("user");

                Intent i = new Intent(ActivityMain.this, ActivityProfile.class);
                i.putExtra("username", user.getUsername());
                i.putExtra("email", user.getEmail());

                startActivity(i);
                */
                //FIN NEO4J
                startActivity(new Intent(getApplicationContext(), ActivityProfile.class));
                return true;
            case R.id.iCloseSesion:
                signOut();
                return true;
        }
        return super.onOptionsItemSelected(item);
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