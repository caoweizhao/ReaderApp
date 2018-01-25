package com.example.caoweizhao.readerapp.API;

import com.example.caoweizhao.readerapp.bean.Note;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by caoweizhao on 2018-1-25.
 */

public interface NoteService {

    @POST(value = "note/add")
    Observable<Note> addNote(@Body Note note);

    @GET(value = "note/queryAll")
    Observable<List<Note>> queryAll(@Query(value = "bookId") int bookId,
                                    @Query(value = "userName") String userName,
                                    @Query(value = "sort") String sort);

    @PATCH(value = "note/delete")
    Observable<ResponseBody> deleteNote(@Body Note note);
}
