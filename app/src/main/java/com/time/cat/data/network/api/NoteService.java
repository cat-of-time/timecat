package com.time.cat.data.network.api;

import com.time.cat.data.model.APImodel.Note;

import java.util.ArrayList;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Url;
import rx.Observable;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/2/22
 * @discription 笔记服务api
 * @usage null
 */
public interface NoteService {
    //增
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("/notes/")
    Observable<Note> createNote(@Body Note note);
    //删
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @DELETE
    Observable<Note> deleteNoteByUrl(@Url String note_url);
    //改
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @PUT
    Observable<Note> putNoteByUrl(@Url String note_url, @Body Note note);
    //查
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("/notes/")
    Observable<ArrayList<Note>> getNotesAll();

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET
    Observable<Note> getNoteByUrl(@Url String note_url);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("/notes/{id}/")
    Observable<Note> getNote(@Path("id") String id);
}
