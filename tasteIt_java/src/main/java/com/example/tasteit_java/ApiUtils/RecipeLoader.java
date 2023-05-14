package com.example.tasteit_java.ApiUtils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tasteit_java.ApiService.ApiRequests;
import com.example.tasteit_java.ApiService.RecipeApiComment;
import com.example.tasteit_java.ApiService.RecipeId_Recipe_User;
import com.example.tasteit_java.clases.Comment;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.SharedPreferencesSaved;
import com.example.tasteit_java.clases.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeLoader {

    private final ApiRequests apiRequests;
    private MutableLiveData<List<Recipe>> recipesLiveData;
    private MutableLiveData<Recipe> recipeLiveData;
    private MutableLiveData<Boolean> likedLiveData;
    private MutableLiveData<List<Comment>> recipeCommentsLiveData;
    private Context context;
    private int skipper;
    private int recipeId;
    private ArrayList<Integer> lastIdRecipes;
    private ArrayList<String> idsRecipesShared;
    private String sender_token;
    private String name;
    private String country;
    private String ingredient;
    private String tags;
    private int limit;

    public RecipeLoader(ApiRequests apiRequests, Context context, String sender_token, int data) { //Llamamos al int 'data' porque podemos poner el skipper o el IsLiked, depende del metodo a llamar (no interfiere)
        this.apiRequests = apiRequests;
        this.context = context;
        recipesLiveData = new MutableLiveData<>();
        this.skipper = data;
        this.sender_token = sender_token;
        likedLiveData = new MutableLiveData<>();
        this.recipeId = data;

        this.name = sender_token;
        this.country = sender_token;
        this.ingredient = sender_token;
        this.tags = sender_token;
    }

    public RecipeLoader(ApiRequests apiRequests, Context context, int data) { //Llamamos al int 'data' porque podemos poner el skipper o el recipeId, depende del metodo a llamar (no interfiere)
        this.apiRequests = apiRequests;
        this.context = context;
        recipesLiveData = new MutableLiveData<>();
        this.skipper = data;
        recipeLiveData = new MutableLiveData<>();
        this.recipeId = data;
        this.limit = data;
    }

    public RecipeLoader(ApiRequests apiRequests, Context context, ArrayList<Integer> lastIdRecipes, ArrayList<String> idsRecipesShared) {
        this.apiRequests = apiRequests;
        this.context = context;
        recipeLiveData = new MutableLiveData<>();
        this.lastIdRecipes = lastIdRecipes;
        this.idsRecipesShared = idsRecipesShared;
        recipeId = 0;
    }

    public RecipeLoader(ApiRequests apiRequests, Context context, int recipeId, int skipper) {
        this.apiRequests = apiRequests;
        this.context = context;
        recipeCommentsLiveData = new MutableLiveData<>();
        this.recipeId = recipeId;
        this.skipper = skipper;
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
                                recipeApi.getRecipeDetails().getRating(),
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
                                recipeApi.getRecipeDetails().getRating(),
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
                                recipeApi.getRecipeDetails().getRating(),
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
                                recipeApi.getRecipeDetails().getRating(),
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
                            recipeApi.getRecipeDetails().getRating(),
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

    /*public LiveData<Recipe> getRandomRecipe() {
        return recipeLiveData;
    }

    public void loadRandomRecipe() {
        if (!idsRecipesShared.isEmpty()) {
            do {
                Collections.shuffle(idsRecipesShared);
                recipeId = Integer.parseInt(idsRecipesShared.get(0));
                idsRecipesShared.remove(0);
            } while(lastIdRecipes.contains(recipeId) || !idsRecipesShared.isEmpty());
        } else {
            do {
                Random random = new Random();
                recipeId = (random.nextInt(50));
            } while(lastIdRecipes.contains(recipeId));
        }
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
                            recipeApi.getRecipeDetails().getRating(),
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
                    loadRandomRecipe();
                }
            }

            @Override
            public void onFailure(Call<List<RecipeId_Recipe_User>> call, Throwable t) {
                Toast.makeText(context, "Something went wrong - " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }*/

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

    public LiveData<List<Comment>> getRecipeComments() {
        return recipeCommentsLiveData;
    }

    public void loadRecipeComments() {
        apiRequests.getCommentsOnRecipe(recipeId, skipper).enqueue(new Callback<List<RecipeApiComment>>() {
            @Override
            public void onResponse(Call<List<RecipeApiComment>> call, Response<List<RecipeApiComment>> response) {
                if (response.isSuccessful()) {
                    List<RecipeApiComment> commentsApi = response.body();
                    List<Comment> comments = new ArrayList<>();

                    if (!commentsApi.isEmpty()) {
                        for (RecipeApiComment temp : commentsApi) {
                            try {
                                Comment comment = new Comment(
                                        temp.getComment().getComment(),
                                        String.valueOf(temp.getComment().getDateCreated()),
                                        Float.valueOf(temp.getComment().getRating()),
                                        new User(temp.getUser().getUsername(), temp.getUser().getBiography(), temp.getUser().getImgProfile(), temp.getUser().getToken())
                                );
                                comments.add(comment);
                            } catch (NullPointerException ex) {
                                Log.e("Error", "apiRequests.getCommentsOnRecipe error - " + ex.getMessage());
                            }
                        }
                    }
                    recipeCommentsLiveData.postValue(comments);
                } else {
                    // La solicitud no fue exitosa
                }
            }

            @Override
            public void onFailure(Call<List<RecipeApiComment>> call, Throwable t) {
                // Hubo un error en la solicitud
                Toast.makeText(context, "Failed to load data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public LiveData<List<Recipe>> getRecipesByName() {
        return recipesLiveData;
    }

    public void loadRecipesByName() {
        apiRequests.getRecipeByName(name, skipper).enqueue(new Callback<List<RecipeId_Recipe_User>>() {
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
                                recipeApi.getRecipeDetails().getRating(),
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

    public LiveData<List<Recipe>> getRecipesByCountry() {
        return recipesLiveData;
    }

    public void loadRecipesByCountry() {
        apiRequests.getRecipeByCountry(country, skipper).enqueue(new Callback<List<RecipeId_Recipe_User>>() {
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
                                recipeApi.getRecipeDetails().getRating(),
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

    public LiveData<List<Recipe>> getRecipesByIngredient() {
        return recipesLiveData;
    }

    public void loadRecipesByIngredient() {
        apiRequests.getRecipesByIngredient(ingredient, skipper).enqueue(new Callback<List<RecipeId_Recipe_User>>() {
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
                                recipeApi.getRecipeDetails().getRating(),
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

    public LiveData<List<Recipe>> getRecipesByTags() {
        return recipesLiveData;
    }

    public void loadRecipesByTags() {
        apiRequests.getRecipesByTags(tags, skipper).enqueue(new Callback<List<RecipeId_Recipe_User>>() {
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
                                recipeApi.getRecipeDetails().getRating(),
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

    public LiveData<List<Recipe>> getRandomRecipes() {
        return recipesLiveData;
    }

    public void loadRandomRecipes() {
        apiRequests.getRandomRecipes(limit).enqueue(new Callback<List<RecipeId_Recipe_User>>() {
            @Override
            public void onResponse(Call<List<RecipeId_Recipe_User>> call, Response<List<RecipeId_Recipe_User>> response) {
                if (response.isSuccessful() && response.body().size() != 0) {
                    List<RecipeId_Recipe_User> recipeApis = response.body();
                    List<Recipe> recipes = new ArrayList<>();
                    for (RecipeId_Recipe_User recipeApi : recipeApis) {
                        Recipe recipe = new Recipe(
                                recipeApi.getRecipeDetails().getName(),
                                recipeApi.getRecipeDetails().getDescription(),
                                (ArrayList<String>) recipeApi.getRecipeDetails().getSteps(),
                                recipeApi.getRecipeDetails().getDateCreated(),
                                recipeApi.getRecipeDetails().getDifficulty(),
                                recipeApi.getRecipeDetails().getRating(),
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
                Toast.makeText(context, "Something went wrong - " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
