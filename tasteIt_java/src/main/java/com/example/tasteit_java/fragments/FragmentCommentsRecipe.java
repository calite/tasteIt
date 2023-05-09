package com.example.tasteit_java.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasteit_java.R;
import com.example.tasteit_java.adapters.AdapterRecyclerCommentsProfile;
import com.example.tasteit_java.adapters.AdapterRecyclerCommentsRecipe;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.User;
import com.example.tasteit_java.clases.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;

public class FragmentCommentsRecipe extends Fragment {

    private int recipeId;
    private RecyclerView rvLvComments;
    private AdapterRecyclerCommentsRecipe adapter;
    private ShimmerFrameLayout shimmer;
    private ConstraintLayout clComment;
    private static Button btnAddComment, btnEditComment;

    public FragmentCommentsRecipe() {
        // Required empty public constructor
    }

    public static FragmentCommentsRecipe newInstance(int recipeId) {
        FragmentCommentsRecipe fragment = new FragmentCommentsRecipe();
        Bundle args = new Bundle();
        args.putInt("recipeId", recipeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipeId = getArguments().getInt("recipeId");
            getArguments().clear();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comments, container, false);

        shimmer = view.findViewById(R.id.shimmer);
        shimmer.startShimmer();

        rvLvComments = view.findViewById(R.id.rvLvComments);
        adapter = new AdapterRecyclerCommentsRecipe(getContext(), recipeId, shimmer);
        rvLvComments.setAdapter(adapter);

        rvLvComments.setLayoutManager(new LinearLayoutManager(getContext()));

        //clComment = view.findViewById(R.id.clComment);
        //clComment.setVisibility(View.INVISIBLE);
        //clComment.setClickable(false);

        return view;
    }

    /*public void hideAddComment(Boolean visibility) {
        if(!visibility) {
            clComment.setVisibility(View.VISIBLE);
            clComment.setClickable(true);

            User user = new BdConnection().retrieveUserbyUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
            Bitmap bitmap = Utils.decodeBase64(user.getImgProfile());
            ivMyPhoto.setImageBitmap(bitmap);

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
    }*/

}