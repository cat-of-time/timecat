package com.time.cat.data.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.time.cat.TimeCatApp;
import com.time.cat.data.network.api.ImageUploadService;
import com.time.cat.data.network.api.MicSoftOcrService;
import com.time.cat.data.network.api.NoteService;
import com.time.cat.data.network.api.OcrService;
import com.time.cat.data.network.api.PicUploadService;
import com.time.cat.data.network.api.RoutineService;
import com.time.cat.data.network.api.TaskService;
import com.time.cat.data.network.api.TranslationService;
import com.time.cat.data.network.api.UserService;
import com.time.cat.data.network.api.WordSegmentService;
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

import static com.time.cat.data.network.ConstantURL.BASE_URL;
import static com.time.cat.data.network.ConstantURL.IMAGE_UPLOAD_URL;
import static com.time.cat.data.network.ConstantURL.MICSOFT_OCR_URL;
import static com.time.cat.data.network.ConstantURL.OCR_URL;
import static com.time.cat.data.network.ConstantURL.PIC_UPLOAD_URL;
import static com.time.cat.data.network.ConstantURL.SEGMENT_URL;
import static com.time.cat.data.network.ConstantURL.YOUDAO_URL;

public class RetrofitHelper {

    private static Gson gson = new GsonBuilder().setLenient().create();
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
                    mOkHttpClient = new OkHttpClient.Builder()
                            .cache(cache)
                            .addInterceptor(interceptor)
                            .retryOnConnectionFailure(true)
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(20, TimeUnit.SECONDS)
                            .readTimeout(20, TimeUnit.SECONDS)
                            .build();
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

    public static RoutineService getRoutineService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(RoutineService.class);
    }

    public static NoteService getNoteService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(mOkHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(NoteService.class);
    }

    public void example() {
//        RetrofitHelper.getUserService().createUser(u) //获取Observable对象
//                .compose(SignupActivity.this.bindToLifecycle())
//                .subscribeOn(Schedulers.newThread())//请求在新的线程中执行
//                .observeOn(Schedulers.io())         //请求完成后在io线程中执行
//                .doOnNext(new Action1<User>() {
//                    @Override
//                    public void call(User user) {
////                        saveUser(user);//保存用户信息到本地
//                        DB.users().saveAndFireEvent(Converter.toDBUser(user));
//                        Log.e(TAG, "保存用户信息到本地" + user.toString());
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())//最后在主线程中执行
//                .subscribe(new Subscriber<User>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        //请求失败
//                        Log.e(TAG, e.toString());
//                        onSignupFailed();
//                        progressDialog.dismiss();
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
//                        Log.e(TAG, "请求成功" + user.toString());
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
