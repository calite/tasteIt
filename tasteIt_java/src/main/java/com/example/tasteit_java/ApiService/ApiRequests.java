package com.example.tasteit_java.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiRequests {

    @GET("recipe/all")
    Call<List<RecipeApi>> getAllRecipes();

    @GET("recipe/{id}")
    Call<List<RecipeApi>> getRecipeById(@Path("id") int id);
}
