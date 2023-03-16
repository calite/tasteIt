package com.example.tasteit_java;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tasteit_java.adapters.AdapterFragmentNewRecipe;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.Utils;
import com.example.tasteit_java.fragments.FragmentInfoNewRecipe;
import com.example.tasteit_java.fragments.FragmentIngredientsNewRecipe;
import com.example.tasteit_java.fragments.FragmentStepsNewRecipe;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ActivityNewRecipe extends AppCompatActivity {

    private ImageView ivRecipePhoto;
    private ImageButton ibPickPhoto;
    private TabLayout tlRecipe;
    private ViewPager2 vpPaginator;
    private Button bSave;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);

        //gipsy fix
        BdConnection app = new BdConnection();  //Instanciamos la conexion

        //firebase User
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = firebaseUser.getUid();


        //menu superior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("New Recipe");

        //info, steps and ingredients Fragments
        vpPaginator = findViewById(R.id.vpPaginator);
        tlRecipe = findViewById(R.id.tlRecipe);

        vpPaginator.setAdapter(new AdapterFragmentNewRecipe(getSupportFragmentManager(), getLifecycle()));

        tlRecipe.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
                tlRecipe.selectTab(tlRecipe.getTabAt(position));
            }
        });

        //ñapa
        tlRecipe.selectTab(tlRecipe.getTabAt(2));
        tlRecipe.selectTab(tlRecipe.getTabAt(0));

        //seleccionar foto
        ibPickPhoto = findViewById(R.id.ibPickPhoto);
        ivRecipePhoto = findViewById(R.id.ivRecipePhoto);
        ibPickPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View v = View.inflate(ActivityNewRecipe.this, R.layout.item_photo_picker, null);
                Dialog dialog = new Dialog(ActivityNewRecipe.this);

                Button bFromGallery = v.findViewById(R.id.bFromGallery);
                Button bFromCamera = v.findViewById(R.id.bFromCamera);

                bFromGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utils.selectImageFromMedia(ActivityNewRecipe.this);
                    }
                });

                bFromCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utils.takePicture(ActivityNewRecipe.this);
                    }
                });

                dialog.setContentView(v);
                dialog.setTitle("Select an option: ");
                dialog.create();
                dialog.show();

            }
        });

        //boton guardado
        bSave = findViewById(R.id.bSave);
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //fecha
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dateCreated = sdf.format(c.getTime());
                //img to base64
                Drawable drawable = ivRecipePhoto.getDrawable();
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                String imgBase64 = Utils.encodeTobase64(bitmap);
                //recogemos datos de fragment info
                String name = FragmentInfoNewRecipe.getRecipeName().getText().toString();
                String description = FragmentInfoNewRecipe.getDescriptionRecipe().getText().toString();
                String country = FragmentInfoNewRecipe.getCountry();
                int difficulty = FragmentInfoNewRecipe.getDificulty();
                ArrayList<String> listTags = FragmentInfoNewRecipe.getTags();
                //recogemos datos de fragment pasos
                ArrayList<String> listSteps = FragmentStepsNewRecipe.getSteps();
                //recogemos datos del fragment ingredientes
                ArrayList<String> listIngredients = FragmentIngredientsNewRecipe.getIngredients();
                //recogemos el userName
                String userName = app.retrieveNameCurrentUser(uid);
                //instanciacion de receta
                if(checkFields()){
                    Recipe r = new Recipe(name, description, listSteps, dateCreated, difficulty, userName, imgBase64, country, listTags, listIngredients);
                    //insercion en neo
                    app.createRecipe(r, uid);
                    //redireccionamos al main
                    startActivity(new Intent(ActivityNewRecipe.this, ActivityMain.class));
                } else{
                    Toast.makeText(ActivityNewRecipe.this, "Fill the required Fields", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    private boolean checkFields() {

        boolean status = true;

        //check foto
        if(ivRecipePhoto.getDrawable() == null) {
            status = false;
        }
        //check name
        EditText etRecipeName = FragmentInfoNewRecipe.getRecipeName();
        if(etRecipeName.getText().toString().length() == 0) {
            status = false;
            etRecipeName.setError("Please enter a Recipe Name");
        }
        //description
        EditText etDescriptionRecipe = FragmentInfoNewRecipe.getDescriptionRecipe();
        if(etDescriptionRecipe.getText().toString().length() == 0) {
            status = false;
            etDescriptionRecipe.setError("Please enter a Recipe Description");
        }
        //tags
        EditText etTagName = FragmentInfoNewRecipe.getEtTagName();
        if(FragmentInfoNewRecipe.getTags().size() == 0) {
            status = false;
            etTagName.setError("");
        }
        //steps
        if(FragmentStepsNewRecipe.getSteps().size() == 0) {
            status = false;
        }
        //ingredients
        EditText etIngredientName = FragmentIngredientsNewRecipe.getEtIngredientName();
        if(FragmentIngredientsNewRecipe.getIngredients().size() == 0) {
            status = false;
            etIngredientName.setError("");
        }
        return status;
    }

    //necesario para el selector de fotos
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101) {
            Utils.onActivityResult(this, requestCode, resultCode, data, filePath, ivRecipePhoto);
        }
        if(requestCode == 202) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ivRecipePhoto.setImageBitmap(photo);
        }

    }

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