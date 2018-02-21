package com.example.caoweizhao.readerapp.API;

import com.example.caoweizhao.readerapp.bean.Book;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by caoweizhao on 2017-9-22.
 */

public interface BookStoreService {

    /**
     * 获取图片
     *
     * @param path
     */
    @GET("book/images/{path}")
    void getImage(@Path("path") String path);

    /**
     * 获取图书
     *
     * @return
     */
    @GET("book/books/category/{category}")
    Observable<List<Book>> getBooks(@Path("category") String category);
}
