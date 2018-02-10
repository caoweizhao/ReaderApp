package com.example.caoweizhao.readerapp.mvp.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.caoweizhao.readerapp.MyApplication;
import com.example.caoweizhao.readerapp.R;
import com.example.caoweizhao.readerapp.activity.AboutActivity;
import com.example.caoweizhao.readerapp.activity.DownloadActivity;
import com.example.caoweizhao.readerapp.activity.LocalFileListActivity;
import com.example.caoweizhao.readerapp.activity.RecentReadActivity;
import com.example.caoweizhao.readerapp.base.BaseFragment;
import com.example.caoweizhao.readerapp.util.SharePreferenceMgr;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by caoweizhao on 2017-9-21.
 */

public class SelfFragment extends BaseFragment {

    @BindView(R.id.recent_reading)
    TextView mRecentReading;
    @BindView(R.id.theme_setting)
    TextView mThemeSetting;
    @BindView(R.id.download_management)
    TextView mDownloadManagement;
    @BindView(R.id.clear_cache)
    LinearLayout mClearCache;
    @BindView(R.id.cache_size_text_view)
    TextView mCacheSize;

    AlertDialog.Builder mDialogBuilder;

    @Override
    protected int getLayoutId() {
        return R.layout.self_fragment_layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDialogBuilder = new AlertDialog.Builder(getContext()).setTitle("请选择主题");
        mDialogBuilder.setCancelable(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        obtainCacheSize();
    }

    private void obtainCacheSize() {
        File file = Glide.getPhotoCacheDir(this.getContext());
        Log.d("SelfFragment", file.getAbsolutePath());
        long size = 0;
        if (file.isDirectory()) {
            try {
                size = getFileSizes(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                size = getFileSize(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mCacheSize.setText(formatFileSize(size));
    }

    @OnClick({R.id.recent_reading, R.id.download_management, R.id.clear_cache,
            R.id.theme_setting, R.id.about_app, R.id.local_file, R.id.functions_introduction,
            R.id.feedback, R.id.switch_account})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            //下载管理
            case R.id.download_management:
                intent = new Intent(getContext(), DownloadActivity.class);
                startActivity(intent);
                break;
            //最近阅读
            case R.id.recent_reading:
                intent = new Intent(getContext(), RecentReadActivity.class);
                startActivity(intent);
                break;
            //清空缓存
            case R.id.clear_cache:
                executeClearCache();
                break;
            //主题设置
            case R.id.theme_setting:
                final int mode = SharePreferenceMgr.getTheme(getContext());
                int selectedItem = 0;
                switch (mode) {
                    case SharePreferenceMgr.THEME_AUTO:
                        selectedItem = 2;
                        break;
                    case SharePreferenceMgr.THEME_DAY_TIME:
                        selectedItem = 0;
                        break;
                    case SharePreferenceMgr.THEME_NIGHT:
                        selectedItem = 1;
                        break;
                }
                mDialogBuilder.setSingleChoiceItems(R.array.theme_items, selectedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                ((MyApplication) getContext().getApplicationContext()).setThemeMode
                                        (SharePreferenceMgr.THEME_DAY_TIME);
                                break;
                            case 1:
                                ((MyApplication) getContext().getApplicationContext()).setThemeMode
                                        (SharePreferenceMgr.THEME_NIGHT);
                                break;
                            case 2:
                                ((MyApplication) getContext().getApplicationContext()).setThemeMode
                                        (SharePreferenceMgr.THEME_AUTO);
                                break;
                        }
                        dialog.dismiss();
                        ((AppCompatActivity) getContext()).recreate();
                    }
                });
                mDialogBuilder.create().show();
                break;
            //关于应用
            case R.id.about_app:
                Intent aboutIntent = new Intent(getContext(), AboutActivity.class);
                startActivity(aboutIntent);
                break;
            //本地文件
            case R.id.local_file:
                Intent intent3 = new Intent(getContext(), LocalFileListActivity.class);
                startActivity(intent3);
                break;
            //反馈
            case R.id.feedback:
                LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
                View contentView = inflater.inflate(R.layout.feedback_layout, null);
                final TextInputEditText mTitle = (TextInputEditText) contentView.findViewById(R.id.title_edit_text);
                final TextInputEditText mContent = (TextInputEditText) contentView.findViewById(R.id.content_edit_text);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setCancelable(false)
                        .setTitle("邮件反馈")
                        .setView(contentView)
                        .setPositiveButton("发送", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String title = mTitle.getText().toString();
                                String content = mContent.getText().toString();
                                if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
                                    Toast.makeText(getContext(), "请填写完整反馈信息！", Toast.LENGTH_SHORT).show();
                                } else {
                                    dialog.dismiss();
                                    Intent data = new Intent(Intent.ACTION_SENDTO);
                                    data.setData(Uri.parse("mailto:devimpl0706@gmail.com"));
                                    data.putExtra(Intent.EXTRA_SUBJECT, title);
                                    data.putExtra(Intent.EXTRA_TEXT, content);
                                    startActivity(data);
                                }

                            }
                        }).setNegativeButton("取消", null);
                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                break;
            //功能说明
            case R.id.functions_introduction:
                break;
            //切换账号
            case R.id.switch_account:
                break;
            default:
                break;
        }
    }

    /**
     * 清空缓存
     */
    private void executeClearCache() {
        AlertDialog dialog = new AlertDialog.Builder(this.getContext())
                .setTitle("清空缓存")
                .setMessage("确定清空缓存吗？这将清除应用的图片缓存，意味着下次使用时需要重新加载图片。")
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File file = Glide.getPhotoCacheDir(SelfFragment.this.getContext());
                        File[] files = file.listFiles();
                        for (File f : files
                                ) {
                            f.delete();
                        }
                        obtainCacheSize();
                        Toast.makeText(getContext(), "清除完成！", Toast.LENGTH_SHORT).show();
                    }
                }).create();
        dialog.show();
    }

    /**
     * 获取指定文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            Log.e("获取文件大小", "文件不存在!");
        }
        return size;
    }

    /**
     * 获取指定文件夹
     *
     * @param f
     * @return
     * @throws Exception
     */
    public static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }
}
