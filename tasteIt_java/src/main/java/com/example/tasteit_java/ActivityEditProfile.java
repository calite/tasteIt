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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.User;
import com.example.tasteit_java.clases.Utils;
import com.google.firebase.auth.FirebaseAuth;

public class ActivityEditProfile extends AppCompatActivity {

    private ImageButton ibPickPhoto;

    private ImageView ivProfilePhoto;

    private TextView tvUsername, tvConfirmPassword;

    private EditText etUsername, etEmail, etPassword, etConfirmPassword, etBiography;

    private Button btnSave;

    private BdConnection connection;

    private User user;

    private Bitmap newProfileImage;

    private Uri filePath;

    private ConstraintLayout constraintLayout;

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
        ibPickPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = View.inflate(ActivityEditProfile.this, R.layout.item_photo_picker, null);
                Dialog dialog = new Dialog(ActivityEditProfile.this);

                Button bFromGallery = v.findViewById(R.id.bFromGallery);
                Button bFromCamera = v.findViewById(R.id.bFromCamera);

                bFromGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utils.selectImageFromMedia(ActivityEditProfile.this);
                    }
                });
                bFromCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utils.takePicture(ActivityEditProfile.this);
                    }
                });

                dialog.setContentView(v);
                dialog.setTitle("Select an option: ");
                dialog.create();
                dialog.show();
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

        //Agregamos un listener a la password
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() > 0) {
                    tvConfirmPassword.setVisibility(View.VISIBLE);
                    etConfirmPassword.setVisibility(View.VISIBLE);

                    constraintLayout = findViewById(R.id.mainLayout);
                    constraintSet = new ConstraintSet();
                    constraintSet.clone(constraintLayout);

                    constraintSet.connect(btnSave.getId(), ConstraintSet.TOP, etConfirmPassword.getId(), ConstraintSet.BOTTOM, 30);
                    constraintSet.applyTo(constraintLayout);
                } else {
                    tvConfirmPassword.setVisibility(View.INVISIBLE);
                    etConfirmPassword.setVisibility(View.INVISIBLE);

                    constraintLayout = findViewById(R.id.mainLayout);
                    constraintSet = new ConstraintSet();
                    constraintSet.clone(constraintLayout);

                    constraintSet.connect(btnSave.getId(), ConstraintSet.TOP, etPassword.getId(), ConstraintSet.BOTTOM, 30);
                    constraintSet.applyTo(constraintLayout);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

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
        etPassword = findViewById(R.id.etPassword);
        etBiography = findViewById(R.id.etBiography);
        tvConfirmPassword = findViewById(R.id.tvConfirmPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSave = findViewById(R.id.btnSave);
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
        String password = etPassword.getText().toString();
        String ConfPass = etConfirmPassword.getText().toString();

        if(password.length() >= 6 && password.equals(ConfPass)) {
            //FirebaseAuth.getInstance().getCurrentUser().reauthenticate();
            FirebaseAuth.getInstance().getCurrentUser().updatePassword(password);
            connection.changeDataUser(FirebaseAuth.getInstance().getCurrentUser().getUid(), username, imagenB64, bio);
            Toast.makeText(this, "Data and password saved successfully", Toast.LENGTH_SHORT).show();
            return true;
        } else if(password.length() == 0 && ConfPass.length() == 0) {
            connection.changeDataUser(FirebaseAuth.getInstance().getCurrentUser().getUid(), username, imagenB64, bio);
            Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(this, "Password must match and be equal or longer than 6 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    //END GUARDADO DE DATOS

    //PHOTO PICKER
    /*private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent,"Select a Picture"), SELECT_PICTURE);
    }

    private void uploadImage() {
        if (filePath != null) {
            //mostrara un dialog con el progreso
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            //alojamiento en el servidor
            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
            //listeners para el resultado de la subida
            ref.putFile(filePath)
                    .addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    //correcto
                                    progressDialog.dismiss();
                                    Toast.makeText(ActivityEditProfile.this,"Image Uploaded!!", Toast.LENGTH_SHORT).show();
                                }
                            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //fail
                            progressDialog.dismiss();
                            Toast.makeText(ActivityEditProfile.this,"Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101) {
            Utils.onActivityResult(this, requestCode, resultCode, data, filePath, ivProfilePhoto);
        }
        if(requestCode == 202) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ivProfilePhoto.setImageBitmap(photo);
        }
    }
    //END PHOTO PICKER

    //MENU superior
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_profile_menu, menu);
        return true;
    }

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