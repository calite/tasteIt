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
import com.example.tasteit_java.R;
import com.example.tasteit_java.adapters.AdapterEndlessRecyclerSearch;
import com.example.tasteit_java.adapters.AdapterFragmentProfile;
import com.example.tasteit_java.adapters.AdapterFragmentSearch;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.User;
import com.example.tasteit_java.clases.Utils;

import java.util.ArrayList;
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
    private ProgressBar pbSearch;
    private ArrayList<Object> dataListAux;
    private AdapterEndlessRecyclerSearch adapter;

    public FragmentSearch() {
        // Required empty public constructor
    }

    public FragmentSearch(String search, int dataView, ArrayList<Object> dataListAux) {
        this.search = search;
        this.dataView = dataView;
        this.dataListAux = dataListAux;
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

    public static FragmentSearch newInstance(String search, int dataView, ArrayList<Object> dataListAux) {
        FragmentSearch fragment = new FragmentSearch();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, dataView);
        args.putString(ARG_PARAM2, search);
        args.putSerializable(ARG_PARAM3, dataListAux);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //dataType = getArguments().getInt(ARG_PARAM1);
            search = getArguments().getString(ARG_PARAM2);
            dataView = getArguments().getInt(ARG_PARAM1);
            dataListAux = (ArrayList<Object>) getArguments().getSerializable(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        rvSearchItems = view.findViewById(R.id.rvSearchItems);
        rvSearchItems.setHasFixedSize(true);
        rvSearchItems.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new AdapterEndlessRecyclerSearch(rvSearchItems, dataListAux, search);
        rvSearchItems.setAdapter(adapter);

        changeDataType(dataView);

        return view;
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
                        if(temp.getTags().contains(search)) {
                            adapter.dataList.add(obj);
                        }
                    }
                }
                break;
            }
        }
        adapter.notifyDataSetChanged();
    }
}