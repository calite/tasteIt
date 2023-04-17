package com.example.tasteit_java;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
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

import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.User;
import com.example.tasteit_java.clases.Utils;
import com.example.tasteit_java.fragments.FragmentComments;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ActivityEditProfile extends AppCompatActivity {

    private ImageButton ibPickPhoto;

    private ImageView ivProfilePhoto;

    private TextView tvUsername, tvConfirmPassword, optChangePass;

    private EditText etUsername, etEmail, etNewPassword, etConfirmPassword, etBiography, etOldPassword;

    private Button btnSave;

    private BdConnection connection;

    private User user;

    private Bitmap newProfileImage;

    private Uri filePath;

    private ConstraintLayout constraintLayout, clPassword;

    private ConstraintSet constraintSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initializeViews();
        retrieveData();

        //Menu superior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");

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
                if(saveData()) {
                    finish();
                }
            }
        });
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
                if(clPassword.getVisibility() != View.VISIBLE) {
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
    }

    //Metodo para traer los datos del perfil
    private void retrieveData() {
        connection = new BdConnection();

        user = connection.retrieveUserbyUid(FirebaseAuth.getInstance().getCurrentUser().getUid());

        tvUsername.setText(user.getUsername());
        etUsername.setText(user.getUsername());

        etBiography.setText(user.getBiography());
        etEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        Bitmap bitmap = Utils.decodeBase64(user.getImgProfile());
        ivProfilePhoto.setImageBitmap(bitmap);

        newProfileImage = bitmap;
    }
    //END Recogida de datos

    //GUARDADO DE DATOS
    private boolean saveData() {
        connection = new BdConnection();

        String imagenB64 = Utils.encodeTobase64(newProfileImage);
        String username = etUsername.getText().toString();
        String bio = etBiography.getText().toString();
        String oldPassword = etOldPassword.getText().toString();
        String newPassword = etNewPassword.getText().toString();
        String ConfPass = etConfirmPassword.getText().toString();

        if(newPassword.length() >= 6 && newPassword.equals(ConfPass) && oldPassword.length() > 0) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final String email = user.getEmail();
            AuthCredential credential = EmailAuthProvider.getCredential(email,oldPassword);
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    if(task.isSuccessful()){
                        user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(Task<Void> task) {
                                if(!task.isSuccessful()){
                                    Toast.makeText(ActivityEditProfile.this, "Something went wrong. Please try again later", Toast.LENGTH_SHORT).show();
                                }else {
                                    connection.changeDataUser(FirebaseAuth.getInstance().getCurrentUser().getUid(), username, imagenB64, bio);
                                    Toast.makeText(ActivityEditProfile.this, "Data and password successfully modified", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else {
                        Toast.makeText(ActivityEditProfile.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return true;
        } else if(clPassword.getVisibility() == View.INVISIBLE && oldPassword.length() == 0) {
            connection.changeDataUser(FirebaseAuth.getInstance().getCurrentUser().getUid(), username, imagenB64, bio);
            Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(this, "Password must match and be equal or longer than 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    //END GUARDADO DE DATOS

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101) {
            Utils.onActivityResult(this, requestCode, resultCode, data, filePath, ivProfilePhoto);
        }
        if(requestCode == 202) {
            if(data.getExtras() != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                ivProfilePhoto.setImageBitmap(photo);
            }
        }
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
    public void callSignOut(View view) {
        signOut();
    } //El boton de logout me lo fumaba la verdad

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, ActivityLogin.class));
    }
    //END LOGOUT

}