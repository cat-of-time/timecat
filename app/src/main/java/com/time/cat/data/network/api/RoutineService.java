package com.time.cat.data.network.api;

import com.time.cat.data.model.APImodel.Routine;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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
public interface RoutineService {
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
     * @param routine routine
     *
     * @return routine
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("/routines/")
    Observable<Routine> createRoutine(@Body Routine routine);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("/")
    Observable<Routine> getRoutine();

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET
    Observable<Routine> getRoutineByUrl(@Url String routine_url);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("/routines/{id}/")
    Observable<Routine> getRoutine(@Path("id") String id);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @GET("/routines/")
    Observable<Routine> getRoutineForUser(@Query("owner") String user_url);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @PUT
    Observable<Routine> putRoutineByUrl(@Url String routine_url, @Body Routine routine);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @DELETE
    Observable<Routine> deleteRoutineByUrl(@Url String routine_url);
}
