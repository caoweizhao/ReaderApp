package com.example.caoweizhao.readerapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by caoweizhao on 2017-12-24.
 */

public class DownloadDatabaseHelper extends SQLiteOpenHelper {

    private String CREATE_TABLE_THREAD = "create table Thread( " +
            "id varchar(20) primary key not null," +
            "downloadedLength BIGINT not null) ";

    private Context mContext;

    public DownloadDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_THREAD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
