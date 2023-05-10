package com.example.tasteit_java.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasteit_java.ActivityMain;
import com.example.tasteit_java.ActivityProfile;
import com.example.tasteit_java.R;
import com.example.tasteit_java.clases.Comment;
import com.example.tasteit_java.clases.OnLoadMoreListener;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterRecyclerCommentsRecipe extends RecyclerView.Adapter {

    public ArrayList<Object> dataList;
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private int visibleThreshold = 4;
    private int lastVisibleItem, totalItemCount, firstVisibleItem;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    private Context context;
    private int commentId;
    private ShimmerFrameLayout shimmer;

    public AdapterRecyclerCommentsRecipe(RecyclerView recyclerView, ShimmerFrameLayout shimmer) {
        dataList = new ArrayList<>();
        this.shimmer = shimmer;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                private int totalDistanceScrolled = 0;
                private int threshold = 10; // umbral de distancia a recorrer en px
                private boolean isScrollingUp = false;
                @Override
                public void onScrolled(RecyclerView recyclerView,
                                       int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    //Toast.makeText(recyclerView.getContext(), "Distancia: " + totalDistanceScrolled, Toast.LENGTH_SHORT).show();

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
        return dataList.get(position) instanceof Comment ? TYPE_ITEM : TYPE_FOOTER;
    }

    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == TYPE_ITEM) {
            View row = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recipe_comment,
                    viewGroup, false);
            return new CommentViewHolder(row);
        } else {
            View row = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.loading_footer,
                    viewGroup, false);
            return new FooterViewHolder(row);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof AdapterEndlessRecyclerMain.RecipeViewHolder) {
            ((CommentViewHolder) viewHolder).bindRow((Comment) dataList.get(position));
        } else if (viewHolder instanceof AdapterEndlessRecyclerMain.FooterViewHolder) {
            ((FooterViewHolder) viewHolder).progressBar.setIndeterminate(true);
        }
    }

    public void setLoaded() {
        loading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public long getItemId(int i) {
        return dataList.get(i).hashCode();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivUserPicture;
        private TextView tvAuthor;
        private TextView tvComment, tvDateCreated, tvRating;
        private ImageButton ibtnOptions;

        public CommentViewHolder(View view) {
            super(view);
            // Define click listener for the DataViewHolder's View
            ivUserPicture = view.findViewById(R.id.ivUserPicture);
            tvAuthor = view.findViewById(R.id.tvAuthor);
            tvComment = view.findViewById(R.id.tvComment);
            tvDateCreated = view.findViewById(R.id.tvDateCreated);
            tvRating = view.findViewById(R.id.tvRating);
        }

        void bindRow(@NonNull Comment comment) {
            tvAuthor.setText(comment.getUser().getUsername());
            tvComment.setText(comment.getComment());
            tvDateCreated.setText(comment.getDateCreated());
            tvRating.setText(String.valueOf(comment.getRating()));

            try{
                Picasso.with(itemView.getContext()).load(comment.getUser().getImgProfile()).into(ivUserPicture);
            }catch(IllegalArgumentException iae){}

            commentId = comment.getId();

            View.OnClickListener listenerProfile = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ActivityProfile.class);
                    intent.putExtra("uid", comment.getTokenUser());
                    context.startActivity(intent);
                }
            };

            ivUserPicture.setOnClickListener(listenerProfile);
            tvComment.setOnClickListener(listenerProfile);
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

    /*PopupMenu.OnMenuItemClickListener popupListener = new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.iEditComment: {
                        FragmentComments.editComment(comments.get(pos).getId(), comments.get(pos).getComment());
                        break;
                    }
                    case R.id.iReportComment: {
                        Toast.makeText(context, "Esta opcion pronto estara disponible!", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case R.id.iRemoveComment: {
                        new BdConnection().removeComment(comments.get(pos).getId());
                        break;
                    }
                }
                updateComments();
                return false;
            }
        };

        if(comments.get(position).getTokenUser().equals(token) & !myProfile) {
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
        }*/

}
