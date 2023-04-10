package com.example.tasteit_java.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.example.tasteit_java.R;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

public class FragmentInfoNewRecipe extends Fragment {

    private static EditText etRecipeName;
    private static EditText etDescriptionRecipe;
    private static Spinner spCountries;
    private static SeekBar skDificulty;
    private ChipGroup cgTags;
    private static ArrayList<String> tags = new ArrayList<>();
    private Button bAddTag;
    private static EditText etTagName;

    public FragmentInfoNewRecipe() {
        // Required empty public constructor
    }

    public static FragmentInfoNewRecipe newInstance() {
        FragmentInfoNewRecipe fragment = new FragmentInfoNewRecipe();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_info, container, false);

        //textbox
        etRecipeName = view.findViewById(R.id.etRecipeName);
        etDescriptionRecipe = view.findViewById(R.id.etDescripcionRecipe);
        //seekbar
        skDificulty = view.findViewById(R.id.skDificulty);
        //spinner
        spCountries = view.findViewById(R.id.spCountry);
        ArrayAdapter<CharSequence> adapterSpinnerCountries = ArrayAdapter.createFromResource(getContext(),
                R.array.countries_array, android.R.layout.simple_spinner_item);
        adapterSpinnerCountries.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCountries.setAdapter(adapterSpinnerCountries);
        //tags
        /*
        cgTags = view.findViewById(R.id.cgTags);
        bAddTag = view.findViewById(R.id.bAddTag);
        etTagName = view.findViewById(R.id.etTagName);

        bAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Chip chip = new Chip(getContext());
                chip.setText(etTagName.getText().toString());
                chip.setCloseIconResource(R.drawable.ic_close);
                chip.setCloseIconVisible(true);
                chip.setChipBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.maroon)));
                chip.setTextColor(Color.WHITE);
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tags.remove(etTagName.getText().toString());
                        cgTags.removeView(chip);
                    }
                });
                cgTags.addView(chip);
                tags.add(etTagName.getText().toString());
                etTagName.setText("");
            }
        });
        */
        return view;
    }

    public static EditText getRecipeName(){
        return etRecipeName;
    }

    public static void setRecipeName(String name) {etRecipeName.setText(name);}

    public static EditText getDescriptionRecipe() { return etDescriptionRecipe; }

    public static void setDescriptionRecipe(String desc) {etDescriptionRecipe.setText(desc);}

    public static EditText getEtTagName() { return etTagName; }

    public static void setEtTagName(String tag) {etTagName.setText(tag);}

    public static String getCountry() {
        return spCountries.getSelectedItem().toString();
    }


    public static void setCountry(String countryName) {
        ArrayList<String> countryList = new ArrayList<>();
        countryList.add("Spain"); countryList.add("France"); countryList.add("Italy"); countryList.add("Portugal"); countryList.add("Greece"); countryList.add("Germany"); countryList.add("Turkey"); countryList.add("England");
        spCountries.setSelection(countryList.indexOf(countryName));
    }

    public static int getDificulty() {
      return skDificulty.getProgress();
    }

    public static void setDificulty(int dificulty){
        skDificulty.setProgress(dificulty);
    }

    public static ArrayList getTags() {
        return tags;
    }

    public static void setTags(ArrayList tagsReceta) {
        tags = tagsReceta;
    }
}