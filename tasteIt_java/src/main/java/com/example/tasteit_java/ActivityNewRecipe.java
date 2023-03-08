package com.example.tasteit_java;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

public class ActivityNewRecipe extends AppCompatActivity {

    private ImageView ivRecipePhoto;
    private ImageButton ibPickPhoto;
    private EditText etRecipeName;
    private EditText etDescriptionRecipe;
    private Button bAddStep;
    private ListView lvSteps;
    private AdapterListViewNewRecipe adapter;
    private ArrayList<String> listSteps;
    private Button bSave;
    private Spinner spCountries;

    //firebase fotos
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private static final int SELECT_PICTURE = 101;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);

        //gipsy fix

        //NEO4J
        String uri = "neo4j+s://dc95b24b.databases.neo4j.io"; //URL conexion Neo4j
        String user = "neo4j";
        String pass = "sBQ6Fj2oXaFltjizpmTDhyEO9GDiqGM1rG-zelf17kg"; //PDTE CIFRAR
        BdConnection app = new BdConnection(uri, user, pass);  //Instanciamos la conexion
        //FIN NEO

        //firebase User
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();


        //menu superior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Recipe");

        //firebase storege
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //textbox
        etRecipeName = findViewById(R.id.etRecipeName);
        etDescriptionRecipe = findViewById(R.id.etDescripcionRecipe);

        //spinner
        spCountries = findViewById(R.id.spCountry);
        ArrayAdapter<CharSequence> adapterSpinnerCountries = ArrayAdapter.createFromResource(this,
                R.array.countries_array, android.R.layout.simple_spinner_item);
        adapterSpinnerCountries.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCountries.setAdapter(adapterSpinnerCountries);



        //boton guardado
        bSave = findViewById(R.id.bSave);

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
        listSteps = new ArrayList<>();
        listSteps.add("");

        lvSteps = findViewById(R.id.lvSteps);
        adapter = new AdapterListViewNewRecipe(this,listSteps);
        lvSteps.setAdapter(adapter);

        bAddStep = findViewById(R.id.bAddStep);
        bAddStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listSteps.add("");
                adapter.notifyDataSetChanged();
            }
        });
        //guardado de receta
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //fecha
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dateCreated = sdf.format(c.getTime());
                //img to base64
                Drawable drawable = ivRecipePhoto.getDrawable();
                Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
                String imgBase64 = Utils.encodeTobase64(bitmap);

                //userName
                String userName = app.retrieveNameCurrentUser(uid);

                Recipe r = new Recipe(etRecipeName.getText().toString(),etDescriptionRecipe.getText().toString(), listSteps,imgBase64,dateCreated,spCountries.getSelectedItem().toString(),userName);
                app.createRecipe(r,uid);
                //temporal
                ActivityMain.listRecipes.add(r);
                //redireccionamos al main
                startActivity(new Intent(ActivityNewRecipe.this, ActivityMain.class));

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

    //subir foto a firebase -> posiblemente no se usara, se cambia a base64
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