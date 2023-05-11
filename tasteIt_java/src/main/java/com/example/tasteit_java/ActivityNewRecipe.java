package com.example.tasteit_java;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tasteit_java.ApiService.ApiClient;
import com.example.tasteit_java.ApiService.ApiRequests;
import com.example.tasteit_java.ApiService.RecipeId_Recipe_User;
import com.example.tasteit_java.ApiUtils.RecipeLoader;
import com.example.tasteit_java.adapters.AdapterFragmentNewRecipe;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.SharedPreferencesSaved;
import com.example.tasteit_java.clases.Utils;
import com.example.tasteit_java.fragments.FragmentInfoNewRecipe;
import com.example.tasteit_java.fragments.FragmentIngredientsNewRecipe;
import com.example.tasteit_java.fragments.FragmentStepsNewRecipe;
import com.example.tasteit_java.request.RecipeEditRequest;
import com.example.tasteit_java.request.RecipeRequest;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityNewRecipe extends AppCompatActivity {

    private ImageView ivRecipePhoto;
    private ImageButton ibPickPhoto;
    private TabLayout tlRecipe;
    private ViewPager2 vpPaginator;
    private Uri newFilePath;
    private Uri lastFileUrl;
    private String token;
    private int recipeId;
    String creatorToken = "";
    private Recipe recipe;
    private boolean editing = false;
    private ApiClient apiClient;
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);

        accessToken = new SharedPreferencesSaved(this).getSharedPreferences().getString("accessToken", "null");
        token = new SharedPreferencesSaved(this).getSharedPreferences().getString("uid", "null");
        newFilePath = null;

        if(getIntent().getExtras() != null) {
            editing = true;
            Bundle params = getIntent().getExtras();
            recipeId = params.getInt("recipeId");
            creatorToken = params.getString("creatorToken");

            bringRecipe();
            getSupportActionBar().setTitle("Edit Recipe");
        } else {
            getSupportActionBar().setTitle("New Recipe");
        }

        //menu superior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        vpPaginator.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tlRecipe.selectTab(tlRecipe.getTabAt(position));
            }
        });

        //seleccionar foto
        ibPickPhoto = findViewById(R.id.ibPickPhoto);
        ivRecipePhoto = findViewById(R.id.ivRecipePhoto);

        //Cambiar foto de la receta
        PopupMenu.OnMenuItemClickListener popupListener = new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.iCamera: {
                        Utils.takePicture(ActivityNewRecipe.this);
                        break;
                    }
                    case R.id.iGallery: {
                        Utils.selectImageFromMedia(ActivityNewRecipe.this);
                        break;
                    }
                }
                return false;
            }
        };

        ibPickPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(ActivityNewRecipe.this, view);
                MenuInflater menuInflater = popupMenu.getMenuInflater();
                menuInflater.inflate(R.menu.change_image_from, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(popupListener);
                popupMenu.show();
            }
        });
    }
/*
    private void saveRecipe(String token, BdConnection app){
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
        String userName = app.retrieveNameCurrentUser(token);

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
        palabras.addAll(listSteps); //esto hay que splitearlo tb @TODO
        listTags = Utils.searchTags(palabras,diccionario);

        //instanciacion de receta
        if(checkFields()){
            Recipe r = new Recipe(name, description, listSteps, dateCreated, difficulty, userName, imgBase64, country, listTags, listIngredients);
            //insercion en neo
            if(editing){

            }else{app.createRecipe(r, token);}

            //redireccionamos al main
            startActivity(new Intent(ActivityNewRecipe.this, ActivityMain.class));
        } else{
            Toast.makeText(ActivityNewRecipe.this, "Fill the required Fields", Toast.LENGTH_SHORT).show();
        }
    }
 */
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
            if (etIngredientName == null) {
                tlRecipe.getTabAt(2).select();
            }else {
                etIngredientName.setError("");
            }
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
                if(editing){
                    if(newFilePath != null) {
                        uploadImage(newFilePath);
                    } else {
                        editRecipe();
                    }
                } else{
                    if(newFilePath != null) {
                        uploadImage(newFilePath);
                    } else {
                        Toast.makeText(this, "You have to set a image", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //necesario para el selector de fotos
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101) {
            newFilePath = data.getData();
            Utils.onActivityResult(this, requestCode, resultCode, data, ivRecipePhoto);
        }
        /*if(requestCode == 202) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ivRecipePhoto.setImageBitmap(photo);
        }*/
    }

    private void bringRecipe() {
        RecipeLoader recipesLoader = new RecipeLoader(ApiClient.getInstance(accessToken).getService(), this, recipeId);
        recipesLoader.getRecipeById().observe(this, this::onRecipeLoaded);
        recipesLoader.loadRecipeById();
    }

    private void onRecipeLoaded(Recipe recipes) {
        recipe = recipes;

        try {
            Picasso.with(this).load(recipe.getImage()).into(ivRecipePhoto);
        }catch(IllegalArgumentException iae){
            Toast.makeText(this, "Image not retrieved", Toast.LENGTH_SHORT).show();
        }

        lastFileUrl = Uri.parse(recipe.getImage());

        //INFO
        FragmentInfoNewRecipe.setRecipeName(recipe.getName());
        FragmentInfoNewRecipe.setDescriptionRecipe(recipe.getDescription());
        FragmentInfoNewRecipe.setCountry(recipe.getCountry());
        FragmentInfoNewRecipe.setDificulty(recipe.getDifficulty());
        FragmentInfoNewRecipe.setTags(recipe.getTags());
        //STEPS
        FragmentStepsNewRecipe.setSteps(recipe.getSteps());
        //INGREDIENTS
        FragmentIngredientsNewRecipe.setIngredients(recipe.getIngredients());
    }

    public void createRecipe() {
        apiClient = ApiClient.getInstance(accessToken);

        String name = FragmentInfoNewRecipe.getRecipeName().getText().toString();
        String description = FragmentInfoNewRecipe.getDescriptionRecipe().getText().toString();
        String country = FragmentInfoNewRecipe.getCountry();
        int difficulty = FragmentInfoNewRecipe.getDificulty();
        //recogemos datos de fragment pasos
        ArrayList<String> listSteps = FragmentStepsNewRecipe.getSteps();
        String steps = String.join(",", listSteps);
        //recogemos datos del fragment ingredientes
        ArrayList<String> listIngredients = FragmentIngredientsNewRecipe.getIngredients();
        String ingredients = String.join(",", listIngredients);
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
        palabras.addAll(listSteps); //esto hay que splitearlo tb @TODO

        if (checkFields()) {
            RecipeRequest r = new RecipeRequest();
            r.setToken(token);
            r.setName(name);
            r.setDescription(description);
            r.setCountry(country);
            r.setImage(newFilePath.toString());
            r.setDifficulty(difficulty);
            r.setIngredients(ingredients);
            r.setSteps(steps);

            Call<Void> call = apiClient.getService().createRecipe(r);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        //AQUI DEVOLVEMOS AL MAIN!
                        Toast.makeText(ActivityNewRecipe.this, "Saved!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        // Handle the error
                        Log.e("API_ERROR", "Response error: " + response.code() + " " + response.message());
                        Toast.makeText(ActivityNewRecipe.this, "Something was wrong!", Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    // Handle the error
                    Toast.makeText(ActivityNewRecipe.this, "Something was wrong!", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    public void editRecipe() {
        Toast.makeText(this, "EDITING ...", Toast.LENGTH_SHORT).show();
        apiClient = ApiClient.getInstance(accessToken);

        String name = FragmentInfoNewRecipe.getRecipeName().getText().toString();
        String description = FragmentInfoNewRecipe.getDescriptionRecipe().getText().toString();
        String country = FragmentInfoNewRecipe.getCountry();
        int difficulty = FragmentInfoNewRecipe.getDificulty();
        //recogemos datos de fragment pasos
        ArrayList<String> listSteps = FragmentStepsNewRecipe.getSteps();
        String steps = String.join(",", listSteps);
        //recogemos datos del fragment ingredientes
        ArrayList<String> listIngredients = FragmentIngredientsNewRecipe.getIngredients();
        String ingredients = String.join(",", listIngredients);
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

        String urlImage = (newFilePath != null ? newFilePath.toString() : lastFileUrl.toString());

        if (checkFields()) {
            RecipeEditRequest r = new RecipeEditRequest();
            r.setRecipeId(recipeId);
            r.setName(name);
            r.setDescription(description);
            r.setCountry(country);
            r.setImage(urlImage);
            r.setDifficulty(difficulty);
            r.setIngredients(ingredients);
            r.setSteps(steps);

            Call<Void> call = apiClient.getService().editRecipe(r);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        if (!lastFileUrl.equals("")) {
                            final StorageReference storageReference = FirebaseStorage.getInstance().getReference().getStorage().getReferenceFromUrl(recipe.getImage());
                            storageReference.delete();
                        }
                        Toast.makeText(ActivityNewRecipe.this, "Saved!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        // Handle the error
                        Log.e("API_ERROR", "Response error: " + response.code() + " " + response.message());
                        Toast.makeText(ActivityNewRecipe.this, "bad!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    // Handle the error
                    Toast.makeText(ActivityNewRecipe.this, "bad!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void saveData(Uri downloadUri) {
        newFilePath = downloadUri;
        if(editing) {
            editRecipe();
        } else {
            createRecipe();
        }
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
                        saveData(downloadUri);
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });

        }
        return null;
    }

}
