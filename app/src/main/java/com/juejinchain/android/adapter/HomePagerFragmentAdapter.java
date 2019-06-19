package com.juejinchain.android.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.juejinchain.android.model.ChannelModel;
import com.juejinchain.android.ui.fragment.HomePagerFragment;
import com.juejinchain.android.ui.fragment.VideoFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * 首页page适配器
 */
public class HomePagerFragmentAdapter extends FragmentPagerAdapter {
    private List<ChannelModel> mChannels;
    private Fragment[] mFragments;

    public HomePagerFragmentAdapter(FragmentManager fm, List<ChannelModel> channels) {
        super(fm);
        mChannels = channels;
        mFragments = new Fragment[channels.size()];
    }

    public Fragment getFragmentItem(int position){
        return mFragments[position];
    }

    @Override
    public Fragment getItem(int position) {
        if (mFragments[position] != null) return mFragments[position];

        if (mChannels.get(position).getName().equals("视频")) {
            mFragments[position] = VideoFragment.newInstance("");
        } else {
            mFragments[position] = HomePagerFragment.newInstance(mChannels.get(position));
        }
        return mFragments[position];
    }

    @Override
    public int getCount() {
        return mChannels.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mChannels.get(position).getName();
    }
}
