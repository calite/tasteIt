package com.example.tasteit_java;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tasteit_java.clases.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ActivityProfile extends AppCompatActivity {


    private FragmentTransaction ft;
    private TabLayout tlUser;
    private ViewPager2 vpPaginator;

    private User user;
    private TextView tvUserName;
    private TextView tvBiography;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //NEO4J
        Bundle params = getIntent().getExtras();
        user = (User) params.getSerializable("user");
        tvUserName = findViewById(R.id.tvUserName);
        tvUserName.setText(user.getUsername());
        //tvBiography = findViewById(R.id.tvBiography);
        //tvBiography.setText(user.getBiography());
        //FIN NEO4J

        //menu superior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");

        //bio, photo and video Fragments
        vpPaginator = findViewById(R.id.vpPaginator);
        tlUser = findViewById(R.id.tlUser);

        vpPaginator.setAdapter(new AdapterFragmentProfile(getSupportFragmentManager(),getLifecycle()));

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