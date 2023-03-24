package com.example.tasteit_java.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.tasteit_java.R;
import com.example.tasteit_java.adapters.AdapterListViewComments;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.Comment;
import com.example.tasteit_java.clases.Recipe;

import java.util.ArrayList;

public class FragmentCommentsRecipe extends Fragment {

    private Recipe recipe;
    private BdConnection connection;
    private ListView lvComments;
    private AdapterListViewComments adapter;



    public FragmentCommentsRecipe() {
        // Required empty public constructor
    }

    public static FragmentCommentsRecipe newInstance(Recipe recipe) {
        FragmentCommentsRecipe fragment = new FragmentCommentsRecipe();
        Bundle args = new Bundle();
        args.putParcelable("recipe", recipe);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipe = (Recipe) getArguments().getParcelable("recipe");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comments_recipe, container, false);

        connection = new BdConnection();

        ArrayList<Comment> listComments = connection.getCommentsOnRecipe(recipe.getId());

        adapter = new AdapterListViewComments(getContext(), listComments);
        lvComments = view.findViewById(R.id.lvComments);
        lvComments.setAdapter(adapter);

        return view;
    }
}