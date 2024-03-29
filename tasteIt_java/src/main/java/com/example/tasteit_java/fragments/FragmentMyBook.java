package com.example.tasteit_java.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasteit_java.ApiService.ApiClient;
import com.example.tasteit_java.ApiGetters.RecipeLoader;
import com.example.tasteit_java.R;
import com.example.tasteit_java.adapters.AdapterEndlessRecyclerMain;
import com.example.tasteit_java.clases.OnLoadMoreListener;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

public class FragmentMyBook extends Fragment {
    private int dataView;
    private String token;
    private int skipper;
    private RecyclerView rvRecipes;
    private ShimmerFrameLayout shimmer;
    private AdapterEndlessRecyclerMain adapter;
    private String accessToken;
    private int allItemsCount;
    private boolean allItemsLoaded;

    public FragmentMyBook() {
        // Required empty public constructor
    }

    public FragmentMyBook(String token, int dataView) {
        this.token = token;
        this.dataView = dataView;

        accessToken = Utils.getUserAcessToken();
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
            getArguments().clear();
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

        skipper = 0;
        allItemsCount = 0;
        allItemsLoaded = false;
        bringRecipes();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvRecipes.setLayoutManager(linearLayoutManager);

        adapter = new AdapterEndlessRecyclerMain(rvRecipes);
        rvRecipes.setAdapter(adapter);

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if(allItemsLoaded) {
                    //Toast.makeText(getContext(), "Finiquitao con " + adapter.getItemCount() + " recetas", Toast.LENGTH_SHORT).show();
                } else {
                    adapter.dataList.add(null);
                    adapter.notifyItemInserted(adapter.getItemCount() - 1);

                    skipper += 10;
                    bringRecipes();
                }
            }

            @Override
            public void update() {
                skipper = 0;
                allItemsCount = 0;
                allItemsLoaded = false;
                adapter.dataList.clear();
                bringRecipes();
            }
        });

        return view;
    }

    private void onRecipesLoaded(List<Recipe> recipes) {
        if(adapter.getItemCount() > 0) {
            if(adapter.getItemViewType(adapter.getItemCount() - 1) != 0) {
                adapter.dataList.remove(adapter.getItemCount() - 1);
            } else if(adapter.getItemViewType(0) != 0) {
                adapter.dataList.remove(0);
            }
        }

        adapter.dataList.addAll(recipes);
        adapter.setLoaded();
        adapter.notifyDataSetChanged();

        if(adapter.dataList.size() != allItemsCount) {
            allItemsCount = adapter.dataList.size();
        } else {
            allItemsLoaded = true;
        }

        shimmer.stopShimmer();
        shimmer.setVisibility(View.GONE);
    }

    private void bringRecipes() {
        switch (dataView) {
            case 0: {
                RecipeLoader recipesLoader = new RecipeLoader(ApiClient.getInstance(accessToken).getService(), getContext(), token, skipper);
                recipesLoader.getRecipesByUser().observe(getViewLifecycleOwner(), this::onRecipesLoaded);
                recipesLoader.loadRecipesByUser();
                break;
            }
            case 1: {
                RecipeLoader recipesLoader = new RecipeLoader(ApiClient.getInstance(accessToken).getService(), getContext(), token, skipper);
                recipesLoader.getRecipesLikedByUser().observe(getViewLifecycleOwner(), this::onRecipesLoaded);
                recipesLoader.loadRecipesLikedByUser();
                break;
            }
            case 2: {
                RecipeLoader recipesLoader = new RecipeLoader(ApiClient.getInstance(accessToken).getService(), getContext(), token, skipper);
                recipesLoader.getRecipesFollowedByUser().observe(getViewLifecycleOwner(), this::onRecipesLoaded);
                recipesLoader.loadRecipesFollowedByUser();
                break;
            }
        }
    }
}