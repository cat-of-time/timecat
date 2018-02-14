package com.time.cat.NetworkSystem.api;

import com.time.cat.mvp.model.APImodel.WordSegs;

import java.util.ArrayList;

import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

/**
 * @author dlink
 * @date 2018/2/2
 */
public interface WordSegmentService {
    //请自行在http://bosonnlp.com/account/register 注册账号，并用自己的API密钥替换下面的X-Token的值
    String XToken = "L8LIQcbG.23465.XXP67wBHuaLu";

    @Headers({"Content-Type: application/json", "Accept: application/json", "X-Token: " + XToken})
    @POST("tag/analysis?space_mode=1&oov_level=3&t2s=0&special_char_conv=1")
    Observable<ArrayList<WordSegs>> getWordSegsList(@Body String string);
}
