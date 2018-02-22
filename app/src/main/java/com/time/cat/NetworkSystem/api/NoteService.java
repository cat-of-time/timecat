package com.time.cat.NetworkSystem.api;

import com.time.cat.mvp.model.Note;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/2/22
 * @discription null
 * @usage null
 */
public interface NoteService {

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("/notes/")
    Observable<Note> createNote(@Body Note note);
}
