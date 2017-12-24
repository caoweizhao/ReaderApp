package com.example.caoweizhao.readerapp.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable{
    private Integer id;
    private String name;
    private String author;
    private String category;
    private String img_url;
    private String url;
    private String publisher;
    private String summary;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.name);
        dest.writeString(this.author);
        dest.writeString(this.category);
        dest.writeString(this.img_url);
        dest.writeString(this.url);
        dest.writeString(this.publisher);
        dest.writeString(this.summary);
    }

    public Book() {
    }

    protected Book(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.name = in.readString();
        this.author = in.readString();
        this.category = in.readString();
        this.img_url = in.readString();
        this.url = in.readString();
        this.publisher = in.readString();
        this.summary = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel source) {
            return new Book(source);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
}
