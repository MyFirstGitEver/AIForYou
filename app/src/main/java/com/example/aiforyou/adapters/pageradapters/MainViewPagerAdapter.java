package com.example.aiforyou.adapters.pageradapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.aiforyou.custom.UserDTO;
import com.example.aiforyou.fragments.mainfragments.AccountFragment;
import com.example.aiforyou.fragments.mainfragments.ProjectFragment;

public class MainViewPagerAdapter extends FragmentStateAdapter {
    public MainViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position == 0) {
            return new ProjectFragment();
        }

        return new AccountFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}