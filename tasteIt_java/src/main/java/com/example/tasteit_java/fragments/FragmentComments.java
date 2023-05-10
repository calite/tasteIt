package com.example.tasteit_java.fragments;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tasteit_java.ApiService.ApiClient;
import com.example.tasteit_java.ApiUtils.UserLoader;
import com.example.tasteit_java.R;
import com.example.tasteit_java.adapters.AdapterRecyclerCommentsProfile;
import com.example.tasteit_java.clases.Comment;
import com.example.tasteit_java.clases.OnLoadMoreListener;
import com.example.tasteit_java.clases.Utils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

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
    private int skipper;
    private String accessToken;
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

            accessToken = Utils.getUserAcessToken();
            skipper = 0;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);

        shimmer = view.findViewById(R.id.shimmer);
        shimmer.startShimmer();

        bringComments();

        rvLvComments = view.findViewById(R.id.rvLvComments);
        adapter = new AdapterRecyclerCommentsProfile(myProfile, rvLvComments);
        rvLvComments.setAdapter(adapter);

        rvLvComments.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if(adapter.getItemCount() > 28) { //habra que ponerle un limite (que en principio puede ser el total de recipes en la bbdd o algo fijo para no sobrecargar el terminal)
                    Toast.makeText(getContext(), "Finiquitao con " + adapter.getItemCount() + " fotos", Toast.LENGTH_SHORT).show();
                } else {
                    adapter.dataList.add(null);
                    adapter.notifyItemInserted(adapter.getItemCount() - 1);

                    skipper += 10;
                    bringComments();
                }
            }

            @Override
            public void update() {
                skipper = 0;
                adapter.dataList.clear();
                bringComments();
            }
        });

        /*if(!myProfile) {
            ivMyPhoto = view.findViewById(R.id.ivMyPhoto);
            etComment = view.findViewById(R.id.etComment);
            btnAddComment = view.findViewById(R.id.btnAddComment);
            btnEditComment = view.findViewById(R.id.btnEditComment);
        }
        clComment = view.findViewById(R.id.clComment);*/

        return view;
    }

    //Carga de usuario asyncrona
    private void bringComments() {
        UserLoader commentsLoader = new UserLoader(ApiClient.getInstance(accessToken).getService(), getContext(), uidProfile, skipper);
        commentsLoader.getUserComments().observe(getViewLifecycleOwner(), this::onCommentsLoaded);
        commentsLoader.loadUserComments();
    }

    private void onCommentsLoaded(List<Comment> comments) {
        if(adapter.getItemViewType(adapter.getItemCount() - 1) != 0) {
            adapter.dataList.remove(adapter.getItemCount() - 1);
        } else if(adapter.getItemViewType(0) != 0) {
            adapter.dataList.remove(0);
        }

        adapter.dataList.addAll(comments);
        adapter.setLoaded();
        shimmer.stopShimmer();
        shimmer.setVisibility(View.GONE);
        adapter.notifyDataSetChanged();
    }

}