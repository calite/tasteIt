package com.example.tasteit_java.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tasteit_java.ActivityProfile;
import com.example.tasteit_java.R;
import com.example.tasteit_java.adapters.AdapterFragmentComments;
import com.example.tasteit_java.bdConnection.BdConnection;

import org.neo4j.driver.Query;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentComments#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentComments extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static ArrayList<String> uidsComments;
    private static ArrayList<String> comments;

    private AdapterFragmentComments adapter;
    private ListView lvComments;

    public FragmentComments() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VideosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentComments newInstance(String param1, String param2) {
        FragmentComments fragment = new FragmentComments();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static FragmentComments newInstance(HashMap<String, String> userComments) {
        FragmentComments fragment = new FragmentComments();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, userComments);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            HashMap<String, String> userComments = (HashMap<String, String>) getArguments().getSerializable(ARG_PARAM1);

            Set<String> keySet = userComments.keySet();
            uidsComments = new ArrayList<String>(keySet);

            Collection<String> values = userComments.values();
            comments = new ArrayList<String>(values);

            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comments, container, false);

        adapter = new AdapterFragmentComments(getContext(), uidsComments, comments);
        lvComments = view.findViewById(R.id.lvComments);
        lvComments.setAdapter(adapter);

        lvComments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity().getApplicationContext(), ActivityProfile.class);
                intent.putExtra("uid", uidsComments.get(i));
                startActivity(intent);
            }
        });

        return view;
    }
}