package com.time.cat.NetworkSystem.api;

import com.time.cat.mvp.model.Task;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
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

}
