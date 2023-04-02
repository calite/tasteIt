package com.example.tasteit_java.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.tasteit_java.ActivityMyBook;
import com.example.tasteit_java.ActivityMain;
import com.example.tasteit_java.ActivityRandom;
import com.example.tasteit_java.ActivitySearch;
import com.example.tasteit_java.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class FragmentMainMenu extends Fragment {

    public FragmentMainMenu() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentMainMenu newInstance() {
        FragmentMainMenu fragment = new FragmentMainMenu();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_menu,container,false);
        //home button
        FloatingActionButton bHome = view.findViewById(R.id.bHome);
        bHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!getActivity().getLocalClassName().equals("ActivityMain")) {
                    Intent i = new Intent(getActivity().getApplicationContext(), ActivityMain.class);
                    getActivity().finish();
                    startActivity(i);
                } else {
                    //Si estamos en la main activity ya podemos actualizar simplemente las recetas
                }
            }
        });
        //search button
        FloatingActionButton bSearch = view.findViewById(R.id.bSearch);
        bSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity().getApplicationContext(), ActivitySearch.class);
                startActivity(i);
            }
        });
        //Random button
        FloatingActionButton bRandom = view.findViewById(R.id.bRandom);
        bRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity().getApplicationContext(), ActivityRandom.class);
                startActivity(i);
            }
        });
        //my book button
        FloatingActionButton bMyBook = view.findViewById(R.id.bMyBook);
        bMyBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity().getApplicationContext(), ActivityMyBook.class);
                startActivity(i);
            }
        });

        return view;
    }
}