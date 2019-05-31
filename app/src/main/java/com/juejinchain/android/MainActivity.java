package com.juejinchain.android;

import android.os.Bundle;
import android.view.Window;

import com.juejinchain.android.ui.fragment.MainFragment;

import me.yokeyword.fragmentation.SupportActivity;

/**
 *
 */
public class MainActivity extends SupportActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        if (findFragment(MainFragment.class) == null) {
            loadRootFragment(R.id.frameLayout, MainFragment.newInstance());
        }
    }

//    @Override
//    public void onFragmentInteraction(Uri uri) {
//
//    }


    @Override
    public void onBackPressedSupport() {
        // 对于 4个类别的主Fragment内的回退back逻辑,已经在其onBackPressedSupport里各自处理了
        super.onBackPressedSupport();
    }

//    @Override
//    public FragmentAnimator onCreateFragmentAnimator() {
//        // 设置横向(和安卓4.x动画相同)
//        return new DefaultHorizontalAnimator();
//    }


}
