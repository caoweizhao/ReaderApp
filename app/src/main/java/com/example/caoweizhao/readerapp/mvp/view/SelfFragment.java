package com.example.caoweizhao.readerapp.mvp.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.example.caoweizhao.readerapp.R;
import com.example.caoweizhao.readerapp.activity.CollectionActivity;
import com.example.caoweizhao.readerapp.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by caoweizhao on 2017-9-21.
 */

public class SelfFragment extends BaseFragment{

    @BindView(R.id.my_collection)
    TextView mCollection;
    @BindView(R.id.recent_reading)
    TextView mRecentReading;
    @BindView(R.id.theme_setting)
    TextView mThemeSetting;
    @BindView(R.id.download_management)
    TextView mDownloadManagement;

    @Override
    protected int getLayoutId() {
        return R.layout.self_fragment_layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @OnClick({R.id.my_collection,R.id.recent_reading})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.my_collection:
                Intent intent = new Intent(getContext(), CollectionActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
