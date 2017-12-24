package com.example.caoweizhao.readerapp.API;

import com.example.caoweizhao.readerapp.bean.Book;
import com.example.caoweizhao.readerapp.bean.Collection;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by caoweizhao on 2017-12-24.
 */

public interface CollectionServices {

    @GET(value = "collection/queryAll/{userId}")
    Observable<List<Collection>> queryAllByUser(@Path(value = "userId") String userId);

    @GET(value = "book/books/{id}")
    Call<Book> queryBookById(@Path(value = "id")Integer id);
}
