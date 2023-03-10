package com.example.tasteit_java;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class FragmentStepsNewRecipe extends Fragment {

    private static ArrayList<String> listSteps = new ArrayList<>();
    private static ListView lvSteps;
    private static Button bAddStep;
    private static AdapterListViewNewRecipe adapter;


    public FragmentStepsNewRecipe() {
        // Required empty public constructor
    }

    public static FragmentStepsNewRecipe newInstance() {
        FragmentStepsNewRecipe fragment = new FragmentStepsNewRecipe();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_steps, container, false);
        //steps
        lvSteps = view.findViewById(R.id.lvSteps);
        adapter = new AdapterListViewNewRecipe(getContext(),listSteps);
        lvSteps.setAdapter(adapter);
        bAddStep = view.findViewById(R.id.bAddStep);
        //añadir de steps
        bAddStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listSteps.add("");
                adapter.notifyDataSetChanged();
            }
        });
        //borrar de steps
        lvSteps.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listSteps.remove(lvSteps.getPositionForView(view));
                return false;
            }
        });

        return view;
    }


    public static ArrayList<String> getSteps() {
        return listSteps;
    }
}
