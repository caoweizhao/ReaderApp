package com.example.caoweizhao.readerapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caoweizhao.readerapp.API.NoteService;
import com.example.caoweizhao.readerapp.MyApplication;
import com.example.caoweizhao.readerapp.R;
import com.example.caoweizhao.readerapp.base.BaseActivity;
import com.example.caoweizhao.readerapp.bean.Note;
import com.example.caoweizhao.readerapp.util.RetrofitUtil;

import java.util.Date;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by caoweizhao on 2018-1-25.
 */

public class NewNoteActivity extends BaseActivity {

    @BindView(R.id.content_edit_text)
    TextInputEditText mContentEditText;
    @BindView(R.id.title_edit_text)
    TextInputEditText mTitleEditText;
    @BindView(R.id.save)
    TextView mSaveView;

    private NoteService mService;
    private int mPage = 0;
    private int mBookId = 0;

    Disposable mDisposable;

    AlertDialog.Builder mAlertDialogBuilder;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_note;
    }

    @Override
    protected void initData() {
        super.initData();
        setToolbar(R.id.toolbar);
        mAlertDialogBuilder = new AlertDialog.Builder(this)
                .setTitle("添加笔记")
                .setCancelable(false);
        mPage = getIntent().getIntExtra("page", 0);
        mBookId = getIntent().getIntExtra("bookId", 0);
        mService = RetrofitUtil.getRetrofit().create(NoteService.class);
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        mSaveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });
    }

    public void saveNote() {
        Log.d("NewNoteActivity", "save");
        String title = mTitleEditText.getText().toString();
        Toast.makeText(this, title, Toast.LENGTH_SHORT).show();
        if (TextUtils.isEmpty(title)) {
            title = "无标题";
        }
        String content = mContentEditText.getText().toString();
        if (TextUtils.isEmpty(content)) {
            Toast.makeText(this, "笔记内容不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }

        Note note = new Note();
        note.setTitle(title);
        note.setPage(mPage);
        note.setBookId(mBookId);
        note.setContent(content);
        note.setUserName(MyApplication.getUser().getUser_name());
        note.setCreateTime(new Date().getTime());

        mService.addNote(note).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Note>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(Note value) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mAlertDialogBuilder.setMessage("添加笔记失败，失败原因：\n" + e.getMessage())
                                .setNegativeButton("再试一次", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        saveNote();
                                    }
                                })
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                }).create()
                                .show();
                    }

                    @Override
                    public void onComplete() {
                        mAlertDialogBuilder.setMessage("添加笔记完成")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                }).create()
                                .show();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        super.onDestroy();
    }
}
