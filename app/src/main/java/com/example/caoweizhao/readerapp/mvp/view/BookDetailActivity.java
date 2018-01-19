package com.example.caoweizhao.readerapp.mvp.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.caoweizhao.readerapp.API.ReaderService;
import com.example.caoweizhao.readerapp.Constant;
import com.example.caoweizhao.readerapp.DownloadService;
import com.example.caoweizhao.readerapp.R;
import com.example.caoweizhao.readerapp.base.BaseActivity;
import com.example.caoweizhao.readerapp.bean.Book;
import com.example.caoweizhao.readerapp.bean.Collection;
import com.example.caoweizhao.readerapp.bean.User;
import com.example.caoweizhao.readerapp.util.FileHelper;
import com.example.caoweizhao.readerapp.util.RetrofitUtil;

import java.io.File;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by caoweizhao on 2017-9-28.
 */

public class BookDetailActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.book_detail_img)
    ImageView mBookDetailImg;
    @BindView(R.id.book_detail_name)
    TextView mBookDetailName;
    @BindView(R.id.book_detail_author)
    TextView mBookDetailAuthor;
    @BindView(R.id.book_detail_publisher)
    TextView mBookDetailPublisher;
    @BindView(R.id.book_detail_read)
    Button mBookDetailRead;
    @BindView(R.id.book_detail_summary)
    TextView mBookDetailSummary;

    Book mBook;
    ReaderService mService;
    MenuItem mCollectItem;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_book_detail_layout;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToolbar.setTitle("书籍详情");
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mService = RetrofitUtil.getRetrofit()
                .create(ReaderService.class);

        Intent intent = getIntent();
        if (intent != null) {
            mBook = intent.getParcelableExtra("data");
            if (mBook != null) {
                initValues();
            }
        }
    }

    private void queryCollection() {
        // TODO: 2017-12-7 完成用户获取
        Collection collection = new Collection();
        collection.setId(mBook.getId());
        collection.setUserName("caoweizhao");
        mService.isCollected(collection)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean value) {
                        //已收藏
                        if (value != null && value.booleanValue() == true) {
                            mCollectItem.setChecked(true);
                            mCollectItem.setIcon(R.drawable.ic_collected);
                        } else {
                            //未收藏
                            mCollectItem.setChecked(false);
                            mCollectItem.setIcon(R.drawable.ic_not_collected);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void initValues() {
        mBookDetailAuthor.setText(mBook.getAuthor());
        String url = Constant.BASE_URL + "book/images/" + mBook.getImg_url();
        Glide.with(this)
                .load(url)
                .into(mBookDetailImg);
        mBookDetailName.setText(mBook.getName());
        mBookDetailPublisher.setText(mBook.getPublisher());
        mBookDetailSummary.setText(mBook.getSummary());
        mBookDetailRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookDetailActivity.this, ReaderActivity.class);
                intent.putExtra("data", mBook.getUrl());
                intent.putExtra("book", mBook);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.book_detail_menu, menu);
        mCollectItem = menu.findItem(R.id.collection);
        queryCollection();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.download:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 0);
                    return true;
                }
                download();
                break;
            case R.id.collection:
                if (item.isChecked()) {
                    doUnCollected();
                } else {
                    doCollected();
                }
                break;
        }
        return true;
    }

    /**
     * 收藏
     */
    private void doCollected() {
        User user;
        /*if ((user = MyApplication.getUser()) == null) {
            return;
        }*/
        Collection collection = new Collection();
        collection.setId(mBook.getId());
        // collection.setUserName(user.getUserName());
        collection.setUserName("caoweizhao");
        mService.collectBook(collection)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody value) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        mCollectItem.setChecked(true);
                        mCollectItem.setIcon(R.drawable.ic_collected);
                        Toast.makeText(BookDetailActivity.this, "收藏成功！", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 解除收藏
     */
    private void doUnCollected() {
        User user;
        /*if ((user = MyApplication.getUser()) == null) {
            return;
        }*/
        Collection collection = new Collection();
        collection.setId(mBook.getId());
        // collection.setUserName(user.getUserName());
        collection.setUserName("caoweizhao");
        mService.unCollectBook(collection)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody value) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        mCollectItem.setChecked(false);
                        mCollectItem.setIcon(R.drawable.ic_not_collected);
                        Toast.makeText(BookDetailActivity.this, "已取消收藏!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 下载书籍
     */
    private void download() {
        /*mService.getBookSize(mBook.getUrl())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .map(new Function<Response<String>, Long>() {
                    @Override
                    public Long apply(Response<String> response) throws Exception {
                        return Long.parseLong(response.headers().get("size"));
                    }
                }).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Long value) {
                //启动下载
                Log.d("BookDetailActivity", "onNext:" + value);
                mDownloadBookTask = new DownloadBookTask(mBook.getUrl(), value, Constant.TASKID.getAndIncrement());
                mDownloadBookTask.download();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });*/
        Intent intent = new Intent(BookDetailActivity.this, DownloadService.class);
        intent.putExtra("book", mBook);
        startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                download();
            }
        }
    }

    public void share(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("application/pdf");
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(FileHelper.getBooksDir(), mBook.getUrl())));
        startActivity(Intent.createChooser(sendIntent,"Share To..."));
    }

}
