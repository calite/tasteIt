package com.example.tasteit_java;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AdapterFragmentProfile extends FragmentStateAdapter {

    private String biography;

    public AdapterFragmentProfile(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, String biography) {
        super(fragmentManager, lifecycle);
        this.biography = biography;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 0:
                return FragmentBio.newInstance(biography);
            case 1:
                return new FragmentPhotos();
            case 2:
                return new FragmentVideos();
            default:
                return new FragmentBio();
        }

    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
