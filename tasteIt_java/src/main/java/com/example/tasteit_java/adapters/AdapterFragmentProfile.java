package com.example.tasteit_java.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.tasteit_java.fragments.FragmentBio;
import com.example.tasteit_java.fragments.FragmentComments;
import com.example.tasteit_java.fragments.FragmentPhotos;

public class AdapterFragmentProfile extends FragmentStateAdapter {

    private Boolean myProfile;
    private String uidProfile;

    public AdapterFragmentProfile(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, String uid, Boolean myProfile) {
        super(fragmentManager, lifecycle);
        this.uidProfile = uid;
        this.myProfile = myProfile;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position) {
            case 0:
                return FragmentBio.newInstance(uidProfile);
            case 1:
                return FragmentPhotos.newInstance(uidProfile);
            case 2:
                return FragmentComments.newInstance(uidProfile, myProfile);
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

}
