package com.example.tasteit_java.fragments;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tasteit_java.ActivityMain;
import com.example.tasteit_java.ActivityProfile;
import com.example.tasteit_java.ActivitySearch;
import com.example.tasteit_java.ApiService.ApiClient;
import com.example.tasteit_java.ApiService.ApiRequests;
import com.example.tasteit_java.ApiService.RecipeId_Recipe_User;
import com.example.tasteit_java.ApiUtils.RecipeLoader;
import com.example.tasteit_java.ApiUtils.UserLoader;
import com.example.tasteit_java.R;
import com.example.tasteit_java.adapters.AdapterEndlessRecyclerSearch;
import com.example.tasteit_java.adapters.AdapterFragmentProfile;
import com.example.tasteit_java.adapters.AdapterFragmentSearch;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.OnLoadMoreListener;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.SharedPreferencesSaved;
import com.example.tasteit_java.clases.User;
import com.example.tasteit_java.clases.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSearch#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSearch extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";


    // TODO: Rename and change types of parameters
    private String search;
    private int dataView;
    private RecyclerView rvSearchItems;
    private ShimmerFrameLayout shimmer;
    private int skipper;
    private int allItemsCount;
    private boolean allItemsLoaded;
    private AdapterEndlessRecyclerSearch adapter;
    private List<Object> dataListAux;

    public FragmentSearch() {
        // Required empty public constructor
    }

    public FragmentSearch(String search, int dataView) {
        this.search = search;
        this.dataView = dataView;
        dataListAux = new ArrayList<>();
        skipper = 0;
        allItemsCount = 0;
        allItemsLoaded = false;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSearch.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentSearch newInstance(String param1, String param2) {
        FragmentSearch fragment = new FragmentSearch();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            search = getArguments().getString(ARG_PARAM1);
            dataView = Integer.parseInt(getArguments().getString(ARG_PARAM2));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        shimmer = view.findViewById(R.id.shimmer);
        shimmer.startShimmer();

        if(search.length() == 0) {
            bringRecipes();
        } else {

        }

        rvSearchItems = view.findViewById(R.id.rvSearchItems);
        rvSearchItems.setHasFixedSize(true);
        rvSearchItems.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new AdapterEndlessRecyclerSearch(rvSearchItems, search);
        rvSearchItems.setAdapter(adapter);

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if(allItemsLoaded) { //habra que ponerle un limite (que en principio puede ser el total de recipes en la bbdd o algo fijo para no sobrecargar el terminal)
                    Toast.makeText(getContext(), "Finiquitao con " + adapter.getItemCount() + " recetas", Toast.LENGTH_SHORT).show();
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

    public void changeDataType(int position) {
        switch (position) {
            case 0: {
                adapter.dataList.clear();
                adapter.dataList.addAll(dataListAux);
                break;
            }
            case 1: {
                adapter.dataList.clear();
                for (Object obj : dataListAux) {
                    if(obj instanceof User) {
                        adapter.dataList.add(obj);
                    }
                }
                break;
            }
            case 2: {
                adapter.dataList.clear();
                for (Object obj : dataListAux) {
                    if(obj instanceof Recipe) {
                        adapter.dataList.add(obj);
                    }
                }
                break;
            }
            case 3: {
                adapter.dataList.clear();
                for (Object obj : dataListAux) {
                    if(obj instanceof Recipe) {
                        Recipe temp = (Recipe) obj;
                        if(temp.getIngredients().contains(search)) {
                            adapter.dataList.add(obj);
                        }
                    }
                }
                break;
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void bringRecipes() {
        String accessToken = new SharedPreferencesSaved(getContext()).getSharedPreferences().getString("accessToken", "null");
        RecipeLoader recipesLoader = new RecipeLoader(ApiClient.getInstance(accessToken).getService(), getContext(), 0);
        recipesLoader.getAllRecipesWSkipper().observe(getViewLifecycleOwner(), this::onRecipesLoaded);
        recipesLoader.loadAllRecipesWSkipper();
    }

    private void bringUserFollowing() {
        String accessToken = new SharedPreferencesSaved(getContext()).getSharedPreferences().getString("accessToken", "null");
        String uidProfile = new SharedPreferencesSaved(getContext()).getSharedPreferences().getString("uid", "null");

        UserLoader userLoader = new UserLoader(ApiClient.getInstance(accessToken).getService(), getContext(), uidProfile, 0);
        userLoader.getFollowingByUser().observe(getViewLifecycleOwner(), this::onUsersLoaded);
        userLoader.loadFollowingByUser();
    }

    private void onRecipesLoaded(List<Recipe> recipes) {
        if(adapter.getItemCount() > 0) {
            if(adapter.getItemViewType(adapter.getItemCount() - 1) != 0) {
                adapter.dataList.remove(adapter.getItemCount() - 1);
            } else if(adapter.getItemViewType(0) != 0) {
                adapter.dataList.remove(0);
            }
        }

        dataListAux.addAll(recipes);
        bringUserFollowing();
    }

    private void onUsersLoaded(List<User> users) {
        if(adapter.getItemCount() > 0) {
            if(adapter.getItemViewType(adapter.getItemCount() - 1) != 0) {
                adapter.dataList.remove(adapter.getItemCount() - 1);
            } else if(adapter.getItemViewType(0) != 0) {
                adapter.dataList.remove(0);
            }
        }

        dataListAux.addAll(users);
        Collections.shuffle(dataListAux);
        changeDataType(dataView);
        adapter.setLoaded();

        if(adapter.dataList.size() != allItemsCount) {
            allItemsCount = adapter.dataList.size();
        } else {
            allItemsLoaded = true;
        }

        shimmer.stopShimmer();
        shimmer.setVisibility(View.GONE);
    }
}