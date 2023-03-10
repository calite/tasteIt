package com.example.tasteit_java;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.User;
import com.example.tasteit_java.clases.Utils;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.neo4j.driver.Query;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.types.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActivityProfile extends AppCompatActivity {


    private FragmentTransaction ft;
    private TabLayout tlUser;
    private ViewPager2 vpPaginator;

    private User user;
    private TextView tvUserName, tvReciperCounter, tvFollowersCounter, tvFollowingCounter, tvLikesCounter;
    private ImageView ivUserPicture;
    private BdConnection connection;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeViews();

        if(getIntent().getExtras() != null) {
            Bundle params = getIntent().getExtras();
            uid = params.getString("uid");
        } else {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            uid = firebaseUser.getUid();
        }

        retrieveData(uid);

        //menu superior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");

        //bio, photo and comments Fragments
        vpPaginator.setAdapter(new AdapterFragmentProfile(getSupportFragmentManager(),getLifecycle(), user));

        tlUser.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vpPaginator.setCurrentItem(tab.getPosition());
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
    }

    private void initializeViews() {
        tvUserName = findViewById(R.id.tvUserName);
        tvReciperCounter = findViewById(R.id.tvReciperCounter);
        tvFollowersCounter = findViewById(R.id.tvFollowersCounter);
        tvFollowingCounter = findViewById(R.id.tvFollowingCounter);
        tvLikesCounter = findViewById(R.id.tvLikesCounter);

        ivUserPicture = findViewById(R.id.ivUserPicture);

        vpPaginator = findViewById(R.id.vpPaginator);
        tlUser = findViewById(R.id.tlUser);
    }

    private void retrieveData(String uid) {
        connection = new BdConnection();

        user = connection.retrieveUserbyUid(uid);

        tvUserName.setText(user.getUsername());

        Bitmap bitmap = Utils.decodeBase64(user.getImgProfile());
        ivUserPicture.setImageBitmap(bitmap);

        user.setUserRecipes(connection.retrieveAllRecipesbyUid(uid));
        user.setUserComments(connection.retrieveCommentsbyUid(uid));

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