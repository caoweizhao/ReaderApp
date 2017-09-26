package com.example.caoweizhao.readerapp.mvp.model.API;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

/**
 * Created by caoweizhao on 2017-9-22.
 */

public interface ReaderService {

    @Streaming
    @GET(value = "book/{path}")
    Observable<ResponseBody> getBook(@Path(value = "path") String url);

    @Streaming
    @GET(value = "book/{path}")
    Observable<ResponseBody> getBookSegment(@Path(value = "path") String url, @Header("Range") String range);

    @GET(value = "book/size/{path}")
    Observable<Response<String>> getBookSize(@Path(value = "path") String url);
}
