package com.example.tasteit_java.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tasteit_java.R;
import com.example.tasteit_java.bdConnection.BdConnection;
import com.example.tasteit_java.clases.Recipe;
import com.example.tasteit_java.clases.Utils;

import java.util.ArrayList;

public class AdapterRecyclerPhotosProfile extends RecyclerView.Adapter<AdapterRecyclerPhotosProfile.ViewHolder> {

    private Context context;
    private String uid;
    private ArrayList<Recipe> arrayListPhotos;

    public AdapterRecyclerPhotosProfile(Context context, String uid) {
        this.context = context;
        this.uid = uid;

        arrayListPhotos = new ArrayList<>();
        new TaskLoadUserPhotos().execute();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_profile_gridview, null, false);
        return new ViewHolder(view);
        //return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bitmap bitmap = Utils.decodeBase64(arrayListPhotos.get(position).getImage());
        holder.ivPhoto.setImageBitmap(bitmap);
    }

    @Override
    public long getItemId(int i) {
        return arrayListPhotos.get(i).hashCode();
    }

    @Override
    public int getItemCount() {
        return arrayListPhotos.size();
    }

    class TaskLoadUserPhotos extends AsyncTask<ArrayList<Recipe>, Void,ArrayList<Recipe>> {
        @Override
        protected void onPreExecute() {

        }
        @Override
        protected ArrayList<Recipe> doInBackground(ArrayList<Recipe>... hashMaps) {
            return new BdConnection().retrieveAllRecipesbyUid(uid);
        }
        @Override
        protected void onPostExecute(ArrayList<Recipe> recipes) {
            //super.onPostExecute(recipes);
            arrayListPhotos = recipes;
            notifyDataSetChanged();
        }
    }
}
