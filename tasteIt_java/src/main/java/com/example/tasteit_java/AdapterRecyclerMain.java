package com.example.tasteit_java;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tasteit_java.clases.Recipe;

import java.util.ArrayList;

public class AdapterRecyclerMain extends RecyclerView.Adapter<AdapterRecyclerMain.ViewHolder> {

    private ArrayList<Recipe> listRecipe;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivPhotoRecipe;
        private final TextView tvRating;
        private final TextView tvNameRecipe;
        private final TextView tvNameCreator;
        private final TextView tvDescriptionRecipe;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            ivPhotoRecipe = view.findViewById(R.id.ivPhotoRecipe);
            tvRating = view.findViewById(R.id.tvRating);
            tvNameRecipe = view.findViewById(R.id.tvNameRecipe);
            tvNameCreator = view.findViewById(R.id.tvNameCreator);
            tvDescriptionRecipe = view.findViewById(R.id.tvDescriptionRecipe);
        }

        public TextView getTvNameRecipe() {
            return tvNameRecipe;
        }

        public TextView getTvDescriptionRecipe() {
            return tvDescriptionRecipe;
        }

        public TextView getTvNameCreator() {
            return tvNameCreator;
        }

        public TextView getTvRating() {
            return tvRating;
        }

        public ImageView getIvPhotoRecipe() {
            return ivPhotoRecipe;
        }

    }
    /*
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public AdapterRecyclerMain(ArrayList<Recipe> listRecipes) {
        listRecipe = new ArrayList<>();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        /*
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_main_recycler, viewGroup, false);
        */
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View view = layoutInflater.inflate(R.layout.item_main_recycler,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        /*
        viewHolder.getTvNameRecipe().setText(listRecipe.get(position).getName());
        viewHolder.getTvDescriptionRecipe().setText(listRecipe.get(position).getDescription());
        viewHolder.getTvNameCreator().setText(listRecipe.get(position).getCreator());
        viewHolder.getTvRating().setText(listRecipe.get(position).getRating());
        viewHolder.getIvPhotoRecipe().setImageResource(listRecipe.get(position).getImg());
        */
        viewHolder.tvNameRecipe.setText(listRecipe.get(position).getName());
        viewHolder.tvNameCreator.setText(listRecipe.get(position).getCreator());
        viewHolder.tvDescriptionRecipe.setText(listRecipe.get(position).getDescription());
        viewHolder.tvRating.setText(listRecipe.get(position).getRating());
        viewHolder.ivPhotoRecipe.setImageResource(listRecipe.get(position).getImg());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return listRecipe.size();
    }


}
