package com.juejinchain.android.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.juejinchain.android.R;
import com.juejinchain.android.WebAppFragment;

import me.yokeyword.fragmentation.SupportActivity;

//不能用activity vue还是会重新加载
public class ArticleDetailActivity extends SupportActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_article_detail);

        initView();
        findViewById(R.id.back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    void initView(){

        FragmentManager fragManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragManager.beginTransaction();
        transaction.add(R.id.frameLayout, WebAppFragment.instance("login"));
        transaction.commit();
    }

}
