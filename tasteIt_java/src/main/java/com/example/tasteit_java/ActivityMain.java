package com.example.tasteit_java;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasteit_java.adapters.AdapterGridViewMain;
import com.example.tasteit_java.adapters.AdapterRecyclerMain;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.Recipe;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ActivityMain extends AppCompatActivity {

    private FloatingActionButton bCreate;
    //TEMPORAL GALERIA
    private GridView gvRecipes;
    private AdapterGridViewMain adapter;
    private RecyclerView rvRecipes;
    private AdapterRecyclerMain adapter2;
    public static ArrayList<Recipe> listRecipes = new ArrayList<>();
    private ProgressBar pgMain;

    //neo
    private BdConnection app;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //menu superior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        app = new BdConnection();  //Instanciamos la conexion

        pgMain = findViewById(R.id.pbMain);

        //boton crear receta
        bCreate = findViewById(R.id.bCreate);
        bCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ActivityMain.this, ActivityNewRecipe.class);
                startActivity(i);
            }
        });
        //recoger token usuario firebase
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();

        new TaskLoadRecipes().execute(listRecipes); //carga de recetas async

        gvRecipes = findViewById(R.id.gvRecipes);

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

        gvRecipes.setOnScrollListener(new AbsListView.OnScrollListener(){
            int currentScrollState, currentVisibleItemCount, currentFirstVisibleItem, currentTotalItemCount, mLastFirstVisibleItem;
            boolean canScrollV, scrollingUp;
            @Override
            public void onScrollStateChanged (AbsListView view,int scrollState){
                 currentScrollState = scrollState;

                if (scrollingUp && !canScrollV) {
                    if (currentFirstVisibleItem == 0) {

                        listRecipes = app.retrieveAllRecipes();
                        Toast.makeText(ActivityMain.this, "Refreshing...", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                        scrollingUp = false;
                    }
                }
            }

            @Override
            public void onScroll (AbsListView view,int firstVisibleItem, int visibleItemCount,
                                  int totalItemCount){
                canScrollV = gvRecipes.canScrollVertically(-1);
                if(mLastFirstVisibleItem < firstVisibleItem){
                    // Scrolling down
                    scrollingUp = false;
                }
                if(mLastFirstVisibleItem > firstVisibleItem){
                    // scrolling up
                    scrollingUp = true;
                }
                mLastFirstVisibleItem = firstVisibleItem;
                currentFirstVisibleItem = firstVisibleItem;
                currentVisibleItemCount = visibleItemCount;
                currentTotalItemCount = totalItemCount;
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
                //ojo que puede petar
                //NavUtils.navigateUpFromSameTask(this);
                onBackPressed();
                return true;
            case R.id.iProfile:
                //NEO4J
                Intent i = new Intent(ActivityMain.this, ActivityProfile.class);
                startActivity(i);
                //FIN NEO4J

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
        startActivity (new Intent(this, ActivityLogin.class));
    }

    //carga de recetas asyncrona
    class TaskLoadRecipes extends AsyncTask<ArrayList<Recipe>, Void,ArrayList<Recipe>> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected ArrayList<Recipe> doInBackground(ArrayList<Recipe>... arrayLists) {
            return app.retrieveAllRecipes();
        }

        @Override
        protected void onPostExecute(ArrayList<Recipe> recipes) {
            //super.onPostExecute(recipes);

            pgMain.setVisibility(View.GONE);

            listRecipes = recipes;

            adapter = new AdapterGridViewMain(getApplicationContext(), listRecipes);
            gvRecipes.setAdapter(adapter);

            adapter.notifyDataSetChanged();
        }
    }
}

