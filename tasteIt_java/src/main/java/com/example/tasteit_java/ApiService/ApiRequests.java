package com.example.tasteit_java.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiRequests {

    @GET("recipe/all")
    Call<List<RecipeId_Recipe_User>> getAllRecipes(); //devuelve todas las recetas TODO: hay que meterle un paginador

    @GET("recipe/{id}")
    Call<List<RecipeId_Recipe_User>> getRecipeById(@Path("id") int id); //devuelve receta por id

    @GET("recipe/byuser/{token}")
    Call<List<RecipeId_Recipe_User>> getRecipesByUser(@Path("token") String token); //devuelve las recetas de un usuario

    @GET("user/liked_recipes/{token}")
    Call<List<RecipeId_Recipe>> getRecipesLiked(@Path("token") String token); //devuelve las recetas a las que le das me gusta

    @GET("user/followers_recipes/{token}")
    Call<List<RecipeId_Recipe_User>> getRecipesFollowed(@Path("token") String token); //devuelve recetas de tus seguidores
}
