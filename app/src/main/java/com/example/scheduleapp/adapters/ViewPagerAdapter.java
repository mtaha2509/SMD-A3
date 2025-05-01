package com.example.scheduleapp.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.scheduleapp.fragments.ScheduleFragment;
import com.example.scheduleapp.fragments.PastFragment;
import com.example.scheduleapp.fragments.NotificationFragment;
import com.example.scheduleapp.fragments.ProfileFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private static final int TAB_COUNT = 4;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new ScheduleFragment();
            case 1:
                return new PastFragment();
            case 2:
                return new NotificationFragment();
            case 3:
                return new ProfileFragment();
            default:
                throw new IllegalStateException("Unexpected position " + position);
        }
    }

    @Override
    public int getItemCount() {
        return TAB_COUNT;
    }
} 