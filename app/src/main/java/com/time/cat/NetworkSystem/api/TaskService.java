package com.time.cat.NetworkSystem.api;

import com.time.cat.mvp.model.Task;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;

/**
 * @author dlink
 * @email linxy59@mail2.sysu.edu.cn
 * @date 2018/2/20
 * @discription null
 * @usage null
 */
public interface TaskService {
    /**
     * POST
       {
            "owner": "http://example.com",
            "plan": "http://example.com",
            "title": "string",
            "content": "string",
            "label": 0,
            "tags": [
            "http://example.com"
            ],
            "is_finished": true,
            "finished_datetime": "2018-02-20T04:27:46Z",
            "is_all_day": true,
            "begin_datetime": "2018-02-20T04:27:46Z",
            "end_datetime": "2018-02-20T04:27:46Z"
        }
     * response
       {
            "url": "http://example.com",
            "owner": "http://example.com",
            "created_datetime": "2018-02-20T04:27:46Z",
            "plan": "http://example.com",
            "title": "string",
            "content": "string",
            "label": 0,
            "tags": [
            "http://example.com"
            ],
            "is_finished": true,
            "finished_datetime": "2018-02-20T04:27:46Z",
            "is_all_day": true,
            "begin_datetime": "2018-02-20T04:27:46Z",
            "end_datetime": "2018-02-20T04:27:46Z"
        }
     *
     * @param task task
     *
     * @return task
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("/tasks/")
    Observable<Task> createTask(@Body Task task);

    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @GET("/")
    Observable<Task> getTask();

    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @GET
    Observable<Task> getTaskByUrl(@Url String task_url);

    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @GET("/tasks/{id}/")
    Observable<Task> getTask(@Path("id") String id);

    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @GET("/tasks/")
    Observable<Task> getTaskForUser(@Query("owner") String user_url);


}
