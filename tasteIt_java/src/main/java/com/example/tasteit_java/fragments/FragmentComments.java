package com.example.tasteit_java.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tasteit_java.ActivityProfile;
import com.example.tasteit_java.R;
import com.example.tasteit_java.adapters.AdapterFragmentComments;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.User;
import com.example.tasteit_java.clases.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;

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
    private static final String ARG_PARAM3 = "param3";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ArrayList<String> uidsComments;
    private ArrayList<String> comments;
    private String uid;
    private Boolean myProfile;
    private FloatingActionButton btnComment;
    private AdapterFragmentComments adapter;
    private ListView lvComments;
    private ConstraintLayout clComment;
    private ShapeableImageView ivMyPhoto;
    private EditText etComment;
    private Button btnAddComment;

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

    public static FragmentComments newInstance(HashMap<String, String> userComments, Boolean myProfile) {
        FragmentComments fragment = new FragmentComments();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, userComments);
        args.putBoolean(ARG_PARAM2, myProfile);
        fragment.setArguments(args);
        return fragment;
    }

    public static FragmentComments newInstance(HashMap<String, String> userComments, Boolean myProfile, String uid) {
        FragmentComments fragment = new FragmentComments();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, userComments);
        args.putBoolean(ARG_PARAM2, myProfile);
        args.putString(ARG_PARAM3, uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            HashMap<String, String> userComments = (HashMap<String, String>) getArguments().getSerializable(ARG_PARAM1);

            Set<String> keySet = userComments.keySet();
            uidsComments = new ArrayList<>(keySet);

            Collection<String> values = userComments.values();
            comments = new ArrayList<>(values);

            this.myProfile = getArguments().getBoolean(ARG_PARAM2);

            if(getArguments().getString(ARG_PARAM3) != null) {
                this.uid = getArguments().getString(ARG_PARAM3);
            }

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

        clComment = view.findViewById(R.id.clComment);
        ivMyPhoto = view.findViewById(R.id.ivMyPhoto);
        etComment = view.findViewById(R.id.etComment);
        btnAddComment = view.findViewById(R.id.btnAddComment);

        hideAddComment(myProfile);

        return view;
    }

    public void hideAddComment(Boolean visibility) {
        if(!visibility) {
            clComment.setVisibility(View.VISIBLE);
            clComment.setClickable(true);

            User user = new BdConnection().retrieveUserbyUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
            Bitmap bitmap = Utils.decodeBase64(user.getImgProfile());
            ivMyPhoto.setImageBitmap(bitmap);

            btnAddComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new BdConnection().commentUser(FirebaseAuth.getInstance().getCurrentUser().getUid(), uid, etComment.getText().toString());
                    uidsComments.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    comments.add(etComment.getText().toString());
                    adapter.notifyDataSetChanged();
                    etComment.setText("");
                }
            });
        } else {
            clComment.setVisibility(View.INVISIBLE);
            clComment.setClickable(false);
        }
    }

}