package com.time.cat.NetworkSystem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.time.cat.NetworkSystem.api.ImageUploadService;
import com.time.cat.NetworkSystem.api.MicSoftOcrService;
import com.time.cat.NetworkSystem.api.OcrService;
import com.time.cat.NetworkSystem.api.PicUploadService;
import com.time.cat.NetworkSystem.api.TaskService;
import com.time.cat.NetworkSystem.api.TranslationService;
import com.time.cat.NetworkSystem.api.UserService;
import com.time.cat.NetworkSystem.api.WordSegmentService;
import com.time.cat.TimeCatApp;
import com.time.cat.util.override.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {
    private static final String SEGMENT_URL = "http://api.bosonnlp.com/";
    private static final String YOUDAO_URL = "http://fanyi.youdao.com/";
    private static final String OCR_URL = "https://api.ocr.space/";
    private static final String MICSOFT_OCR_URL = "https://api.projectoxford.ai/";
    //     private static final String IMAGE_UPLOAD_URL = "http://up.imgapi.com/";
    private static final String IMAGE_UPLOAD_URL = "https://sm.ms/";
    private static final String PIC_UPLOAD_URL = "https://yotuku.cn/";
    private static final String BASE_URL = "http://192.168.88.105:8000/";

    static Gson gson = new GsonBuilder().setLenient().create();
    private static OkHttpClient mOkHttpClient;

    static {
        initOkHttpClient();
    }

    /**
     * 初始化OKHttpClient
     * 设置缓存
     * 设置超时时间
     * 设置打印日志
     * 设置UA拦截器
     */
    private static void initOkHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new Log());
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        if (mOkHttpClient == null) {
            synchronized (RetrofitHelper.class) {
                if (mOkHttpClient == null) {
                    //设置Http缓存
                    Cache cache = new Cache(new File(TimeCatApp.getInstance().getCacheDir(), "HttpCache"), 1024 * 1024 * 100);
                    mOkHttpClient = new OkHttpClient.Builder().cache(cache).addInterceptor(interceptor).retryOnConnectionFailure(true).connectTimeout(30, TimeUnit.SECONDS).writeTimeout(20, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).build();
                }
            }
        }
    }

    public static TranslationService getTranslationService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(YOUDAO_URL)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(TranslationService.class);
    }

    public static WordSegmentService getWordSegmentService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(SEGMENT_URL).client(mOkHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(WordSegmentService.class);
    }

    public static OcrService getOcrService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(OCR_URL).client(mOkHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(OcrService.class);
    }

    public static MicSoftOcrService getMicsoftOcrService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MICSOFT_OCR_URL)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(MicSoftOcrService.class);
    }

    public static ImageUploadService getImageUploadService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(IMAGE_UPLOAD_URL)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit.create(ImageUploadService.class);
    }

    public static PicUploadService getPicUploadService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(PIC_UPLOAD_URL)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(PicUploadService.class);
    }

    public static UserService getUserService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(UserService.class);
    }

    public static TaskService getTaskService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(TaskService.class);
    }

    public void example() {
//        RetrofitHelper.getUserService().createUser(u) //获取Observable对象
//                .compose(SignupActivity.this.bindToLifecycle()).subscribeOn(Schedulers.newThread())//请求在新的线程中执行
//                .observeOn(Schedulers.io())         //请求完成后在io线程中执行
//                .doOnNext(new Action1<User>() {
//                    @Override
//                    public void call(User user) {
////                        saveUser(user);//保存用户信息到本地
//                        DB.users().saveAndFireEvent(ModelUtil.toDBUser(user));
//                        android.util.Log.e(TAG, "保存用户信息到本地" + user.toString());
//                    }
//                }).observeOn(AndroidSchedulers.mainThread())//最后在主线程中执行
//                .subscribe(new Subscriber<User>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        //请求失败
//                        android.util.Log.e(TAG, e.toString());
//                        onSignupFailed();
//                        progressDialog.dismiss();
//
//                    }
//
//                    @Override
//                    public void onNext(User user) {
//                        //请求成功
//                        intent = new Intent(SignupActivity.this, MainActivity.class);
//                        intent.putExtra(LoginActivity.INTENT_USER_EMAIL, user.getEmail());
//                        setResult(RESULT_OK, intent);
//                        onSignupSuccess();
//                        progressDialog.dismiss();
//                        android.util.Log.e(TAG, "请求成功" + user.toString());
//                    }
//                });
    }

    public static class Log implements HttpLoggingInterceptor.Logger {
        @Override
        public void log(String message) {
            LogUtil.d(message);
        }
    }

    /**
     * 添加UA拦截器
     * B站请求API文档需要加上UA
     */
    private static class UserAgentInterceptor implements Interceptor {

        private static final String COMMON_UA_STR = "";

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request requestWithUserAgent = originalRequest.newBuilder().removeHeader("User-Agent").addHeader("User-Agent", COMMON_UA_STR).build();
            return chain.proceed(requestWithUserAgent);
        }
    }
}
