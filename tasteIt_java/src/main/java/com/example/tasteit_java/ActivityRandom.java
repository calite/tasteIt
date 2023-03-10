package com.example.tasteit_java;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ActivityRandom extends AppCompatActivity implements GestureDetector.OnGestureListener{
    private GridView gvRecipes;
    private AdapterGridViewMain adapter;
    private Button btnShuffle;

    private ArrayList<Recipe> recipeList;

    private ArrayList<Recipe> listRecipes = new ArrayList<>();
    private GestureDetector gestureDetector;
    Logger log = Logger.getLogger(ActivityRandom.class.getName());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random);

        BdConnection app = new BdConnection();  //Instanciamos la conexion

        ArrayList<Recipe> recipes = app.retrieveAllRecipes();
        recipeList = recipes;
        gvRecipes = findViewById(R.id.gvRecipes);
        adapter = new AdapterGridViewMain(this, listRecipes);
        gvRecipes.setAdapter(adapter);

        //menu superior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Random");
        //fragment menu inferior
        /*
        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        FragmentMainMenu mainMenuFargment = new FragmentMainMenu();
        //comprobamos si existe
        if(savedInstanceState == null) {
            ft.add(R.id.fcMainMenu, mainMenuFargment);
            ft.commit();
        }
        */
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

        gestureDetector = new GestureDetector(this);

        gvRecipes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), ActivityRecipe.class);
                i.putExtra("recipe",listRecipes.get(0));
                Bundle params = getIntent().getExtras();
                User user = (User) params.getSerializable("user");
                i.putExtra("user",user);
                startActivity(i);
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

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        if(listRecipes.size() != 0) {
            int recipeIndex = (int) (Math.random() * recipeList.size());
            log.log(Level.INFO, "" + recipeIndex);
            listRecipes.clear();
            listRecipes.add(recipeList.get(recipeIndex));
            adapter.notifyDataSetChanged();
        }
        return true;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}