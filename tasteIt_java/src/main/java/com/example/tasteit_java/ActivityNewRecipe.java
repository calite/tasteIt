package com.example.tasteit_java;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class ActivityNewRecipe extends AppCompatActivity {

    private ImageView ivRecipePhoto;
    private ImageButton ibPickPhoto;
    private EditText etRecipeName;
    private EditText etDescriptionRecipe;
    private Button bAddStep;
    private ListView lvSteps;
    private AdapterListViewNewRecipe adapter;
    private ArrayList<String> ArrayListSteps;
    private Button bSave;

    //firebase fotos
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private static final int SELECT_PICTURE = 101;
    private Uri filePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);

        //menu superior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Recipe");

        //firebase storege
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //seleccionar foto perfil
        ibPickPhoto = findViewById(R.id.ibPickPhoto);
        ivRecipePhoto = findViewById(R.id.ivRecipePhoto);
        ibPickPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });


        //steps
        ArrayListSteps = new ArrayList<>();
        ArrayListSteps.add("");

        lvSteps = findViewById(R.id.lvSteps);
        adapter = new AdapterListViewNewRecipe(this,ArrayListSteps);
        lvSteps.setAdapter(adapter);

        bAddStep = findViewById(R.id.bAddStep);
        bAddStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayListSteps.add("");
                adapter.notifyDataSetChanged();
            }
        });


    }

    //PHOTO PICKER
    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select a Picture"), SELECT_PICTURE);
    }

    private void uploadImage() {
        if (filePath != null) { //se podria simplificar mas
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
                            Toast.makeText(ActivityNewRecipe.this,"Image Uploaded!!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //fail
                            progressDialog.dismiss();
                            Toast.makeText(ActivityNewRecipe.this,"Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    try {
                        //creamos imagen
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                        //almacenamos el path
                        filePath = data.getData();
                        //cambiamos imagen del perfil
                        ivRecipePhoto.setImageBitmap(bitmap);
                        //subimos la imagen a firebase
                        //uploadImage(); DESCOMENTAR!
                        //el upload se debe hacer en el save
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //END PHOTO PICKER

    //MENU SUPERIOR
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}