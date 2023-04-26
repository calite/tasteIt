package com.example.tasteit_java.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasteit_java.ApiService.ApiClient;
import com.example.tasteit_java.ApiService.ApiRequests;
import com.example.tasteit_java.ApiService.RecipeId_Recipe;
import com.example.tasteit_java.ApiService.RecipeId_Recipe_User;
import com.example.tasteit_java.R;
import com.example.tasteit_java.adapters.AdapterEndlessRecyclerMain;
import com.example.tasteit_java.clases.Recipe;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentMyBook extends Fragment {
    private int dataView;
    private String token;
    private RecyclerView rvRecipes;
    private ShimmerFrameLayout shimmer;
    private AdapterEndlessRecyclerMain adapter;

    public FragmentMyBook() {
        // Required empty public constructor
    }

    public FragmentMyBook(String token, int dataView) {
        this.token = token;
        this.dataView = dataView;
    }

    public static FragmentMyBook newInstance(String token) {
        FragmentMyBook fragment = new FragmentMyBook();
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
        View view = inflater.inflate(R.layout.fragment_my_book, container, false);
        shimmer = view.findViewById(R.id.shimmer);
        shimmer.startShimmer();

        rvRecipes = view.findViewById(R.id.rvRecipes);
        rvRecipes.setHasFixedSize(true);

        bringRecipes();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvRecipes.setLayoutManager(linearLayoutManager);

        adapter = new AdapterEndlessRecyclerMain(rvRecipes);
        rvRecipes.setAdapter(adapter);

        /*adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if(adapter.getItemCount() > 28) { //habra que ponerle un limite (que en principio puede ser el total de recipes en la bbdd o algo fijo para no sobrecargar el terminal)
                    Toast.makeText(getContext(), "Finiquitao con " + adapter.getItemCount() + " recetas", Toast.LENGTH_SHORT).show();
                } else {
                    adapter.dataList.add(null);
                    adapter.notifyItemInserted(adapter.getItemCount() - 1);

                    //skipper += 10;
                    //bringRecipes();
                }
            }

            @Override
            public void update() {
                adapter.dataList.add(0, null);
                adapter.notifyItemInserted(0);

                skipper = 0;
                adapter.dataList.clear();
                bringRecipes();
            }
        });*/

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
            switch (dataView) {
                case 0: {
                    apiRequests.getRecipesByUser(token).enqueue(new Callback<List<RecipeId_Recipe_User>>() {
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
                    break;
                }
                case 1: {
                    apiRequests.getRecipesLiked(token).enqueue(new Callback<List<RecipeId_Recipe>>() {
                        @Override
                        public void onResponse(Call<List<RecipeId_Recipe>> call, Response<List<RecipeId_Recipe>> response) {
                            if (response.isSuccessful()) {
                                List<RecipeId_Recipe> recipesApi = response.body();
                                List<Recipe> recipes = new ArrayList<>();

                                //tratamos los datos
                                for (RecipeId_Recipe recipeApi : recipesApi) {
                                    Recipe recipe = new Recipe(
                                            recipeApi.getRecipeDetails().getName(),
                                            recipeApi.getRecipeDetails().getDescription(),
                                            (ArrayList<String>) recipeApi.getRecipeDetails().getSteps(),
                                            recipeApi.getRecipeDetails().getDateCreated(),
                                            recipeApi.getRecipeDetails().getDifficulty(),
                                            "kek",
                                            recipeApi.getRecipeDetails().getImage(),
                                            recipeApi.getRecipeDetails().getCountry(),
                                            (ArrayList<String>) recipeApi.getRecipeDetails().getTags(),
                                            (ArrayList<String>) recipeApi.getRecipeDetails().getIngredients(),
                                            recipeApi.getRecipeId(),
                                            "kek"
                                    );
                                    recipes.add(recipe);
                                }
                                recipeLiveData.postValue(recipes);
                            } else {
                                // La solicitud no fue exitosa
                            }
                        }
                        @Override
                        public void onFailure(Call<List<RecipeId_Recipe>> call, Throwable t) {
                            // Hubo un error en la solicitud
                            Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                }
                case 2: {
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
                    break;
                }
            }
        }
    }

    private void onRecipesLoaded(List<Recipe> recipes) {
        shimmer.stopShimmer();
        shimmer.setVisibility(View.GONE);

        /*if(adapter.getItemCount() > 0) {
            adapter.dataList.remove(adapter.getItemCount() - 1);
        }*/

        adapter.dataList.addAll(recipes);
        //adapter.setLoaded();
        adapter.notifyDataSetChanged();
    }

    private void bringRecipes() {
        RecipesLoader recipesLoader = new RecipesLoader(ApiClient.getInstance().getService());
        recipesLoader.getRecipes().observe(getViewLifecycleOwner(), this::onRecipesLoaded);
        recipesLoader.loadRecipes();
    }
}