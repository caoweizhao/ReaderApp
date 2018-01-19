package com.example.caoweizhao.readerapp.bean;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.text.DecimalFormat;

/**
 * Created by caoweizhao on 2018-1-13.
 */

public class RecentReadBook extends DataSupport {

    @Column
    private int id;
    @Column
    private long time;
    @Column
    private int page;
    @Column
    private float percent;
    @Column
    private String url;
    @Column
    private Book mBook;

    public Book getBook() {
        return mBook;
    }

    public void setBook(Book book) {
        mBook = book;
    }

    public float getPercent() {
        DecimalFormat df = new DecimalFormat("#####0.00");
        String p = df.format(percent);//返回的是String类型的数据
        return Float.parseFloat(p);
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
