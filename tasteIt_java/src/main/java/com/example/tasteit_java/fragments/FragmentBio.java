package com.example.tasteit_java.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tasteit_java.ApiService.ApiClient;
import com.example.tasteit_java.ApiGetters.UserLoader;
import com.example.tasteit_java.R;
import com.example.tasteit_java.clases.User;
import com.example.tasteit_java.clases.Utils;

import java.util.HashMap;

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
    private String accessToken;

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
            accessToken = Utils.getUserAcessToken();
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bio, container, false);
        tvBiography = view.findViewById(R.id.tvBiography);
        bringUser();
        return view;
    }

    //Carga de usuario asyncrona
    private void bringUser() {
        UserLoader userLoader = new UserLoader(ApiClient.getInstance(accessToken).getService(), getContext(), uidProfile);
        userLoader.getAllUser().observe(getViewLifecycleOwner(), this::onUserLoaded);
        userLoader.loadAllUser();
    }

    private void onUserLoaded(HashMap<String, Object> userInfo) {
        User temp = (User) userInfo.get("user");
        biography = temp.getBiography();
        tvBiography.setText(biography);
    }
}