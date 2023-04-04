package com.example.tasteit_java.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tasteit_java.ActivityRecipe;
import com.example.tasteit_java.ApiService.ApiClient;
import com.example.tasteit_java.ApiService.ApiRequests;
import com.example.tasteit_java.ApiService.RecipeId_Recipe_User;
import com.example.tasteit_java.R;
import com.example.tasteit_java.adapters.AdapterGridViewMain;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.Recipe;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentFollowedRecipes extends Fragment {

    private String token;
    private ProgressBar pbFollowedRecipes;
    private GridView gvRecipes;
    private AdapterGridViewMain adapter;
    public static ArrayList<Recipe> listRecipes = new ArrayList<>();

    private BdConnection app;

    public FragmentFollowedRecipes() {
        // Required empty public constructor
    }

    public static FragmentFollowedRecipes newInstance(String token) {
        FragmentFollowedRecipes fragment = new FragmentFollowedRecipes();
        Bundle args = new Bundle();
        args.putString("token", token);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            token = getArguments().getString("token");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_followed_recipes, container, false);

        pbFollowedRecipes = view.findViewById(R.id.pbFollowedRecipes);

        bringRecipes();

        gvRecipes = view.findViewById(R.id.gvRecipes);
        adapter = new AdapterGridViewMain(getContext(), listRecipes);
        gvRecipes.setAdapter(adapter);

        gvRecipes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int posicion, long l) {
                Intent i = new Intent(getContext(), ActivityRecipe.class);
                i.putExtra("recipeId", listRecipes.get(posicion).getId());
                startActivity(i);
            }
        });

        return view;
    }

    //carga de recetas asyncrona
    private class RecipesLoader {

        private final ApiRequests apiRequests;
        private final MutableLiveData<List<Recipe>> recipeLiveData;

        public RecipesLoader(ApiRequests apiRequests) {
            this.apiRequests = apiRequests;
            recipeLiveData = new MutableLiveData<>();
        }

        public LiveData<List<Recipe>> getRecipes() {
            return recipeLiveData;
        }

        public void loadRecipes() {

            apiRequests.getRecipesFollowed(token).enqueue(new Callback<List<RecipeId_Recipe_User>>() {
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
                        recipeLiveData.postValue(recipes);
                    } else {
                        // La solicitud no fue exitosa
                    }
                }

                @Override
                public void onFailure(Call<List<RecipeId_Recipe_User>> call, Throwable t) {
                    // Hubo un error en la solicitud
                    Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void onRecipesLoaded(List<Recipe> recipes) {
        // Actualizar la UI con la lista de recetas
        pbFollowedRecipes.setVisibility(View.GONE);

        listRecipes = (ArrayList<Recipe>) recipes;

        adapter = new AdapterGridViewMain(getContext(), listRecipes);
        gvRecipes.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }

    private void bringRecipes() {
        RecipesLoader recipesLoader = new RecipesLoader(ApiClient.getInstance().getService());

        recipesLoader.getRecipes().observe(getViewLifecycleOwner(), this::onRecipesLoaded);

        recipesLoader.loadRecipes();
    }
}