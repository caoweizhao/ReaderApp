package com.example.caoweizhao.readerapp.mvp.model.API;

import com.example.caoweizhao.readerapp.bean.Book;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by caoweizhao on 2017-9-22.
 */

public interface BookStoreService {

    @GET("book/images/{path}")
    void getImage(@Path("path") String path);

    @GET("book/books")
    Observable<List<Book>> getBooks();
}
