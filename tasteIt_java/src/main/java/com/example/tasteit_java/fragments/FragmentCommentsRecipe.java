package com.example.tasteit_java.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasteit_java.ApiService.ApiClient;
import com.example.tasteit_java.ApiGetters.RecipeLoader;
import com.example.tasteit_java.R;
import com.example.tasteit_java.adapters.AdapterRecyclerCommentsRecipe;
import com.example.tasteit_java.clases.Comment;
import com.example.tasteit_java.clases.OnLoadMoreListener;
import com.example.tasteit_java.clases.SharedPreferencesSaved;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

public class FragmentCommentsRecipe extends Fragment {

    private int recipeId;
    private RecyclerView rvLvComments;
    private AdapterRecyclerCommentsRecipe adapter;
    private int skipper;
    private int allItemsCount;
    private boolean allItemsLoaded;
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
        View view = inflater.inflate(R.layout.fragment_comments, container, false);

        shimmer = view.findViewById(R.id.shimmer);
        shimmer.startShimmer();

        skipper = 0;
        allItemsCount = 0;
        allItemsLoaded = false;
        rvLvComments = view.findViewById(R.id.rvLvComments);

        bringComments();

        rvLvComments.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdapterRecyclerCommentsRecipe(rvLvComments, shimmer);
        rvLvComments.setAdapter(adapter);

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if(!allItemsLoaded) { //habra que ponerle un limite (que en principio puede ser el total de recipes en la bbdd o algo fijo para no sobrecargar el terminal)
                    adapter.dataList.add(null);
                    adapter.notifyItemInserted(adapter.getItemCount() - 1);

                    skipper += 10;
                    bringComments();
                }
            }
            @Override
            public void update() {
                updateList();
            }
        });

        //clComment = view.findViewById(R.id.clComment);
        //clComment.setVisibility(View.INVISIBLE);
        //clComment.setClickable(false);

        return view;
    }

    public void updateList() {
        skipper = 0;
        allItemsCount = 0;
        allItemsLoaded = false;
        adapter.dataList.clear();
        adapter.notifyDataSetChanged();

        shimmer.setVisibility(View.VISIBLE);
        shimmer.startShimmer();

        bringComments();
    }

    private void bringComments() {
        String accessToken = new SharedPreferencesSaved(getContext()).getSharedPreferences().getString("accessToken", "null");
        RecipeLoader recipesLoader = new RecipeLoader(ApiClient.getInstance(accessToken).getService(), getContext(), recipeId, skipper);
        recipesLoader.getRecipeComments().observe(getViewLifecycleOwner(), this::onCommentsLoaded);
        recipesLoader.loadRecipeComments();
    }

    private void onCommentsLoaded(List<Comment> comments) {
        if(adapter.getItemCount() > 0) {
            if(adapter.getItemViewType(adapter.getItemCount() - 1) != 0) {
                adapter.dataList.remove(adapter.getItemCount() - 1);
            } else if(adapter.getItemViewType(0) != 0) {
                adapter.dataList.remove(0);
            }
        }

        adapter.dataList.addAll(comments);
        adapter.setLoaded();
        adapter.notifyDataSetChanged();

        if(adapter.dataList.size() != allItemsCount) {
            allItemsCount = adapter.dataList.size();
        } else {
            allItemsLoaded = true;
        }

        shimmer.stopShimmer();
        shimmer.setVisibility(View.GONE);
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