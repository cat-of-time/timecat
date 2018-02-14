package com.time.cat.NetworkSystem.api;

import com.time.cat.mvp.model.APImodel.ImageUpload;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by wangyan-pd on 2017/1/9.
 */
//Content-Disposition: form-data; name="smfile"; filename="1.png"
//Content-Type: image/png
public interface ImageUploadService {
    @Headers({"Accept:*/*", "Accept-Encoding:gzip, deflate", "Accept-Language:zh-CN,zh;q=0.8", "Connection:keep-alive", "Content-Type:multipart/form-data", "User-Agent:Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36"})
    // @Multipart
    @POST("/api/upload")
    Observable<ImageUpload> uploadImage4search(@Body RequestBody imgs);
    // Observable<ImageUpload> uploadImage4search(@PartMap Map<String, Object> fields, @Body RequestBody imgs);
    //@Part("smfile; filename=\"abc.jpg\"")
}
