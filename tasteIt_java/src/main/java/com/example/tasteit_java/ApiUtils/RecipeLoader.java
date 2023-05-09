package com.example.tasteit_java.ApiUtils;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tasteit_java.ActivityRandom;
import com.example.tasteit_java.ApiService.ApiRequests;
import com.example.tasteit_java.ApiService.RecipeId_Recipe_User;
import com.example.tasteit_java.clases.Recipe;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeLoader {

    private final ApiRequests apiRequests;
    private MutableLiveData<List<Recipe>> recipesLiveData;
    private MutableLiveData<Recipe> recipeLiveData;
    private MutableLiveData<Boolean> likedLiveData;
    private Context context;
    private int skipper;
    private int recipeId;
    private ArrayList<Integer> lastIdRecipes;
    private String sender_token;

    public RecipeLoader(ApiRequests apiRequests, Context context, String sender_token, int data) { //Llamamos al int 'data' porque podemos poner el skipper o el IsLiked, depende del metodo a llamar (no interfiere)
        this.apiRequests = apiRequests;
        this.context = context;
        recipesLiveData = new MutableLiveData<>();
        this.skipper = data;
        this.sender_token = sender_token;
        likedLiveData = new MutableLiveData<>();
        this.recipeId = data;
    }

    public RecipeLoader(ApiRequests apiRequests, Context context, int data) { //Llamamos al int 'data' porque podemos poner el skipper o el recipeId, depende del metodo a llamar (no interfiere)
        this.apiRequests = apiRequests;
        this.context = context;
        recipesLiveData = new MutableLiveData<>();
        this.skipper = data;
        recipeLiveData = new MutableLiveData<>();
        this.recipeId = data;
    }

    public RecipeLoader(ApiRequests apiRequests, Context context, ArrayList<Integer> lastIdRecipes) {
        this.apiRequests = apiRequests;
        this.context = context;
        recipeLiveData = new MutableLiveData<>();
        this.lastIdRecipes = lastIdRecipes;
        recipeId = 0;
    }

    public LiveData<List<Recipe>> getRecipesByUser() {
        return recipesLiveData;
    }

    public void loadRecipesByUser() {
        apiRequests.getRecipesByUser(sender_token, skipper).enqueue(new Callback<List<RecipeId_Recipe_User>>() {
            @Override
            public void onResponse(Call<List<RecipeId_Recipe_User>> call, Response<List<RecipeId_Recipe_User>> response) {
                if (response.isSuccessful()) {
                    List<RecipeId_Recipe_User> recipeApis = response.body();
                    List<Recipe> recipes = new ArrayList<>();

                    //tratamos los datos
                    for (RecipeId_Recipe_User recipeApi : recipeApis) {
                        Recipe recipe = new Recipe(
                                recipeApi.getRecipeDetails().getName(),
                                recipeApi.getRecipeDetails().getDescription(),
                                (ArrayList<String>) recipeApi.getRecipeDetails().getSteps(),
                                recipeApi.getRecipeDetails().getDateCreated(),
                                recipeApi.getRecipeDetails().getDifficulty(),
                                recipeApi.getUser().getUsername(),
                                recipeApi.getRecipeDetails().getImage(),
                                recipeApi.getRecipeDetails().getCountry(),
                                (ArrayList<String>) recipeApi.getRecipeDetails().getTags(),
                                (ArrayList<String>) recipeApi.getRecipeDetails().getIngredients(),
                                recipeApi.getRecipeId(),
                                recipeApi.getUser().getToken()
                        );
                        recipes.add(recipe);
                    }
                    recipesLiveData.postValue(recipes);
                } else {
                    // La solicitud no fue exitosa
                }
            }

            @Override
            public void onFailure(Call<List<RecipeId_Recipe_User>> call, Throwable t) {
                // Hubo un error en la solicitud
                Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public LiveData<List<Recipe>> getRecipesLikedByUser() {
        return recipesLiveData;
    }

    public void loadRecipesLikedByUser() {
        apiRequests.getRecipesLiked(sender_token, skipper).enqueue(new Callback<List<RecipeId_Recipe_User>>() {
            @Override
            public void onResponse(Call<List<RecipeId_Recipe_User>> call, Response<List<RecipeId_Recipe_User>> response) {
                if (response.isSuccessful()) {
                    List<RecipeId_Recipe_User> recipeApis = response.body();
                    List<Recipe> recipes = new ArrayList<>();

                    //tratamos los datos
                    for (RecipeId_Recipe_User recipeApi : recipeApis) {
                        Recipe recipe = new Recipe(
                                recipeApi.getRecipeDetails().getName(),
                                recipeApi.getRecipeDetails().getDescription(),
                                (ArrayList<String>) recipeApi.getRecipeDetails().getSteps(),
                                recipeApi.getRecipeDetails().getDateCreated(),
                                recipeApi.getRecipeDetails().getDifficulty(),
                                recipeApi.getUser().getUsername(),
                                recipeApi.getRecipeDetails().getImage(),
                                recipeApi.getRecipeDetails().getCountry(),
                                (ArrayList<String>) recipeApi.getRecipeDetails().getTags(),
                                (ArrayList<String>) recipeApi.getRecipeDetails().getIngredients(),
                                recipeApi.getRecipeId(),
                                recipeApi.getUser().getToken()
                        );
                        recipes.add(recipe);
                    }
                    recipesLiveData.postValue(recipes);
                } else {
                    // La solicitud no fue exitosa
                }
            }

            @Override
            public void onFailure(Call<List<RecipeId_Recipe_User>> call, Throwable t) {
                // Hubo un error en la solicitud
                Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public LiveData<List<Recipe>> getRecipesFollowedByUser() {
        return recipesLiveData;
    }

    public void loadRecipesFollowedByUser() {
        apiRequests.getRecipesFollowed(sender_token, skipper).enqueue(new Callback<List<RecipeId_Recipe_User>>() {
            @Override
            public void onResponse(Call<List<RecipeId_Recipe_User>> call, Response<List<RecipeId_Recipe_User>> response) {
                if (response.isSuccessful()) {
                    List<RecipeId_Recipe_User> recipesApi = response.body();
                    List<Recipe> recipes = new ArrayList<>();

                    //tratamos los datos
                    for (RecipeId_Recipe_User recipeApi : recipesApi) {
                        Recipe recipe = new Recipe(
                                recipeApi.getRecipeDetails().getName(),
                                recipeApi.getRecipeDetails().getDescription(),
                                (ArrayList<String>) recipeApi.getRecipeDetails().getSteps(),
                                recipeApi.getRecipeDetails().getDateCreated(),
                                recipeApi.getRecipeDetails().getDifficulty(),
                                recipeApi.getUser().getUsername(),
                                recipeApi.getRecipeDetails().getImage(),
                                recipeApi.getRecipeDetails().getCountry(),
                                (ArrayList<String>) recipeApi.getRecipeDetails().getTags(),
                                (ArrayList<String>) recipeApi.getRecipeDetails().getIngredients(),
                                recipeApi.getRecipeId(),
                                recipeApi.getUser().getUsername()
                        );
                        recipes.add(recipe);
                    }
                    recipesLiveData.postValue(recipes);
                } else {
                    // La solicitud no fue exitosa
                }
            }
            @Override
            public void onFailure(Call<List<RecipeId_Recipe_User>> call, Throwable t) {
                // Hubo un error en la solicitud
                Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public LiveData<List<Recipe>> getAllRecipesWSkipper() {
        return recipesLiveData;
    }

    public void loadAllRecipesWSkipper() {
        apiRequests.getRecipes(skipper).enqueue(new Callback<List<RecipeId_Recipe_User>>() {
            @Override
            public void onResponse(Call<List<RecipeId_Recipe_User>> call, Response<List<RecipeId_Recipe_User>> response) {
                if (response.isSuccessful()) {
                    List<RecipeId_Recipe_User> recipeApis = response.body();
                    List<Recipe> recipes = new ArrayList<>();

                    //tratamos los datos
                    for (RecipeId_Recipe_User recipeApi : recipeApis) {
                        Recipe recipe = new Recipe(
                                recipeApi.getRecipeDetails().getName(),
                                recipeApi.getRecipeDetails().getDescription(),
                                (ArrayList<String>) recipeApi.getRecipeDetails().getSteps(),
                                recipeApi.getRecipeDetails().getDateCreated(),
                                recipeApi.getRecipeDetails().getDifficulty(),
                                recipeApi.getUser().getUsername(),
                                recipeApi.getRecipeDetails().getImage(),
                                recipeApi.getRecipeDetails().getCountry(),
                                (ArrayList<String>) recipeApi.getRecipeDetails().getTags(),
                                (ArrayList<String>) recipeApi.getRecipeDetails().getIngredients(),
                                recipeApi.getRecipeId(),
                                recipeApi.getUser().getToken()
                        );
                        recipes.add(recipe);
                    }
                    recipesLiveData.postValue(recipes);
                } else {
                    // La solicitud no fue exitosa
                }
            }

            @Override
            public void onFailure(Call<List<RecipeId_Recipe_User>> call, Throwable t) {
                // Hubo un error en la solicitud
                Toast.makeText(context, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public LiveData<Recipe> getRecipeById() {
        return recipeLiveData;
    }

    public void loadRecipeById() {
        apiRequests.getRecipeById(recipeId).enqueue(new Callback<List<RecipeId_Recipe_User>>() {
            @Override
            public void onResponse(Call<List<RecipeId_Recipe_User>> call, Response<List<RecipeId_Recipe_User>> response) {
                if (response.isSuccessful() && response.body().size() != 0) {
                    RecipeId_Recipe_User recipeApi = response.body().get(0);
                    Recipe recipe = new Recipe(
                            recipeApi.getRecipeDetails().getName(),
                            recipeApi.getRecipeDetails().getDescription(),
                            (ArrayList<String>) recipeApi.getRecipeDetails().getSteps(),
                            recipeApi.getRecipeDetails().getDateCreated(),
                            recipeApi.getRecipeDetails().getDifficulty(),
                            recipeApi.getUser().getUsername(),
                            recipeApi.getRecipeDetails().getImage(),
                            recipeApi.getRecipeDetails().getCountry(),
                            (ArrayList<String>) recipeApi.getRecipeDetails().getTags(),
                            (ArrayList<String>) recipeApi.getRecipeDetails().getIngredients(),
                            recipeApi.getRecipeId(),
                            recipeApi.getUser().getToken()
                    );
                    recipeLiveData.postValue(recipe);
                } else {
                    // La solicitud no fue exitosa
                }
            }
            @Override
            public void onFailure(Call<List<RecipeId_Recipe_User>> call, Throwable t) {
                Toast.makeText(context, "Something went wrong - " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public LiveData<Recipe> getRandomRecipe() {
        return recipeLiveData;
    }

    public void loadRandomRecipe() {
        do {
            recipeId = (int) (Math.random() * 50) + 1; //PDTE CAMBIAR EL 50 POR LA CANTIDAD TOTAL DE RECETAS (PET API)
            Toast.makeText(context, "ID: " + recipeId, Toast.LENGTH_SHORT).show();
        } while(lastIdRecipes.contains(recipeId));

        apiRequests.getRecipeById(recipeId).enqueue(new Callback<List<RecipeId_Recipe_User>>() {
            @Override
            public void onResponse(Call<List<RecipeId_Recipe_User>> call, Response<List<RecipeId_Recipe_User>> response) {
                if (response.isSuccessful() && response.body().size() != 0) {
                    RecipeId_Recipe_User recipeApi = response.body().get(0);
                    Recipe recipe = new Recipe(
                            recipeApi.getRecipeDetails().getName(),
                            recipeApi.getRecipeDetails().getDescription(),
                            (ArrayList<String>) recipeApi.getRecipeDetails().getSteps(),
                            recipeApi.getRecipeDetails().getDateCreated(),
                            recipeApi.getRecipeDetails().getDifficulty(),
                            recipeApi.getUser().getUsername(),
                            recipeApi.getRecipeDetails().getImage(),
                            recipeApi.getRecipeDetails().getCountry(),
                            (ArrayList<String>) recipeApi.getRecipeDetails().getTags(),
                            (ArrayList<String>) recipeApi.getRecipeDetails().getIngredients(),
                            recipeApi.getRecipeId(),
                            recipeApi.getUser().getToken()
                    );
                    recipeLiveData.postValue(recipe);
                } else {
                    // La solicitud no fue exitosa
                    loadRandomRecipe();
                }
            }
            @Override
            public void onFailure(Call<List<RecipeId_Recipe_User>> call, Throwable t) {
                Toast.makeText(context, "Something went wrong - " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public LiveData<Boolean> getIsLiked() {
        return likedLiveData;
    }

    public void loadIsLiked() {
        apiRequests.getRecipeIsLiked(recipeId, sender_token).enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (response.isSuccessful()) {
                    Boolean bool = response.body();
                    likedLiveData.postValue(bool);
                } else {
                    Toast.makeText(context, "Primer error", Toast.LENGTH_SHORT).show();
                    // La solicitud no fue exitosa
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                // Hubo un error en la solicitud
                Toast.makeText(context, "Failed to load data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
