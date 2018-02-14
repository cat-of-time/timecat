package com.time.cat.NetworkSystem.api;

import com.time.cat.mvp.model.APImodel.User;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * @author dlink
 * @date 2018/2/6
 * @discription 登录服务接口
 */
public interface LoginService {
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @GET("/users/")
    Observable<User> login(@Query("email") String email, @Query("password") String password, @Query("is_staff") boolean is_staff);

    /**
     * POST
     * {
     * "account": {
     * "nickname": "temp"
     * },
     * "email": "user@example.com",
     * "password": "string",
     * "is_staff": false
     * }
     * response
     * {
     * "id": 5,
     * "url": "http://192.168.88.105:8000/users/5/",
     * "account": {
     * "id": 4,
     * "url": "http://192.168.88.105:8000/accounts/4/",
     * "user": "http://192.168.88.105:8000/users/4/",
     * "nickname": "temp"
     * },
     * "username": "user@example.com",
     * "email": "user@example.com",
     * "is_staff": false,
     * "plans": [],
     * "tags": [],
     * "tasks": []
     * }
     *
     * @param user user
     *
     * @return user
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST("/users/")
    Observable<User> createUser(@Body User user);
}
