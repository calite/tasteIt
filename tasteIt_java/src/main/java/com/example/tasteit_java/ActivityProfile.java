package com.example.tasteit_java;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tasteit_java.ApiService.ApiClient;
import com.example.tasteit_java.ApiGetters.UserLoader;
import com.example.tasteit_java.adapters.AdapterFragmentProfile;
import com.example.tasteit_java.clases.OnItemNavSelectedListener;
import com.example.tasteit_java.clases.SharedPreferencesSaved;
import com.example.tasteit_java.clases.User;
import com.example.tasteit_java.clases.Utils;
import com.example.tasteit_java.ApiRequest.UserCommentRequest;
import com.example.tasteit_java.ApiRequest.UserFollowRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityProfile extends AppCompatActivity {
    private TabLayout tlUser;
    private ViewPager2 vpPaginator;
    private AdapterFragmentProfile adapter;
    private User userProfile;
    private String accessToken;
    private TextView tvUserName, tvReciperCounter, tvFollowersCounter, tvFollowingCounter, tvLikesCounter;
    private ImageView ivUserPicture;
    private Button btnFollow;
    private ShimmerFrameLayout shimmer;
    private ConstraintLayout tagRecipe, tagFollowers, tagFollowing, tagLikes;
    private String uid;
    private Boolean myProfile;

    private FloatingActionButton bComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        shimmer = findViewById(R.id.shimmer);
        shimmer.startShimmer();

        accessToken = Utils.getUserAcessToken();

        if (getIntent().getExtras() != null) {
            uid = getIntent().getExtras().getString("uid");
            if (!Utils.getUserToken().equals(uid)) {
                myProfile = false;
                getIsFollowing(Utils.getUserToken(), uid);
            } else {
                myProfile = true;
                initializeViews();
                bringUser();
            }
        } else {
            uid = Utils.getUserToken();
            myProfile = true;
            initializeViews();
            bringUser();
        }

        //menu superior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");
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
        bComment = findViewById(R.id.bComment);
        if (myProfile) {
            btnFollow.setVisibility(View.INVISIBLE);
            btnFollow.setEnabled(false);
        } else {
            btnFollow.setVisibility(View.VISIBLE);
            btnFollow.setEnabled(true);
        }
        vpPaginator = findViewById(R.id.vpPaginator);
        tlUser = findViewById(R.id.tlUser);

        tagRecipe = findViewById(R.id.tagRecipe);
        tagFollowers = findViewById(R.id.tagFollowers);
        tagFollowing = findViewById(R.id.tagFollowing);
        tagLikes = findViewById(R.id.tagLikes);

        tlUser.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() != vpPaginator.getCurrentItem()) {
                    if (tab.getPosition() == 2 && vpPaginator.getCurrentItem() != 2) {
                        if (myProfile) {
                            bComment.setVisibility(View.GONE);
                            bComment.setEnabled(false);
                        } else {
                            bComment.setVisibility(View.VISIBLE);
                            bComment.setEnabled(true);
                        }
                    } else if (tab.getPosition() != 2 && vpPaginator.getCurrentItem() == 2) {
                        bComment.setVisibility(View.GONE);
                    }
                }
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
                UserFollowRequest userFollowRequest = new UserFollowRequest(Utils.getUserToken(), uid);
                ApiClient apiClient = ApiClient.getInstance(accessToken);
                Call<Void> call = apiClient.getService().followUser(userFollowRequest);
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            getIsFollowing(Utils.getUserToken(), uid);
                        } else {
                            // Handle the error
                            Log.e("API_ERROR", "Response error: " + response.code() + " " + response.message());
                            Toast.makeText(ActivityProfile.this, "bad!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        // Handle the error
                        Toast.makeText(ActivityProfile.this, "bad!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        bComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View rate = View.inflate(ActivityProfile.this, R.layout.alert_dialog_comment_rate, null);
                RatingBar rbRating = rate.findViewById(R.id.rbRating);
                TextView textView12 = rate.findViewById(R.id.textView12);
                EditText etComment = rate.findViewById(R.id.etCommentRate);
                Button btnSend = rate.findViewById(R.id.btnSend);
                Button btnCancel = rate.findViewById(R.id.btnCancel);

                rbRating.setVisibility(View.GONE);
                rbRating.setEnabled(false);
                textView12.setVisibility(View.GONE);
                textView12.setEnabled(false);

                Dialog dialog = new Dialog(ActivityProfile.this);
                dialog.setContentView(rate);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                Window window = dialog.getWindow();
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.gravity = Gravity.CENTER;
                window.setAttributes(lp);
                window.setWindowAnimations(Animation.INFINITE);

                btnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!String.valueOf(etComment.getText()).trim().equals("")) {
                            UserCommentRequest userCommentRequest = new UserCommentRequest(Utils.getUserToken(), uid, String.valueOf(etComment.getText()));
                            ApiClient apiClient = ApiClient.getInstance(accessToken);
                            Call<Void> call = apiClient.getService().commentUser(userCommentRequest);
                            call.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        getIsFollowing(Utils.getUserToken(), uid);
                                        dialog.cancel();
                                        Intent intent = getIntent();
                                        finish();
                                        startActivity(intent);
                                    } else {
                                        // Handle the error
                                        Log.e("API_ERROR", "Response error: " + response.code() + " " + response.message());
                                        Toast.makeText(ActivityProfile.this, "bad!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    // Handle the error
                                    Toast.makeText(ActivityProfile.this, "bad!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(ActivityProfile.this, "You must write a comment to continue!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }
                });

                dialog.show();
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

    //MENU superior
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);

        BottomNavigationView fcMainMenu = findViewById(R.id.fcMainMenu);
        fcMainMenu.setSelectedItemId(R.id.bHome);
        fcMainMenu.setOnItemSelectedListener(new OnItemNavSelectedListener(this));

        if (myProfile) {
            menu.getItem(0).setVisible(true);
        } else {
            menu.getItem(0).setVisible(false);
        }

        menu.getItem(1).setVisible(false); //Peta el signout
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                onBackPressed();
                return true;
            case R.id.iEditProfile:
                startActivity(new Intent(getApplicationContext(), ActivityEditProfile.class));
                return true;
            case R.id.iCloseSesion:
                //signOut();
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
    public void callSignOut(View view) {
        //signOut();
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        SharedPreferences.Editor editor = new SharedPreferencesSaved(this).getEditer();
        editor.remove("uid");
        editor.remove("accessToken");
        editor.commit();

        startActivity(new Intent(this, ActivityLogin.class));
        finish();
    }

    //Cuando se cambia a otra actividad y se vuelve a esta (ya creada) actualizamos los datos
    @Override
    protected void onRestart() {
        if (myProfile) {
            initializeViews();
            bringUser();
        } else {
            getIsFollowing(Utils.getUserToken(), uid);
        }
        super.onRestart();
    }

    //Carga de usuario asyncrona
    private void bringUser() {
        UserLoader userLoader = new UserLoader(ApiClient.getInstance(accessToken).getService(), this, uid);
        userLoader.getAllUser().observe(this, this::onUserLoaded);
        userLoader.loadAllUser();
    }

    private void onUserLoaded(HashMap<String, Object> userInfo) {
        if (userInfo.size() == 5) {
            userProfile = (User) userInfo.get("user");

            tvUserName.setText(userProfile.getUsername());

            try {
                Picasso.with(this)
                        .load(userProfile.getImgProfile())
                        .into(ivUserPicture);
            } catch (IllegalArgumentException e) {
                Log.e("Image Error", "Error loading profile image");
            }

            String counter = String.valueOf(userInfo.get("recipes"));
            tvReciperCounter.setText(counter);

            counter = String.valueOf(userInfo.get("followers"));
            tvFollowersCounter.setText(counter);

            counter = String.valueOf(userInfo.get("following"));
            tvFollowingCounter.setText(counter);

            counter = String.valueOf(userInfo.get("liked"));
            tvLikesCounter.setText(counter);

            if (adapter == null) {
                adapter = new AdapterFragmentProfile(getSupportFragmentManager(), getLifecycle(), uid, myProfile);
                vpPaginator.setAdapter(adapter);
                shimmer.stopShimmer();
                shimmer.hideShimmer();
            }

            adapter.notifyDataSetChanged();
        } else {
            bringUser();
        }
    }

    private void getIsFollowing(String sender_token, String receiver_token) {
        UserLoader isFollowing = new UserLoader(ApiClient.getInstance(Utils.getUserAcessToken()).getService(), this, sender_token, receiver_token);
        isFollowing.getIsFollow().observe(this, this::onFollowingLoaded);
        isFollowing.loadIsFollow();
    }

    private void onFollowingLoaded(Boolean isFollow) {
        initializeViews();
        bringUser();

        if (isFollow) {
            btnFollow.setText("UNFOLLOW");
        } else {
            btnFollow.setText("FOLLOW");
        }
    }
}