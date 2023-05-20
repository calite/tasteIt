package com.example.tasteit_java.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.tasteit_java.R;
import com.example.tasteit_java.adapters.AdapterListViewNewRecipe;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.TreeMap;

public class FragmentStepsNewRecipe extends Fragment {

    private static ArrayList<String> listSteps = new ArrayList<>();
    private static ListView lvSteps;
    private EditText etStep;
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

        etStep = view.findViewById(R.id.etStep);

        //añadir de steps
        bAddStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etStep.getText().toString().length() > 0) {
                    listSteps.add(etStep.getText().toString());
                    adapter.notifyDataSetChanged();
                    etStep.setText("");
                } else {
                    Toast.makeText(getContext(), R.string.error_step, Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    public static void setSteps(ArrayList<String> steps){
        listSteps = new ArrayList<>(steps);
    }
    public static ArrayList<String> getSteps() {
        return listSteps;
    }

}
