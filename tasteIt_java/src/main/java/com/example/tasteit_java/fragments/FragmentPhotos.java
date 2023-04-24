package com.example.tasteit_java.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.tasteit_java.ActivityMain;
import com.example.tasteit_java.ActivityRecipe;
import com.example.tasteit_java.ApiService.ApiClient;
import com.example.tasteit_java.ApiService.ApiRequests;
import com.example.tasteit_java.ApiService.RecipeId_Recipe;
import com.example.tasteit_java.ApiService.RecipeId_Recipe_User;
import com.example.tasteit_java.R;
import com.example.tasteit_java.adapters.AdapterGridViewMain;
import com.example.tasteit_java.adapters.AdapterGridViewProfile;
import com.example.tasteit_java.adapters.AdapterRecyclerPhotosProfile;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.Recipe;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentPhotos#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentPhotos extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //VARIABLES PARA EL GRID
    //private GridView gvPhotos;
    //private AdapterGridViewProfile adapter;
    private RecyclerView rvGridPhotos;
    private AdapterRecyclerPhotosProfile adapter;

    // TODO: Rename and change types of parameters
    //private ArrayList<Recipe> recipes;
    private String uidParam;
    private String mParam2;

    public FragmentPhotos() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PhotosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentPhotos newInstance(String param1, String param2) {
        FragmentPhotos fragment = new FragmentPhotos();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static FragmentPhotos newInstance(String uid) {
        FragmentPhotos fragment = new FragmentPhotos();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uidParam = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos, container, false);

        bringRecipes();

        adapter = new AdapterRecyclerPhotosProfile(getContext());
        rvGridPhotos = view.findViewById(R.id.rvGridPhotos);
        rvGridPhotos.setAdapter(adapter);

        rvGridPhotos.setLayoutManager(new GridLayoutManager(getContext(), 3));

        return view;
    }

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
            apiRequests.getRecipesByUser(uidParam).enqueue(new Callback<List<RecipeId_Recipe_User>>() {
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
        }
    }

    private void onRecipesLoaded(List<Recipe> recipes) {
        if(adapter.getItemCount() > 0) {
            adapter.arrayListPhotos.remove(adapter.getItemCount() - 1);
        }

        adapter.arrayListPhotos.addAll(recipes);
        adapter.notifyDataSetChanged();
    }

    private void bringRecipes() {
        //olvidamos asynctask y metemos lifecycle, que es mas actual y esta mejor optimizado
        RecipesLoader recipesLoader = new RecipesLoader(ApiClient.getInstance().getService());
        recipesLoader.getRecipes().observe(this, this::onRecipesLoaded);
        recipesLoader.loadRecipes();
    }
}