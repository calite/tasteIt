package com.example.tasteit_java.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tasteit_java.ApiService.ApiClient;
import com.example.tasteit_java.ApiGetters.RecipeLoader;
import com.example.tasteit_java.R;
import com.example.tasteit_java.adapters.AdapterRecyclerPhotosProfile;
import com.example.tasteit_java.clases.OnLoadMoreListener;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

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
    private RecyclerView rvGridPhotos;
    private AdapterRecyclerPhotosProfile adapter;
    private ShimmerFrameLayout shimmer;
    private int skipper;
    private int allItemsCount;
    private boolean allItemsLoaded;
    private String accessToken;

    // TODO: Rename and change types of parameters
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
            accessToken = Utils.getUserAcessToken();
            skipper = 0;
            allItemsCount = 0;
            allItemsLoaded = false;
            getArguments().clear();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos, container, false);

        shimmer = view.findViewById(R.id.shimmer);
        shimmer.startShimmer();

        bringRecipes();

        rvGridPhotos = view.findViewById(R.id.rvGridPhotos);
        rvGridPhotos.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = new AdapterRecyclerPhotosProfile(getContext(), rvGridPhotos);
        rvGridPhotos.setAdapter(adapter);

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if(allItemsLoaded) { //habra que ponerle un limite (que en principio puede ser el total de recipes en la bbdd o algo fijo para no sobrecargar el terminal)
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
                updateList();
            }
        });

        return view;
    }

    public void updateList() {
        skipper = 0;
        allItemsCount = 0;
        allItemsLoaded = false;
        adapter.dataList.clear();
        adapter.notifyDataSetChanged();

        shimmer.setVisibility(View.VISIBLE);
        shimmer.startShimmer();

        bringRecipes();
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
        RecipeLoader recipesLoader = new RecipeLoader(ApiClient.getInstance(accessToken).getService(), getContext(), uidParam, skipper);
        recipesLoader.getRecipesByUser().observe(getViewLifecycleOwner(), this::onRecipesLoaded);
        recipesLoader.loadRecipesByUser();
    }
}