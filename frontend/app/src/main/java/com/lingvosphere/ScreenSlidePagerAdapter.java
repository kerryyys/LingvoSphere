package com.lingvosphere;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.lingvosphere.Fragments.HomeFragment;
import com.lingvosphere.Fragments.LearningJourneyFragment;
import com.lingvosphere.Fragments.MentorshipHubFragment;
import com.lingvosphere.Fragments.ProfileFragment;

public class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
    public ScreenSlidePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new LearningJourneyFragment();
            case 2:
                return new MentorshipHubFragment();
            case 3:
                return new ProfileFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4; // 总共有三个界面
    }
}
