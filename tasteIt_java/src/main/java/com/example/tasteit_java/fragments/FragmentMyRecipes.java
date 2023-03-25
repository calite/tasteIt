package com.example.tasteit_java.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.tasteit_java.ActivityRecipe;
import com.example.tasteit_java.R;
import com.example.tasteit_java.adapters.AdapterGridViewMain;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.Recipe;

import java.util.ArrayList;

public class FragmentMyRecipes extends Fragment {

    private String token;
    private GridView gvRecipes;
    private AdapterGridViewMain adapter;
    public static ArrayList<Recipe> listRecipes = new ArrayList<>();

    private BdConnection app;

    public FragmentMyRecipes() {
        // Required empty public constructor
    }

    public static FragmentMyRecipes newInstance(String token) {
        FragmentMyRecipes fragment = new FragmentMyRecipes();
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
        View view = inflater.inflate(R.layout.fragment_my_recipes, container, false);

        app = new BdConnection();  //Instanciamos la conexion

        listRecipes = app.retrieveAllRecipesbyUid(token);
        gvRecipes = view.findViewById(R.id.gvRecipes);
        adapter = new AdapterGridViewMain(getContext(), listRecipes);
        gvRecipes.setAdapter(adapter);

        gvRecipes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int posicion, long l) {
                Intent i = new Intent(getContext(), ActivityRecipe.class);
                i.putExtra("recipe", listRecipes.get(posicion));
                startActivity(i);
            }
        });

        return view;
    }
}