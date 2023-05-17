package com.example.tasteit_java.ApiRequest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecipeDeleteRequest {
    @SerializedName("RecipeId")
    @Expose
    private int recipeId;

    public RecipeDeleteRequest(int recipeId) {
        this.recipeId = recipeId;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }
}
