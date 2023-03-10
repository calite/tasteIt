package com.example.tasteit_java;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.tasteit_java.clases.Recipe;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

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
    private GridView gvPhotos;
    private AdapterGridViewProfile adapter;


    // TODO: Rename and change types of parameters
    private ArrayList<Recipe> recipes;
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

    public static FragmentPhotos newInstance(ArrayList<Recipe> recipes) {
        FragmentPhotos fragment = new FragmentPhotos();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, recipes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipes = (ArrayList<Recipe>) getArguments().getSerializable(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos, container, false);

        adapter = new AdapterGridViewProfile(getContext(), recipes);
        gvPhotos = view.findViewById(R.id.gvPhotos);
        gvPhotos.setAdapter(adapter);

        gvPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(), ActivityRecipe.class);
                intent.putExtra("recipe", recipes.get(i));
                startActivity(intent);
            }
        });

        return view;

    }
}