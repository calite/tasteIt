package com.example.tasteit_java;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tasteit_java.ApiService.ApiClient;
import com.example.tasteit_java.ApiUtils.UserLoader;
import com.example.tasteit_java.clases.SharedPreferencesSaved;
import com.example.tasteit_java.clases.User;
import com.example.tasteit_java.clases.Utils;
import com.example.tasteit_java.request.UserEditRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityEditProfile extends AppCompatActivity {
    private ImageButton ibPickPhoto;
    private ImageView ivProfilePhoto;
    private TextView tvUsername, tvConfirmPassword, optChangePass;
    private EditText etUsername, etEmail, etNewPassword, etConfirmPassword, etBiography, etOldPassword;
    private Button btnSave;
    private String accessToken;
    private String uid;
    private User user;
    private Uri newFilePath;
    private Uri lastFileUrl;
    private ShimmerFrameLayout shimmer;
    private ConstraintLayout constraintLayout, clPassword;

    private ConstraintSet constraintSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        shimmer = findViewById(R.id.shimmer);
        shimmer.startShimmer();

        accessToken = new SharedPreferencesSaved(this).getSharedPreferences().getString("accessToken", "null");
        uid = new SharedPreferencesSaved(this).getSharedPreferences().getString("uid", "null");

        initializeViews();
        bringUser();

        //Menu superior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");
    }

    //Metodo para instanciar los elementos de la UI
    private void initializeViews() {
        ibPickPhoto = findViewById(R.id.ibPickPhoto);
        ivProfilePhoto = findViewById(R.id.ivProfilePhoto);

        tvUsername = findViewById(R.id.tvUsername);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etNewPassword = findViewById(R.id.etNewPassword);
        etOldPassword = findViewById(R.id.etOldPassword);
        etBiography = findViewById(R.id.etBiography);
        tvConfirmPassword = findViewById(R.id.tvConfirmPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSave = findViewById(R.id.btnSave);
        optChangePass = findViewById(R.id.optChangePass);
        clPassword = findViewById(R.id.clPassword);

        optChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clPassword.getVisibility() != View.VISIBLE) {
                    clPassword.setVisibility(View.VISIBLE);

                    constraintLayout = findViewById(R.id.mainLayout);
                    constraintSet = new ConstraintSet();
                    constraintSet.clone(constraintLayout);

                    constraintSet.connect(btnSave.getId(), ConstraintSet.TOP, clPassword.getId(), ConstraintSet.BOTTOM, 30);
                    constraintSet.applyTo(constraintLayout);
                } else {
                    clPassword.setVisibility(View.INVISIBLE);

                    constraintLayout = findViewById(R.id.mainLayout);
                    constraintSet = new ConstraintSet();
                    constraintSet.clone(constraintLayout);

                    constraintSet.connect(btnSave.getId(), ConstraintSet.TOP, optChangePass.getId(), ConstraintSet.BOTTOM, 30);
                    constraintSet.applyTo(constraintLayout);
                }
            }
        });

        //Cambiar foto de perfil
        PopupMenu.OnMenuItemClickListener popupListener = new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.iCamera: {
                        Utils.takePicture(ActivityEditProfile.this);
                        break;
                    }
                    case R.id.iGallery: {
                        Utils.selectImageFromMedia(ActivityEditProfile.this);
                        break;
                    }
                }
                return false;
            }
        };

        ibPickPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(ActivityEditProfile.this, view);
                MenuInflater menuInflater = popupMenu.getMenuInflater();
                menuInflater.inflate(R.menu.change_image_from, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(popupListener);
                popupMenu.show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldPassword = etOldPassword.getText().toString();
                String newPassword = etNewPassword.getText().toString();
                String ConfPass = etConfirmPassword.getText().toString();

                if (newPassword.length() >= 8 && newPassword.equals(ConfPass) && oldPassword.length() > 0 && newFilePath != null) {
                    changePassword(oldPassword, newPassword);
                    Toast.makeText(ActivityEditProfile.this, "Updateo password", Toast.LENGTH_SHORT).show();
                    uploadImage(newFilePath);
                } else if (newPassword.length() >= 8 && newPassword.equals(ConfPass) && oldPassword.length() > 0 && newFilePath == null) {
                    saveDataUser(null);
                } else if (oldPassword.length() == 0 && newFilePath != null) {
                    uploadImage(newFilePath);
                } else if (oldPassword.length() == 0 && newFilePath == null) {
                    saveDataUser(null);
                } else {
                    Toast.makeText(getApplicationContext(), "Password must match and be equal or longer than 8 characters", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void changePassword(String oldPassword, String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String email = user.getEmail();
        AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(ActivityEditProfile.this, "Something went wrong. Please try again later", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ActivityEditProfile.this, "Data and password successfully modified", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(ActivityEditProfile.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Metodo para traer los datos del perfil
    private void retrieveData() {
        tvUsername.setText(user.getUsername());
        etUsername.setText(user.getUsername());

        etBiography.setText(user.getBiography());
        etEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        try {
            Picasso.with(this)
                    .load(user.getImgProfile())
                    .into(ivProfilePhoto);
        } catch (IllegalArgumentException e) {
            Log.e("Image Error", "Error loading profile image");
        }

        lastFileUrl = Uri.parse(user.getImgProfile());
        newFilePath = null;

        shimmer.stopShimmer();
        shimmer.hideShimmer();
    }
    //END Recogida de datos

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            newFilePath = data.getData();
            Utils.onActivityResult(this, requestCode, resultCode, data, ivProfilePhoto);
        }
        /*if(requestCode == 202) {
            if(data.getExtras() != null) {
                Uri uri = data.getData();
                Toast.makeText(this, "ahora esta?", Toast.LENGTH_SHORT).show();

                newFilePath = uri;

                Bitmap photo = (Bitmap) data.getExtras().get("data");
                ivProfilePhoto.setImageBitmap(photo);
            }
        }*/
    }
    //END PHOTO PICKER

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.iCloseSesion:
                signOut();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //END MENU superior

    //LOGOUT
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, ActivityLogin.class));
    }

    private Uri uploadImage(Uri filePath) {
        if (filePath != null) {
            final StorageReference ref = FirebaseStorage.getInstance().getReference().child("images/" + UUID.randomUUID().toString());
            UploadTask uploadTask = ref.putFile(filePath);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        saveDataUser(downloadUri);
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }
        return null;
    }

    //Carga de usuario asyncrona
    private void bringUser() {
        UserLoader userCountersLoader = new UserLoader(ApiClient.getInstance(accessToken).getService(), this, uid);
        userCountersLoader.getAllUser().observe(this, this::onUserLoaded);
        userCountersLoader.loadAllUser();
    }

    private void onUserLoaded(HashMap<String, Object> counters) {
        this.user = (User) counters.get("user");
        retrieveData();
    }

    public void saveDataUser(Uri imgUrl) {
        ApiClient apiClient = ApiClient.getInstance(accessToken);

        String urlImage = (imgUrl != null ? imgUrl.toString() : lastFileUrl.toString());
        String username = etUsername.getText().toString();
        String bio = etBiography.getText().toString();

        UserEditRequest editer = new UserEditRequest(Utils.getUserToken(), username, urlImage, bio);

        Call<Void> call = apiClient.getService().editUser(editer);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    //Toast.makeText(ActivityEditProfile.this, "Good!", Toast.LENGTH_SHORT).show();
                    if (!lastFileUrl.equals("")) {
                        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().getStorage().getReferenceFromUrl(user.getImgProfile());
                        storageReference.delete();
                    }
                    finish();
                } else {
                    // Handle the error
                    Log.e("API_ERROR", "Response error: " + response.code() + " " + response.message());
                    Toast.makeText(ActivityEditProfile.this, "bad!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Handle the error
                Toast.makeText(ActivityEditProfile.this, "bad!", Toast.LENGTH_SHORT).show();
            }
        });

    }

}