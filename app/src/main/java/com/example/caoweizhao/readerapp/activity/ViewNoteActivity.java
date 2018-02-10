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
import com.example.caoweizhao.readerapp.R;
import com.example.caoweizhao.readerapp.RxBus;
import com.example.caoweizhao.readerapp.base.BaseActivity;
import com.example.caoweizhao.readerapp.bean.Note;
import com.example.caoweizhao.readerapp.util.RetrofitUtil;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent;

import java.util.Date;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by caoweizhao on 2018-1-26.
 */

public class ViewNoteActivity extends BaseActivity {

    @BindView(R.id.content_edit_text)
    TextInputEditText mContentEditText;
    @BindView(R.id.title_edit_text)
    TextInputEditText mTitleEditText;
    @BindView(R.id.save)
    TextView mSaveView;

    AlertDialog.Builder mAlertDialogBuilder;

    private NoteService mService;
    private int mPage = 0;
    private int mBookId = 0;

    private Note mNote;
    Disposable mDisposable;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_view_note;
    }

    @Override
    protected void initData() {
        super.initData();
        setToolbar(R.id.toolbar);
        mAlertDialogBuilder = new AlertDialog.Builder(this)
                .setTitle("修改笔记")
                .setCancelable(false);
        mNote = getIntent().getParcelableExtra("note");
        if (mNote != null) {
            mPage = mNote.getPage();
            mBookId = mNote.getBookId();
            mContentEditText.setText(mNote.getContent());
            mTitleEditText.setText(mNote.getTitle());

        }
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
        RxTextView.textChangeEvents(mContentEditText)
                .skipInitialValue()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<TextViewTextChangeEvent>() {
                    @Override
                    public void accept(TextViewTextChangeEvent event) throws Exception {
                        mSaveView.setVisibility(View.VISIBLE);
                    }
                });
        RxTextView.textChangeEvents(mTitleEditText)
                .skipInitialValue()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<TextViewTextChangeEvent>() {
                    @Override
                    public void accept(TextViewTextChangeEvent event) throws Exception {
                        mSaveView.setVisibility(View.VISIBLE);
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

        mNote.setTitle(title);
        mNote.setContent(content);
        mNote.setCreateTime(new Date().getTime());

        mService.addNote(mNote).subscribeOn(Schedulers.newThread())
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
                        mAlertDialogBuilder.setMessage("保存笔记失败，失败原因：\n" + e.getMessage())
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
                        RxBus.get().post(mNote);
                        mAlertDialogBuilder.setMessage("保存成功！")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                }).setNegativeButton("", null)
                                .create()
                                .show();
                    }
                });
    }
}
