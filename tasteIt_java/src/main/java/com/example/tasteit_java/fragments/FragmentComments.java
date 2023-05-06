package com.example.tasteit_java.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tasteit_java.ActivityProfile;
import com.example.tasteit_java.ActivityRecipe;
import com.example.tasteit_java.R;
import com.example.tasteit_java.adapters.AdapterFragmentComments;
import com.example.tasteit_java.adapters.AdapterRecyclerCommentsProfile;
import com.example.tasteit_java.adapters.AdapterRecyclerPhotosProfile;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.Comment;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.User;
import com.example.tasteit_java.clases.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

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
    private String uidProfile;
    private Boolean myProfile;
    private static int editCommentId;

    private RecyclerView rvLvComments;
    private AdapterRecyclerCommentsProfile adapter;
    private ShimmerFrameLayout shimmer;
    private ConstraintLayout clComment;
    private ShapeableImageView ivMyPhoto;
    private static EditText etComment;
    private static Button btnAddComment, btnEditComment;

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

    public static FragmentComments newInstance(String uid, Boolean myProfile) {
        FragmentComments fragment = new FragmentComments();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM2, myProfile);
        args.putString(ARG_PARAM3, uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.myProfile = getArguments().getBoolean(ARG_PARAM2);
            this.uidProfile = getArguments().getString(ARG_PARAM3);
            getArguments().clear();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);

        shimmer = view.findViewById(R.id.shimmer);
        shimmer.startShimmer();

        adapter = new AdapterRecyclerCommentsProfile(getContext(), uidProfile, myProfile, shimmer);
        rvLvComments = view.findViewById(R.id.rvLvComments);
        rvLvComments.setAdapter(adapter);

        rvLvComments.setLayoutManager(new LinearLayoutManager(getContext()));

        if(!myProfile) {
            ivMyPhoto = view.findViewById(R.id.ivMyPhoto);
            etComment = view.findViewById(R.id.etComment);
            btnAddComment = view.findViewById(R.id.btnAddComment);
            btnEditComment = view.findViewById(R.id.btnEditComment);
        }
        clComment = view.findViewById(R.id.clComment);
        hideAddComment(myProfile);

        return view;
    }

    public static void editComment(int id, String comment) {
        btnAddComment.setVisibility(View.INVISIBLE);
        btnAddComment.setEnabled(false);

        btnEditComment.setVisibility(View.VISIBLE);
        btnEditComment.setEnabled(true);

        etComment.setText(comment);
        editCommentId = id;
    }

    public void hideAddComment(Boolean visibility) {
        if(!visibility) {
            clComment.setVisibility(View.VISIBLE);
            clComment.setClickable(true);

            User user = new BdConnection().retrieveAllUserbyUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
            try{
                Picasso.with(getContext()).load(user.getImgProfile()).into(ivMyPhoto);
            }catch(IllegalArgumentException iae){}
            //Bitmap bitmap = Utils.decodeBase64(user.getImgProfile());
            //ivMyPhoto.setImageBitmap(bitmap);

            btnAddComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new BdConnection().commentUser(FirebaseAuth.getInstance().getCurrentUser().getUid(), uidProfile, etComment.getText().toString());
                    adapter.updateComments();
                    etComment.setText("");
                }
            });

            btnEditComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new BdConnection().editComment(editCommentId, etComment.getText().toString());
                    adapter.updateComments();
                    etComment.setText("");

                    btnEditComment.setVisibility(View.INVISIBLE);
                    btnEditComment.setEnabled(false);

                    btnAddComment.setVisibility(View.VISIBLE);
                    btnAddComment.setEnabled(true);
                }
            });

        } else {
            clComment.setVisibility(View.INVISIBLE);
            clComment.setClickable(false);
        }
    }

}