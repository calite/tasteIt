package com.example.tasteit_java;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tasteit_java.ApiService.ApiClient;
import com.example.tasteit_java.ApiService.ApiRequests;
import com.example.tasteit_java.ApiService.ApiUser;
import com.example.tasteit_java.ApiService.RecipeId_Recipe_User;
import com.example.tasteit_java.adapters.AdapterFragmentProfile;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.OnItemNavSelectedListener;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.User;
import com.example.tasteit_java.clases.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.neo4j.driver.Query;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityProfile extends AppCompatActivity {
    private TabLayout tlUser;
    private ViewPager2 vpPaginator;
    private AdapterFragmentProfile adapter;
    private User userProfile;
    private TextView tvUserName, tvReciperCounter, tvFollowersCounter, tvFollowingCounter, tvLikesCounter;
    private ImageView ivUserPicture;
    private Button btnFollow;
    private ShimmerFrameLayout shimmer;
    private ConstraintLayout tagRecipe, tagFollowers, tagFollowing, tagLikes;
    private BdConnection connection;
    private String uid;
    private Boolean myProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        BottomNavigationView fcMainMenu = findViewById(R.id.fcMainMenu);
        fcMainMenu.setSelectedItemId(R.id.bHome);
        fcMainMenu.setOnItemSelectedListener(new OnItemNavSelectedListener(this));

        shimmer = findViewById(R.id.shimmer);
        shimmer.startShimmer();

        if (getIntent().getExtras() != null) {
            Bundle params = getIntent().getExtras();
            uid = params.getString("uid");
            if (!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(uid)) {
                myProfile = false;
            } else {
                myProfile = true;
            }
        } else {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            myProfile = true;
        }

        connection = new BdConnection();
        initializeViews();
        bringUser();

        //menu superior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");

        //bio, photo and comments Fragments
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

        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connection.isFollowing(FirebaseAuth.getInstance().getCurrentUser().getUid(), uid)) {
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
                i.putExtra("dataType", 1);
                startActivity(i);
            }
        });
        tagFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ActivityProfileData.class);
                i.putExtra("uid", uid);
                i.putExtra("dataType", 2);
                startActivity(i);
            }
        });
        tagFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ActivityProfileData.class);
                i.putExtra("uid", uid);
                i.putExtra("dataType", 3);
                startActivity(i);
            }
        });
        tagLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), ActivityProfileData.class);
                i.putExtra("uid", uid);
                i.putExtra("dataType", 4);
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
            if (connection.isFollowing(FirebaseAuth.getInstance().getCurrentUser().getUid(), uid)) {
                btnFollow.setText("UNFOLLOW");
            }
            btnFollow.setVisibility(View.VISIBLE);
            btnFollow.setEnabled(true);
        }
        vpPaginator = findViewById(R.id.vpPaginator);
        tlUser = findViewById(R.id.tlUser);

        tagRecipe = findViewById(R.id.tagRecipe);
        tagFollowers = findViewById(R.id.tagFollowers);
        tagFollowing = findViewById(R.id.tagFollowing);
        tagLikes = findViewById(R.id.tagLikes);
    }

    //Metodo para traer los datos del perfil
    private void retrieveData(String uid) {

        tvUserName.setText(userProfile.getUsername());
        //ivUserPicture.setImageBitmap(Utils.uriToBitmap(getApplicationContext(), userProfile.getImgProfile()));

        Picasso.with(this)
                .load("https://firebasestorage.googleapis.com/v0/b/tasteit-java.appspot.com/o/images%2F035d70df-1048-4c15-ba6a-c4d81d44a026?alt=media&token=d2c0ebf1-3b4e-40a4-9162-94fbc2070008")
                .into(ivUserPicture);

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
                //Toast.makeText(this, "Aqui finalizamos", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.iEditProfile:
                startActivity(new Intent(getApplicationContext(), ActivityEditProfile.class));
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
        finish();
    }

    //Cuando se cambia a otra actividad y se vuelve a esta (ya creada) actualizamos los datos
    @Override
    protected void onRestart() {
        super.onRestart();
        //bringUser();
        //adapter.updateFragments(userProfile.getBiography());
    }

    //carga de recetas asyncrona
    private class UserLoader {

        private final ApiRequests apiRequests;
        private final MutableLiveData<User> userLiveData;

        public UserLoader(ApiRequests apiRequests) {
            this.apiRequests = apiRequests;
            userLiveData = new MutableLiveData<>();
        }

        public LiveData<User> getUser() {
            return userLiveData;
        }

        public void loadUser() {
            apiRequests.getUserByToken(uid).enqueue(new Callback<ApiUser>() {
                @Override
                public void onResponse(Call<ApiUser> call, Response<ApiUser> response) {
                    if (response.isSuccessful()) {
                        ApiUser UserApi = response.body();
                        User users = null;
                        User temp = new User(
                                UserApi.getUser().getUsername(),
                                UserApi.getUser().getBiography(),
                                UserApi.getUser().getImgProfile(),
                                UserApi.getUser().getToken()
                        );
                        users = temp;
                        userLiveData.postValue(users);
                    } else {
                        Toast.makeText(ActivityProfile.this, "Primer error", Toast.LENGTH_SHORT).show();
                        // La solicitud no fue exitosa
                    }
                }

                @Override
                public void onFailure(Call<ApiUser> call, Throwable t) {
                    // Hubo un error en la solicitud
                    Toast.makeText(ActivityProfile.this, "Failed to load data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void onUserLoaded(User users) {
        userProfile = users;
        retrieveData(uid);
        adapter = new AdapterFragmentProfile(getSupportFragmentManager(), getLifecycle(), uid, myProfile);
        vpPaginator.setAdapter(adapter);

        shimmer.stopShimmer();
        shimmer.hideShimmer();
    }

    private void bringUser() {
        //olvidamos asynctask y metemos lifecycle, que es mas actual y esta mejor optimizado
        UserLoader userLoader = new UserLoader(ApiClient.getInstance().getService());
        userLoader.getUser().observe(this, this::onUserLoaded);
        userLoader.loadUser();
    }

}