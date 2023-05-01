package com.example.tasteit_java.ApiService;

import com.example.tasteit_java.clases.User;
import com.example.tasteit_java.request.EditRecipeRequest;
import com.example.tasteit_java.request.RecipeRequest;
import com.google.protobuf.Api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiRequests {

    @GET("recipe/all")
    Call<List<RecipeId_Recipe_User>> getAllRecipes(); //devuelve todas las recetas TODO: hay que meterle un paginador

    @GET("recipe/all/{skipper}")
    Call<List<RecipeId_Recipe_User>> getRecipes(@Path("skipper") int skipper); //devuelve x cantidad de recetas (skipper es el limitador)

    @GET("recipe/{id}")
    Call<List<RecipeId_Recipe_User>> getRecipeById(@Path("id") int id); //devuelve receta por id

    @GET("recipe/byuser/{token}")
    Call<List<RecipeId_Recipe_User>> getRecipesByUser(@Path("token") String token); //devuelve las recetas de un usuario

    @GET("user/liked_recipes/{token}")
    Call<List<RecipeId_Recipe>> getRecipesLiked(@Path("token") String token); //devuelve las recetas a las que le das me gusta

    @GET("user/followers_recipes/{token}")
    Call<List<RecipeId_Recipe_User>> getRecipesFollowed(@Path("token") String token); //devuelve recetas de tus seguidores

    @GET("user/bytoken/{token}")
    Call<ApiUser> getUserByToken(@Path("token") String token); //Devuelve el usuario por uid

    @Headers("Content-Type: application/json")
    @POST("/recipe/create")
    Call<Void> createRecipe(@Body RecipeRequest recipeRequest);

    @Headers("Content-Type: application/json")
    @POST("/recipe/edit")
    Call<Void> editRecipe(@Body EditRecipeRequest editRecipeRequest);
}
