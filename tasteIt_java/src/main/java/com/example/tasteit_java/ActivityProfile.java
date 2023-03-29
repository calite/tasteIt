package com.example.tasteit_java;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tasteit_java.adapters.AdapterFragmentProfile;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.User;
import com.example.tasteit_java.clases.Utils;
import com.example.tasteit_java.fragments.FragmentMainMenu;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import org.neo4j.driver.Query;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

import java.util.ArrayList;
import java.util.HashMap;

public class ActivityProfile extends AppCompatActivity {

    private FragmentTransaction ft;
    private TabLayout tlUser;
    private ViewPager2 vpPaginator;
    private AdapterFragmentProfile adapter;
    private User userProfile;
    private TextView tvUserName, tvReciperCounter, tvFollowersCounter, tvFollowingCounter, tvLikesCounter;
    private ImageView ivUserPicture;
    private Button btnFollow;
    private ConstraintLayout tagRecipe;
    private BdConnection connection;
    private String uid;
    private Boolean myProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if(getIntent().getExtras() != null) {
            Bundle params = getIntent().getExtras();
            uid = params.getString("uid");
            myProfile = false;
        } else {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            myProfile = true;
        }

        connection = new BdConnection();
        initializeViews();
        new TaskLoadUser().execute();

        //menu superior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");

        //bio, photo and comments Fragments
        tlUser.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vpPaginator.setCurrentItem(tab.getPosition());
                Toast.makeText(ActivityProfile.this, "se ejecuta 2!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        vpPaginator.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                //super.onPageSelected(position);
                tlUser.selectTab(tlUser.getTabAt(position));
                Toast.makeText(ActivityProfile.this, "se ejecuta 1!", Toast.LENGTH_SHORT).show();
            }
        });

        //fragment menu inferior
        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        FragmentMainMenu mainMenuFragment = new FragmentMainMenu();
        //comprobamos si existe
        if(savedInstanceState == null) {
            ft.add(R.id.fcMainMenu, mainMenuFragment,"main_menu");
            ft.commit();
        }

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(connection.isFollowing(FirebaseAuth.getInstance().getCurrentUser().getUid(), uid)) {
                    connection.unFollowUser(FirebaseAuth.getInstance().getCurrentUser().getUid(), uid);
                    btnFollow.setText("FOLLOW");
                } else {
                    connection.followUser(FirebaseAuth.getInstance().getCurrentUser().getUid(), uid);
                    btnFollow.setText("UNFOLLOW");
                }
            }
        });
        tagRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ActivityProfileData.class);
                i.putExtra("uid", uid);
                startActivity(i);
            }
        });
    }

    //Metodo para instanciar los elementos de la UI
    private void initializeViews() {
        tvUserName = findViewById(R.id.tvUserName);
        tvReciperCounter = findViewById(R.id.tvReciperCounter);
        tvFollowersCounter = findViewById(R.id.tvFollowersCounter);
        tvFollowingCounter = findViewById(R.id.tvFollowingCounter);
        tvLikesCounter = findViewById(R.id.tvLikesCounter);

        ivUserPicture = findViewById(R.id.ivUserPicture);

        btnFollow = findViewById(R.id.btnFollow);
        if (myProfile) {
            btnFollow.setVisibility(View.INVISIBLE);
            btnFollow.setEnabled(false);
        } else {
            if(connection.isFollowing(FirebaseAuth.getInstance().getCurrentUser().getUid(), uid)) {
                btnFollow.setText("UNFOLLOW");
            }
            btnFollow.setVisibility(View.VISIBLE);
            btnFollow.setEnabled(true);
        }

        vpPaginator = findViewById(R.id.vpPaginator);
        tlUser = findViewById(R.id.tlUser);

        tagRecipe = findViewById(R.id.tagRecipe);
    }

    //Metodo para traer los datos del perfil
    private void retrieveData(String uid) {

        tvUserName.setText(userProfile.getUsername());

        Bitmap bitmap = Utils.decodeBase64(userProfile.getImgProfile());
        ivUserPicture.setImageBitmap(bitmap);

        //user.setUserRecipes(connection.retrieveAllRecipesbyUid(uid));
        //user.setUserComments(connection.retrieveCommentsbyUid(uid));

        Session session = connection.openSession();

        Query query = new Query("MATCH (n1:User)-[:Created]-(n2:Recipe) WHERE n1.token = '" + uid + "' RETURN COUNT(n2);");
        Result result = session.run(query);

        String counter = String.valueOf(result.single().get(0).asInt());
        tvReciperCounter.setText(counter);

        query = new Query("MATCH (n1:User)-[:Following]->(n2:User) WHERE n1.token = '" + uid + "' RETURN COUNT(n2);");
        result = session.run(query);

        counter = String.valueOf(result.single().get(0).asInt());
        tvFollowingCounter.setText(counter);

        query = new Query("MATCH (n1:User)<-[:Following]-(n2:User) WHERE n1.token = '" + uid + "' RETURN COUNT(n2);");
        result = session.run(query);

        counter = String.valueOf(result.single().get(0).asInt());
        tvFollowersCounter.setText(counter);

        query = new Query("MATCH (n1:User)-[:Liked]->(n2:Recipe) WHERE n1.token = '" + uid + "' RETURN COUNT(n2);");
        result = session.run(query);

        counter = String.valueOf(result.single().get(0).asInt());
        tvLikesCounter.setText(counter);

        connection.closeSession(session);
    }


    //MENU superior
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        if (myProfile) {
            menu.getItem(0).setVisible(true);
        } else {
            menu.getItem(0).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.iEditProfile:
                startActivity(new Intent(getApplicationContext(), ActivityEditProfile.class));
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
        finish();
    }

    //Cuando se cambia a otra actividad y se vuelve a esta (ya creada) actualizamos los datos
    @Override
    protected void onRestart() {
        super.onRestart();
        retrieveData(uid);
        adapter.updateFragments(userProfile.getBiography());
    }

    //Tareas asincronas para la carga de los datos del usuario (peticiones a la bbdd)
    class TaskLoadUser extends AsyncTask<User, Void,User> {
        @Override
        protected void onPreExecute() {

        }
        @Override
        protected User doInBackground(User... hashMaps) {
            return connection.retrieveAllUserbyUid(uid);
        }
        @Override
        protected void onPostExecute(User user) {
            //super.onPostExecute(recipes);
            userProfile = user;
            retrieveData(uid);
            if(myProfile) {
                adapter = new AdapterFragmentProfile(getSupportFragmentManager(),getLifecycle(), userProfile, myProfile);
            } else {
                adapter = new AdapterFragmentProfile(getSupportFragmentManager(),getLifecycle(), userProfile, myProfile, uid);
            }

            vpPaginator.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

}