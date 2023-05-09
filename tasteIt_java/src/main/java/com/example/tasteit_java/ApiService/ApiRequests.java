package com.example.tasteit_java.ApiService;

import com.example.tasteit_java.request.RecipeEditRequest;
import com.example.tasteit_java.request.RecipeCommentRequest;
import com.example.tasteit_java.request.RecipeLikeRequest;
import com.example.tasteit_java.request.RecipeReportRequest;
import com.example.tasteit_java.request.RecipeRequest;
import com.example.tasteit_java.request.UserCommentRequest;
import com.example.tasteit_java.request.UserEditRequest;
import com.example.tasteit_java.request.UserFollowRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiRequests {

    //USUARIOS
    @GET("user/bytoken/{token}")
    Call<UserApi> getUserByToken(@Path("token") String token); //Devuelve el usuario por uid
    @GET("user/byname/{username}/{skipper}")
    Call<List<UserApi>> getUserByName(@Path("username") String username, @Path("skipper") int skipper); //Devuelve el usuario por username

    //DATOS USUARIO
    @GET("user/liked_recipes/{token}/{skipper}")
    Call<List<RecipeId_Recipe_User>> getRecipesLiked(@Path("token") String token, @Path("skipper") int skipper); //Devuelve las recetas a las que le da me gusta un usuario
    @GET("user/followers_recipes/{token}/{skipper}")
    Call<List<RecipeId_Recipe_User>> getRecipesFollowed(@Path("token") String token, @Path("skipper") int skipper); //Devuelve recetas de los seguidores de un usuario
    @GET("user/following_user/{sender_token}/{skipper}")
    Call<List<UserApi>> getFollowingUsers(@Path("sender_token") String sender_token, @Path("skipper") int skipper); //Devuelve los users que sigue un usuario
    @GET("user/followers_user/{sender_token}/{skipper}")
    Call<List<UserApi>> getFollowersUsers(@Path("sender_token") String sender_token, @Path("skipper") int skipper); //Devuelve a los seguidores de un usuario
    @GET("user/comments/{token}/{skipper}")
    Call<List<UserCommentApi>> getCommentsOnUser(@Path("token") String token, @Path("skipper") int skipper); //Devuelve los comentarios a un usuario

    //CONTADORES USUARIO
    @GET("user/recipes_created/{token}")
    Call<List<Integer>> getCountRecipes(@Path("token") String token); //Cuenta recetas creadas
    @GET("user/following/{token}")
    Call<List<Integer>> getCountFollowing(@Path("token") String token); //Cuenta de seguidores
    @GET("user/followers/{token}")
    Call<List<Integer>> getCountFollowers(@Path("token") String token); //Cuenta de seguidos
    @GET("user/recipes_liked/{token}")
    Call<List<Integer>> getCountRecipesLiked(@Path("token") String token); //Cuenta de recetas liked

    //POST USUARIO
    @Headers("Content-Type: application/json")
    @POST("user/register")
    Call<Void> userRegister(@Body UserApi userApi);
    @Headers("Content-Type: application/json")
    @POST("user/edit")
    Call<Void> editUser(@Body UserEditRequest userEditRequest);
    @Headers("Content-Type: application/json")
    @POST("user/follow")
    Call<Void> followUser(@Body UserFollowRequest userFollowRequest); //Seguir a usuario (comprueba si ya le sigue o no)
    @Headers("Content-Type: application/json")
    @POST("user/comment_user")
    Call<Void> commentUser(@Body UserCommentRequest userCommentRequest);

    //RECETAS
    @GET("recipe/all")
    Call<List<RecipeId_Recipe_User>> getAllRecipes(); //Devuelve todas las recetas
    @GET("recipe/all/{skipper}")
    Call<List<RecipeId_Recipe_User>> getRecipes(@Path("skipper") int skipper); //Devuelve x cantidad de recetas (skipper es el limitador)
    @GET("recipe/{id}")
    Call<List<RecipeId_Recipe_User>> getRecipeById(@Path("id") int id); //Devuelve receta por id
    @GET("recipe/byname/{name}/{skipper}")
    Call<List<RecipeId_Recipe_User>> getRecipeByName(@Path("name") String name, @Path("skipper") int skipper); //Devuelve las recetas que coincidan con el nombre
    @GET("recipe/bycountry/{country}/{skipper}")
    Call<List<RecipeId_Recipe_User>> getRecipeByCountry(@Path("country") String country, @Path("skipper") int skipper); //Devuelve las recetas que coincidan con el pais
    @GET("recipe/byuser/{token}/{skipper}")
    Call<List<RecipeId_Recipe_User>> getRecipesByUser(@Path("token") String token, @Path("skipper") int skipper); //Devuelve las recetas de un usuario
    @GET("recipe/byingredients/{ingredients}/{skipper}")
    Call<List<RecipeId_Recipe_User>> getRecipesByIngredient(@Path("ingredients") String ingredients, @Path("skipper") int skipper); //Devuelve las recetas por ingrediente
    @GET("recipe/bytags/{tags}/{skipper}")
    Call<List<RecipeId_Recipe_User>> getRecipesByTags(@Path("tags") String tags, @Path("skipper") int skipper); //Devuelve las recetas por tag

    //DATOS RECETA
    @GET("recipe/likes/{rid}")
    Call<List<RecipeId_Recipe_User>> getLikesOnRecipe(@Path("rid") int rid); //Usuarios que dan like a una receta
    @GET("recipe/comments/{rid}/{skipper}")
    Call<List<RecipeApiComment>> getCommentsOnRecipe(@Path("rid") int rid, @Path("skipper") int skipper); //Comentarios de una receta

    //POST RECETAS
    @Headers("Content-Type: application/json")
    @POST("/recipe/create")
    Call<Void> createRecipe(@Body RecipeRequest recipeRequest);
    @Headers("Content-Type: application/json")
    @POST("/recipe/edit")
    Call<Void> editRecipe(@Body RecipeEditRequest recipeEditRequest);
    @Headers("Content-Type: application/json")
    @POST("/recipe/comment_recipe")
    Call<Void> commentRecipe(@Body RecipeCommentRequest recipeCommentRequest);
    @Headers("Content-Type: application/json")
    @POST("/recipe/report_recipe")
    Call<Void> reportRecipe(@Body RecipeReportRequest recipeReportRequest);
    @Headers("Content-Type: application/json")
    @POST("/recipe/like")
    Call<Void> likeOnRecipe(@Body RecipeLikeRequest recipeLikeRequest);

    //COMPROBACIONES
    @GET("recipe/isliked/{rid}_{token}")
    Call<Boolean> getRecipeIsLiked(@Path("rid") int rid, @Path("token") String token); //Ver si un usuario tiene like en una receta
    @GET("user/following/{sender_token}_{receiver_token}")
    Call<Boolean> getIsFollowingUser(@Path("sender_token") String sender_token, @Path("receiver_token") String receiver_token); //Ver si un usuario sigue a otro usuario
}
