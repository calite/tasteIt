package com.example.tasteit_java;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
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
import java.util.Arrays;
import java.util.Calendar;

public class ActivityNewRecipe extends AppCompatActivity {

    private ImageView ivRecipePhoto;
    private ImageButton ibPickPhoto;
    private TabLayout tlRecipe;
    private ViewPager2 vpPaginator;
    private Uri filePath;
    private BdConnection app;
    private String uid;

    private EditText etRecipeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);


         app = new BdConnection();  //Instanciamos la conexion

        //firebase User
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = firebaseUser.getUid();

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

    }

    private void saveRecipe(String uid, BdConnection app){
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
        //ArrayList<String> listTags = FragmentInfoNewRecipe.getTags();
        //recogemos datos de fragment pasos
        ArrayList<String> listSteps = FragmentStepsNewRecipe.getSteps();
        //recogemos datos del fragment ingredientes
        ArrayList<String> listIngredients = FragmentIngredientsNewRecipe.getIngredients();
        //recogemos el userName
        String userName = app.retrieveNameCurrentUser(uid);

        //generacion automatica de tags
        ArrayList<String> listTags = new ArrayList<>();
        ArrayList<String> diccionario = new ArrayList<>();
        //traemos el diccionario
        Resources res = getResources();
        TypedArray tagsArray = res.obtainTypedArray(R.array.tags_array);
        for (int i = 0; i < tagsArray.length(); i++) {
            diccionario.add(tagsArray.getString(i));
        }
        tagsArray.recycle(); //liberamos el recurso
        ArrayList<String> palabras = new ArrayList<>();
        //recogemos todas las palabras escritas por el usuario
        palabras.addAll(Arrays.asList(name.split("\\s+")));
        palabras.addAll(Arrays.asList(description.split("\\s+")));
        palabras.addAll(listIngredients);
        palabras.addAll(listSteps);
        listTags = Utils.searchTags(palabras,diccionario);

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

    //comprobacion de campos
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
        /*
        //tags
        EditText etTagName = FragmentInfoNewRecipe.getEtTagName();
        if(FragmentInfoNewRecipe.getTags().size() == 0) {
            status = false;
            etTagName.setError("");
        }
        */
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

    //MENU superior
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_recipe_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.iSaveRecipe:
                saveRecipe(uid,app);
                return true;
        }
        return super.onOptionsItemSelected(item);
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



}