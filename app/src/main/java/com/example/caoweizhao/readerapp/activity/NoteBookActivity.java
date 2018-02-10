package com.example.caoweizhao.readerapp.activity;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.caoweizhao.readerapp.API.NoteService;
import com.example.caoweizhao.readerapp.R;
import com.example.caoweizhao.readerapp.RxBus;
import com.example.caoweizhao.readerapp.adapter.NoteBookAdapter;
import com.example.caoweizhao.readerapp.base.BaseActivity;
import com.example.caoweizhao.readerapp.bean.Note;
import com.example.caoweizhao.readerapp.util.RetrofitUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by caoweizhao on 2018-1-25.
 */

public class NoteBookActivity extends BaseActivity {

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.note_book_sort_type_spinner)
    Spinner mSortTypeSpinner;


    List<Note> mNoteList = new ArrayList<>();
    NoteBookAdapter mAdapter;

    NoteService mService;
    List<Disposable> mDisposables = new ArrayList<>();

    private int mBookId;
    private String mUserName;

    private int mSelectedPosition;


    public static final String PAGE_SORT = "page";
    public static final String CREATE_TIME_SORT = "createTime";
    private String mSort = PAGE_SORT;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_note_book;
    }

    @Override
    protected void initData() {
        super.initData();
        setToolbar(R.id.toolbar);
        mService = RetrofitUtil.getRetrofit().create(NoteService.class);
        mAdapter = new NoteBookAdapter(mNoteList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.setEmptyView(R.layout.empty_view);

        Intent intent = getIntent();
        mBookId = intent.getIntExtra("bookId", 0);
        mUserName = intent.getStringExtra("userName");
    }

    @Override
    protected void initEvent() {
        super.initEvent();

        RxBus.get().toFlowable().subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Log.d("NoteBookActivity", "accept");
                if (o instanceof Note) {
                    int index = mNoteList.indexOf(o);
                    Log.d("NoteBookActivity", "accept" + index);
                    mNoteList.set(index, (Note) o);
                    mAdapter.notifyItemChanged(index);
                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNoteList(mSort);
            }
        });
        mAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                mSelectedPosition = position;
                return false;
            }
        });
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(NoteBookActivity.this, ViewNoteActivity.class);
                intent.putExtra("note", mNoteList.get(position));
                startActivity(intent);
            }
        });
        mSortTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mSort = PAGE_SORT;
                        break;
                    case 1:
                        mSort = CREATE_TIME_SORT;
                        break;
                    default:
                        break;
                }
                getNoteList(mSort);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Note note = mNoteList.get(mSelectedPosition);
        deleteNote(note);
        return super.onContextItemSelected(item);
    }

    /**
     * 删除笔记
     *
     * @param note
     */
    private void deleteNote(Note note) {
        mService.deleteNote(note)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposables.add(d);
                    }

                    @Override
                    public void onNext(ResponseBody value) {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(NoteBookActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        mNoteList.remove(mSelectedPosition);
                        mAdapter.notifyItemRemoved(mSelectedPosition);
                    }
                });
    }

    /**
     * 获取笔记列表
     *
     * @param sort 要选择的排序方式（page，createTime）
     */
    private void getNoteList(String sort) {
        mSwipeRefreshLayout.setRefreshing(true);
        mService.queryAll(mBookId, mUserName, sort)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Note>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposables.add(d);
                    }

                    @Override
                    public void onNext(List<Note> value) {
                        mNoteList = value;
                        mAdapter.setNewData(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(NoteBookActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onComplete() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        for (Disposable d : mDisposables
                ) {
            if (d != null) {
                d.dispose();
            }
        }
        super.onDestroy();
    }
}
