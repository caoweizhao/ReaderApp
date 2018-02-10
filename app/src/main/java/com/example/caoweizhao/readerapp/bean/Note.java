package com.example.caoweizhao.readerapp.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by caoweizhao on 2018-1-25.
 */

public class Note implements Parcelable {

    /**
     * id : 1
     * title : null
     * content : 哈哈
     * page : 20
     * bookId : 2
     * createTime : 1516853330000
     */

    @SerializedName("id")
    private long id;
    @SerializedName("title")
    private String title;
    @SerializedName("content")
    private String content;
    @SerializedName("page")
    private int page;
    @SerializedName("bookId")
    private int bookId;
    @SerializedName("createTime")
    private long createTime;
    @SerializedName("userName")
    private String userName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeInt(this.page);
        dest.writeInt(this.bookId);
        dest.writeLong(this.createTime);
        dest.writeString(this.userName);
    }

    public Note() {
    }

    protected Note(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.content = in.readString();
        this.page = in.readInt();
        this.bookId = in.readInt();
        this.createTime = in.readLong();
        this.userName = in.readString();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel source) {
            return new Note(source);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Note) {
            if (((Note) obj).getId() == this.getId()) {
                return true;
            }
        }
        return false;
    }
}
