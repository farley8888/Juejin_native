package com.juejinchain.android.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.juejinchain.android.model.ChannelModel;
import com.juejinchain.android.model.VideoCategoryModel;
import com.juejinchain.android.ui.fragment.HomePagerFragment;
import com.juejinchain.android.ui.fragment.VideoFragment;
import com.juejinchain.android.ui.fragment.VideoPagerFragment;

import java.util.List;


/**
 * 视频page适配器
 */
public class VideoPagerFragmentAdapter extends FragmentPagerAdapter {
    private List<VideoCategoryModel> mChannels;

    public VideoPagerFragmentAdapter(FragmentManager fm, List<VideoCategoryModel> channels) {
        super(fm);
        mChannels = channels;
    }

    @Override
    public Fragment getItem(int position) {

        return VideoPagerFragment.newInstance(mChannels.get(position));

    }

    @Override
    public int getCount() {
        return mChannels.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mChannels.get(position).ch;
    }
}
