package com.example.tasteit_java.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasteit_java.ActivityEditProfile;
import com.example.tasteit_java.ActivityProfile;
import com.example.tasteit_java.ActivityRecipe;
import com.example.tasteit_java.ApiService.ApiClient;
import com.example.tasteit_java.ApiService.ApiRequests;
import com.example.tasteit_java.ApiService.UserApi;
import com.example.tasteit_java.R;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.Comment;
import com.example.tasteit_java.clases.OnLoadMoreListener;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.User;
import com.example.tasteit_java.clases.Utils;
import com.example.tasteit_java.fragments.FragmentComments;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterRecyclerCommentsProfile extends RecyclerView.Adapter {
    public ArrayList<Object> dataList;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private int visibleThreshold = 3;
    private int lastVisibleItem, totalItemCount, firstVisibleItem;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private Boolean myProfile;
    private ShimmerFrameLayout shimmer;

    public AdapterRecyclerCommentsProfile(Boolean myProfile, RecyclerView recyclerView) {
        this.myProfile = myProfile;
        dataList = new ArrayList<>();

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                private int totalDistanceScrolled = 0;
                private int threshold = 100; // umbral de distancia a recorrer en px
                private boolean isScrollingUp = false;
                @Override
                public void onScrolled(RecyclerView recyclerView,
                                       int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    if(recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_SETTLING) {
                        if (dy < 0) {
                            // User is scrolling up
                            isScrollingUp = true;
                        } else if (dy > 0) {
                            // User is scrolling down
                            isScrollingUp = false;
                        }

                        totalDistanceScrolled += dy;

                        if (linearLayoutManager.findFirstVisibleItemPosition() > 0 && dy > 0) {
                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager.findLastCompletelyVisibleItemPosition();

                            if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                // End has been reached
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                                totalDistanceScrolled = 0;
                            }
                        } else if (isScrollingUp && linearLayoutManager.findFirstVisibleItemPosition() == 0) {
                            // Beginning has been reached
                            if (!loading && totalDistanceScrolled > threshold) {
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.update();
                                }
                                loading = true;
                                totalDistanceScrolled = 0;
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (!dataList.isEmpty()) {
            return dataList.get(position) instanceof Comment ? TYPE_ITEM : TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_ITEM) {
            View row = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_profile_comments,
                    viewGroup, false);
            return new CommentViewHolder(row);
        } else {
            View row = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.loading_footer,
                    viewGroup, false);
            return new FooterViewHolder(row);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof CommentViewHolder) {
            ((CommentViewHolder) viewHolder).bindRow((Comment) dataList.get(position));
        } else if (viewHolder instanceof FooterViewHolder) {
            ((FooterViewHolder) viewHolder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public long getItemId(int i) {
        return dataList.get(i).hashCode();
    }

    public void setLoaded() {
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAuthor;
        TextView tvAuthor;
        TextView tvComment, tvDateCreated;
        ImageButton ibtnOptions;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAuthor = itemView.findViewById(R.id.ivAuthor);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvComment = itemView.findViewById(R.id.tvComment);
            //ibtnOptions = itemView.findViewById(R.id.ibtnOptions);
            tvDateCreated = itemView.findViewById(R.id.tvDateCreated);
        }

        void bindRow(@NonNull Comment comment) {
            tvAuthor.setText(comment.getUser().getUsername());
            tvComment.setText(comment.getComment());

            Picasso.with(itemView.getContext())
                    .load(comment.getUser().getImgProfile())
                    .into(ivAuthor);

            View.OnClickListener listenerProfile = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), ActivityProfile.class);
                    intent.putExtra("uid", comment.getTokenUser());
                    view.getContext().startActivity(intent);
                }
            };

            ivAuthor.setOnClickListener(listenerProfile);
            //tvComment.setOnClickListener(listenerProfile);
            tvAuthor.setOnClickListener(listenerProfile);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.footer);
        }
    }

    /*@Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        int pos = position;
        //User user = new BdConnection().retrieveAllUserbyUid(comments.get(position).getTokenUser());
        String token = Utils.getUserToken();

        /*PopupMenu.OnMenuItemClickListener popupListener = new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.iEditComment: {
                        //FragmentComments.editComment(comments.get(pos).getId(), comments.get(pos).getComment());
                        Toast.makeText(context, "Esta opcion pronto estara disponible!", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case R.id.iReportComment: {
                        Toast.makeText(context, "Esta opcion pronto estara disponible!", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case R.id.iRemoveComment: {
                        //new BdConnection().removeComment(comments.get(pos).getId());
                        Toast.makeText(context, "Esta opcion pronto estara disponible!", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
                updateComments();
                return false;
            }
        };*/

        /*if(comments.get(position).getTokenUser().equals(token) & !myProfile) {
            holder.ibtnOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(context, view);
                    MenuInflater menuInflater = popupMenu.getMenuInflater();
                    menuInflater.inflate(R.menu.profile_comments_menu, popupMenu.getMenu());
                    popupMenu.getMenu().getItem(2).setVisible(false);
                    popupMenu.setOnMenuItemClickListener(popupListener);
                    popupMenu.show();
                }
            });
        } else {
            holder.ibtnOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(context, view);
                    MenuInflater menuInflater = popupMenu.getMenuInflater();
                    menuInflater.inflate(R.menu.profile_comments_menu, popupMenu.getMenu());
                    popupMenu.getMenu().getItem(0).setVisible(false);
                    popupMenu.setOnMenuItemClickListener(popupListener);
                    popupMenu.show();
                }
            });
        }

        holder.tvAuthor.setText(user.getUsername());
        holder.tvComment.setText(comments.get(position).getComment());

        try{
            Picasso.with(context).load(user.getImgProfile()).into(holder.ivAuthor);
        }catch(IllegalArgumentException iae){}
        //Bitmap bitmap = Utils.decodeBase64(user.getImgProfile());
        //holder.ivAuthor.setImageBitmap(bitmap);

        View.OnClickListener listenerProfile = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ActivityProfile.class);
                intent.putExtra("uid", comments.get(pos).getTokenUser());
                context.startActivity(intent);
            }
        };

        holder.ivAuthor.setOnClickListener(listenerProfile);
        holder.tvComment.setOnClickListener(listenerProfile);
        holder.tvAuthor.setOnClickListener(listenerProfile);
    }

    @Override
    public long getItemId(int i) {
        return comments.get(i).hashCode();
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void updateComments() {
        new TaskLoadUserComments().execute();
        notifyDataSetChanged();
    }

    class TaskLoadUserComments extends AsyncTask<ArrayList<Comment>, Void,ArrayList<Comment>> {
        @Override
        protected void onPreExecute() {

        }
        @Override
        protected ArrayList<Comment> doInBackground(ArrayList<Comment>... hashMaps) {
            return new BdConnection().retrieveCommentsbyUid(uidProfile);
        }
        @Override
        protected void onPostExecute(ArrayList<Comment> userComments) {
            //super.onPostExecute(recipes);
            comments = new ArrayList<>(userComments);
            notifyDataSetChanged();
            shimmer.stopShimmer();
            shimmer.setVisibility(View.GONE);
        }
    }
        }*/

}
