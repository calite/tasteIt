package com.example.tasteit_java;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tasteit_java.adapters.AdapterGridViewMain;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ActivitySearch extends AppCompatActivity {
    private EditText tvSearch;
    private RadioButton rbName;
    private RadioButton rbTags;
    private RadioButton rbCountry;
    private RadioGroup rg;
    private GridView gvRecipes;
    private AdapterGridViewMain adapter;
    Logger log = Logger.getLogger(ActivityMain.class.getName());
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        BdConnection app = new BdConnection();  //Instanciamos la conexion

        ArrayList<Recipe> recipes = app.retrieveAllRecipes();
        ArrayList<Recipe> listRecipes = new ArrayList<>();
        //LOG RECIPES COUNTRY & NAME

        for(Recipe r: recipes) {
            log.log(Level.INFO, r.getName() + " " + r.getCountry()+" "+r.getTags().toString());
        }

        tvSearch = findViewById(R.id.tvSearch);
        rbName = findViewById(R.id.rbName);
        rbTags = findViewById(R.id.rbTags);
        rbCountry = findViewById(R.id.rbCountry);
        rg = findViewById(R.id.rg);
        gvRecipes = findViewById(R.id.gvRecipes);
        adapter = new AdapterGridViewMain(this, listRecipes);
        gvRecipes.setAdapter(adapter);

        tvSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String search = tvSearch.getText().toString();
                listRecipes.clear();
                if(!search.equals("")){
                    for(Recipe r: recipes){
                        if(rg.getCheckedRadioButtonId() == rbName.getId()){
                            //SEARCH BY NAME
                            if(r.getName().toLowerCase(Locale.ROOT).contains(search.toLowerCase())){
                                listRecipes.add(r);
                            }
                        }else if (rg.getCheckedRadioButtonId() == rbCountry.getId()) {
                            //SEARCH BY COUNTRY
                            if(r.getCountry().toLowerCase(Locale.ROOT).contains(search.toLowerCase())){
                                listRecipes.add(r);
                            }
                        }else if (rg.getCheckedRadioButtonId() == rbTags.getId()){
                            //SEARCH BY TAGS

                        for(String tag: r.getTags()){
                            if(tag.contains(search.toLowerCase())){
                                listRecipes.add(r);
                            }
                        }


                        }else{
                            //SEARCH BY ALL
                            if(r.getCountry().toLowerCase(Locale.ROOT).contains(search.toLowerCase()) || r.getName().toLowerCase(Locale.ROOT).contains(search.toLowerCase()) || r.getTags().contains(search.toLowerCase())){
                                listRecipes.add(r);
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }



            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //CLEAR GRIDVIEW WHEN SEARCH PARAMETER IS CHANGED
                tvSearch.setText("");
                listRecipes.clear();
                adapter.notifyDataSetChanged();
            }
        });

        //menu superior
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search");
        //fragment menu inferior
        /*
        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        FragmentMainMenu mainMenuFargment = new FragmentMainMenu();

        //comprobamos si existe
        if(savedInstanceState == null) {
            ft.add(R.id.fcMainMenu, mainMenuFargment);
            ft.commit();
        }
        */

        gvRecipes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), ActivityRecipe.class);
                i.putExtra("recipe",listRecipes.get(position));
                Bundle params = getIntent().getExtras();
                User user = (User) params.getSerializable("user");
                i.putExtra("user",user);
                startActivity(i);
            }
        });
    }


    //MENU superior
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.iProfile:
                startActivity(new Intent(getApplicationContext(), ActivityProfile.class));
                return true;
            case R.id.iCloseSesion:
                signOut();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //END MENU superior
    //LOGOUT
    public void callSignOut(View view){
        signOut();
    }
    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        startActivity (new Intent(this, ActivityLogin.class));
    }



}