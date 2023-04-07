package com.example.tasteit_java.fragments;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tasteit_java.R;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.Comment;
import com.example.tasteit_java.clases.User;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentBio#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentBio extends Fragment {

    private TextView tvBiography;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String biography;

    private String uidProfile;
    private String mParam2;

    public FragmentBio() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BioFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentBio newInstance(String param1, String param2) {
        FragmentBio fragment = new FragmentBio();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static FragmentBio newInstance(String uid) {
        FragmentBio fragment = new FragmentBio();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uidProfile = getArguments().getString(ARG_PARAM1);
            new TaskLoadUserBio().execute();
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bio, container, false);
        tvBiography = view.findViewById(R.id.tvBiography);
        tvBiography.setText(biography);
        return view;
    }

    public void updateBio(String biography) {
        this.biography = "";
        if(!this.biography.equals(biography)) {
                this.biography = biography;
                tvBiography.setText(biography);
        }
    }

    class TaskLoadUserBio extends AsyncTask<User, Void,User> {
        @Override
        protected void onPreExecute() {

        }
        @Override
        protected User doInBackground(User... hashMaps) {
            return new BdConnection().retrieveUserbyUid(uidProfile);
        }
        @Override
        protected void onPostExecute(User userBio) {
            //super.onPostExecute(recipes);
            updateBio(userBio.getBiography());
        }
    }
}