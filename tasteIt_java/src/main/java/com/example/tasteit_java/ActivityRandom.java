package com.example.tasteit_java;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tasteit_java.adapters.AdapterGridViewMain;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.Utils;
import com.example.tasteit_java.fragments.FragmentRandom;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.logging.Logger;

public class ActivityRandom extends AppCompatActivity implements GestureDetector.OnGestureListener{
    private GridView gvRecipes;
    private AdapterGridViewMain adapter;
    private Button btnShuffle;

    private ArrayList<Recipe> recipeList;

    private ArrayList<Recipe> recipes;
    private FragmentContainerView fcRandom;
    private ArrayList<Recipe> listRecipes = new ArrayList<>();
    private GestureDetector gestureDetector;
    Logger log = Logger.getLogger(ActivityRandom.class.getName());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random);

        BdConnection app = new BdConnection();  //Instanciamos la conexion

        recipes = app.retrieveAllRecipes();
        gestureDetector = new GestureDetector(this);

        //menu superior
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        fcRandom = findViewById(R.id.fcRandom);
        fcRandom.setVisibility(View.INVISIBLE);

        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int recipeIndex = (int) (Math.random()*recipes.size());
                FragmentRandom fr = new FragmentRandom();
                Bundle arguments = new Bundle();
                arguments.putParcelable("recipe", recipes.get(recipeIndex));
                fr.setArguments(arguments);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fcRandom, fr);
                ft.commit();
                fcRandom.setVisibility(View.VISIBLE);
                btnShuffle.setVisibility(View.INVISIBLE);
                findViewById(R.id.textView11).setVisibility(View.INVISIBLE);
                /*
                MOVES THE BUTTOM
                ConstraintLayout cl = findViewById(R.id.cl);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(cl);
                constraintSet.connect(R.id.btnShuffle,ConstraintSet.TOP,R.id.cl,ConstraintSet.TOP,0);
                constraintSet.applyTo(cl);
                 */
            }
        });

    }


    //MENU superior
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);

        menu.getItem(1).setVisible(false);
        menu.getItem(2).setVisible(false);

        Bitmap originalBitmap = Utils.decodeBase64(new BdConnection().retrieveUserbyUid(Utils.getUserToken()).getImgProfile());
        BitmapDrawable roundedBitmapDrawable = new BitmapDrawable(getResources(), Utils.getRoundBitmapWithImage(originalBitmap));
        menu.getItem(0).setIcon(roundedBitmapDrawable);

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
                if(AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }else{AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);}
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
        if(recipes.size() != 0) {
            int recipeIndex = (int) (Math.random()*recipes.size());
            FragmentRandom fr = new FragmentRandom();
            Bundle arguments = new Bundle();
            arguments.putParcelable("recipe", recipes.get(recipeIndex));
            fr.setArguments(arguments);
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fcRandom, fr);
            ft.commit();

        }
        return true;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

}