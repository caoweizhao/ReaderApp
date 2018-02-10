package com.example.caoweizhao.readerapp.activity;

import android.content.Intent;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.caoweizhao.readerapp.R;
import com.example.caoweizhao.readerapp.adapter.LocalFileAdapter;
import com.example.caoweizhao.readerapp.base.BaseActivity;
import com.example.caoweizhao.readerapp.bean.LocalFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by caoweizhao on 2018-2-5.
 */

public class LocalFileListActivity extends BaseActivity {

    SparseArray<List<String>> localFileType = new SparseArray<>();

    {
        /*<item>PDF</item>
        <item>WORD</item>
        <item>EXCEL</item>
        <item>PPT</item>
        <item>TXT</item>*/
        localFileType.put(0, Collections.singletonList("pdf"));
        localFileType.put(1, Arrays.asList("doc", "docx"));
        localFileType.put(2, Arrays.asList("xlsx", "xls"));
        localFileType.put(3, Collections.singletonList("ppt"));
        localFileType.put(4, Collections.singletonList("txt"));
    }

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.local_file_spinner)
    Spinner mLocalFileTypeSpinner;

    LocalFileAdapter mAdapter;

    List<LocalFile> mLocalFiles = new ArrayList<>();
    List<LocalFile> mFilterFiles = new ArrayList<>();
    List<String> mFilters = localFileType.get(0);

    private boolean isFirstEnter = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_local_file_list;
    }

    @Override
    protected void initData() {
        super.initData();
        setToolbar(R.id.toolbar);
        mAdapter = new LocalFileAdapter(mLocalFiles);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.setEmptyView(R.layout.empty_view);
        getData();
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
        mLocalFileTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("LocalFileLisstActivity", "onItemSelected" + position);
                mFilters = localFileType.get(position);
                filterData(mFilters);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("LocalFileListActivity", "onNothingSelected");
            }
        });

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                LocalFile localFile = mFilterFiles.get(position);
                Intent intent = new Intent(LocalFileListActivity.this, LocalFileViewActivity.class);
                intent.putExtra("file", localFile);
                startActivity(intent);
            }
        });
    }

    /**
     * 过滤文件类型
     *
     * @param filters 文件类型
     */
    public void filterData(final List<String> filters) {
        Log.d("LocalFileListActivity", "filterData" + mLocalFiles.size());
        mSwipeRefreshLayout.setRefreshing(true);
        mFilterFiles.clear();
        Observable.fromIterable(mLocalFiles)
                .filter(new Predicate<LocalFile>() {
                    @Override
                    public boolean test(LocalFile file) throws Exception {
                        for (String filter : filters
                                ) {
                            if (file.getName().endsWith(filter)) {
                                return true;
                            }
                        }

                        return false;
                    }
                }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LocalFile>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LocalFile value) {
                        mFilterFiles.add(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onComplete() {
                        mAdapter.setNewData(mFilterFiles);
                        if(isFirstEnter){
                            isFirstEnter = false;
                            return;
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    /**
     * 获取数据
     */
    public void getData() {
        mSwipeRefreshLayout.setRefreshing(true);

        //查找本地文件
        Observable.create(new ObservableOnSubscribe<List<LocalFile>>() {
            @Override
            public void subscribe(ObservableEmitter<List<LocalFile>> e) throws Exception {
                List<LocalFile> localFiles = new ArrayList<>();
                File root = Environment.getExternalStorageDirectory();
                searchFiles(root, localFiles);
                e.onNext(localFiles);
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<LocalFile>>() {
                    @Override
                    public void accept(List<LocalFile> files) throws Exception {
                        mLocalFiles.clear();
                        mLocalFiles.addAll(files);
                        filterData(mFilters);
                    }
                });

    }

    /**
     * 搜索本地文件
     *
     * @param file       目录
     * @param localFiles 存储结构
     */
    private void searchFiles(File file, List<LocalFile> localFiles) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files
                    ) {
                searchFiles(f, localFiles);
            }
        } else if (!TextUtils.isEmpty(file.getName())) {
            int index = file.getName().lastIndexOf(".");
            if (index != -1) {
                if (isInKey(file.getName().substring(index))) {
                    LocalFile localFile = new LocalFile(file.getName(), file);
                    localFiles.add(localFile);
                }
            }
        }
    }

    /**
     * 判断是否存在目标类型文件
     *
     * @param name 文件后缀
     * @return
     */
    private boolean isInKey(String name) {
        if (TextUtils.isEmpty(name)) {
            return false;
        }
        return LocalFileAdapter.IMAGE_RESOURCE_MAP.containsKey(name);
    }
}
