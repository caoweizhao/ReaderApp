package com.example.caoweizhao.readerapp.API;

import com.example.caoweizhao.readerapp.bean.Collection;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

/**
 * Created by caoweizhao on 2017-9-22.
 */

public interface ReaderService {

    /**
     * 获取图书
     * @param url
     * @return
     */
    @Streaming
    @GET(value = "book/{path}")
    Observable<ResponseBody> getBook(@Path(value = "path") String url);

    /**
     * 获取部分图书（分段下载）
     * @param url
     * @param range
     * @return
     */
    @Streaming
    @GET(value = "book/{path}")
    Observable<ResponseBody> getBookSegment(@Path(value = "path") String url, @Header("Range") String range);

    /**
     * 获取图书大小
     * @param url
     * @return
     */
    @GET(value = "book/size/{path}")
    Observable<Response<String>> getBookSize(@Path(value = "path") String url);

    /**
     * 收藏
     * @param collection
     * @return
     */
    @POST(value = "collection/collect")
    Observable<ResponseBody> collectBook(@Body Collection collection);

    /**
     * 取消收藏
     * @param collection
     * @return
     */
    @PATCH(value = "collection/uncollect")
    Observable<ResponseBody> unCollectBook(@Body Collection collection);

    /**
     * 查看是否已收藏
     * @param collection
     * @return
     */
    @POST(value = "collection/query")
    Observable<Boolean> isCollected(@Body Collection collection);


}
