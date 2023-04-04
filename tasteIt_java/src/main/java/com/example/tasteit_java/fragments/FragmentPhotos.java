package com.example.tasteit_java.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.tasteit_java.ActivityRecipe;
import com.example.tasteit_java.R;
import com.example.tasteit_java.adapters.AdapterGridViewProfile;
import com.example.tasteit_java.adapters.AdapterRecyclerPhotosProfile;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.Recipe;

import java.util.ArrayList;

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
    //private GridView gvPhotos;
    //private AdapterGridViewProfile adapter;
    private RecyclerView rvGridPhotos;
    private AdapterRecyclerPhotosProfile adapter;
    private ArrayList<Integer> recipeIds;


    // TODO: Rename and change types of parameters
    //private ArrayList<Recipe> recipes;
    private String uidParam;
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

    public static FragmentPhotos newInstance(String uid) {
        FragmentPhotos fragment = new FragmentPhotos();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uidParam = getArguments().getString(ARG_PARAM1);
            recipeIds = new ArrayList<>();
            new TaskLoadUserPhotos().execute();
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos, container, false);

        adapter = new AdapterRecyclerPhotosProfile(getContext(), uidParam);
        rvGridPhotos = view.findViewById(R.id.rvGridPhotos);
        rvGridPhotos.setAdapter(adapter);

        rvGridPhotos.setLayoutManager(new GridLayoutManager(getContext(), 3));

        final GestureDetector mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });

        rvGridPhotos.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                try {
                    View child = rv.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && mGestureDetector.onTouchEvent(e)) {

                        int position = rv.getChildAdapterPosition(child);

                        Intent intent = new Intent(getContext(), ActivityRecipe.class);
                        intent.putExtra("recipeId", recipeIds.get(position));
                        startActivity(intent);

                        return true;
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }

                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        /*rvGridPhotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(), ActivityRecipe.class);
                intent.putExtra("recipeId", recipeIds.get(i));
                startActivity(intent);
            }
        });*/
        return view;

    }

    class TaskLoadUserPhotos extends AsyncTask<ArrayList<Integer>, Void,ArrayList<Integer>> {
        @Override
        protected void onPreExecute() {

        }
        @Override
        protected ArrayList<Integer> doInBackground(ArrayList<Integer>... hashMaps) {
            for(Recipe temp : new BdConnection().retrieveAllRecipesbyUid(uidParam)) {
                recipeIds.add(temp.getId());
            }
            return recipeIds;
        }
        @Override
        protected void onPostExecute(ArrayList<Integer> ids) {
            //super.onPostExecute(recipes);
            recipeIds = ids;
            adapter.notifyDataSetChanged();
        }
    }
}