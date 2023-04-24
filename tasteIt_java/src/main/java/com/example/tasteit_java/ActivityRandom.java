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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tasteit_java.adapters.AdapterGridViewMain;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.OnItemNavSelectedListener;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.Utils;
import com.example.tasteit_java.fragments.FragmentRandom;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.logging.Logger;

public class ActivityRandom extends AppCompatActivity implements View.OnTouchListener {
    private Button btnShuffle;
    private float startX;
    private ArrayList<Recipe> recipes;
    private FragmentContainerView fcRandom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random);

        BottomNavigationView fcMainMenu = findViewById(R.id.fcMainMenu);
        fcMainMenu.setSelectedItemId(R.id.bRandom);
        fcMainMenu.setOnItemSelectedListener(new OnItemNavSelectedListener(this));
        BdConnection app = new BdConnection();  //Instanciamos la conexion

        recipes = app.retrieveAllRecipes();

        getSupportActionBar().setTitle("Random");
        btnShuffle = findViewById(R.id.btnShuffle);

        fcRandom = findViewById(R.id.fcRandom);
        fcRandom.setVisibility(View.INVISIBLE);

        fcRandom.setOnTouchListener(this);

        btnShuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int recipeIndex = (int) (Math.random() * recipes.size());
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

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Se ha iniciado el toque, guarda la posición inicial
                startX = event.getX();
                break;

            case MotionEvent.ACTION_UP:
                // Se ha liberado el toque, compara la posición inicial con la posición final
                float endX = event.getX();
                float deltaX = endX - startX;

                // Si el desplazamiento horizontal es mayor a cierto umbral, realiza la acción de scroll
                if (Math.abs(deltaX) > 100) {
                    if (deltaX > 0) {
                        // Desplazamiento hacia la derecha, selecciona la pestaña anterior

                    } else {
                        // Desplazamiento hacia la izquierda, selecciona la pestaña siguiente
                        if (recipes.size() != 0) {
                            int recipeIndex = (int) (Math.random() * recipes.size());
                            FragmentRandom fr = new FragmentRandom();
                            Bundle arguments = new Bundle();
                            arguments.putParcelable("recipe", recipes.get(recipeIndex));
                            fr.setArguments(arguments);
                            FragmentManager fm = getSupportFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            ft.replace(R.id.fcRandom, fr);
                            ft.commit();
                        }
                    }
                }
                break;
        }
        return true;
    }
}