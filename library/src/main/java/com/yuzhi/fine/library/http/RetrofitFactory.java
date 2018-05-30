package com.yuzhi.fine.library.http;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.yuzhi.fine.library.http.converter.FastJsonConverterFactory;
import com.yuzhi.fine.library.http.cookie.PersistentCookieJar;
import com.yuzhi.fine.library.http.cookie.cache.SetCookieCache;
import com.yuzhi.fine.library.http.cookie.persistence.SharedPrefsCookiePersistor;
import com.yuzhi.fine.library.http.model.Header;
import com.yuzhi.fine.library.http.utils.HttpsUtils;
import com.yuzhi.fine.library.http.utils.OkHttpDns;
import com.yuzhi.fine.library.utils.AppUtils;
import com.yuzhi.fine.library.utils.DeviceUtils;
import com.yuzhi.fine.library.utils.NetworkUtils;
import com.yuzhi.fine.library.utils.Utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.ConnectionSpec;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class RetrofitFactory {

    private static final String BASE_URL = "http://47.95.216.226:8080/";

    private static final long DEFAULT_READ_TIMEOUT = 15;
    private static final long DEFAULT_WRITE_TIMEOUT = 20;
    private static final long DEFAULT_CONNECT_TIMEOUT = 20;
    private static final long DEFAULT_CACHE_SIZE = 10 * 1024 * 1024; // 10MB

    private static HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
    private static OkHttpClient httpClient = new OkHttpClient.Builder()
            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addNetworkInterceptor(new StethoInterceptor()) // Facebook强大的监测工具：Stetho
            .addInterceptor(new HttpHeaderInterceptor())    // 添加通用的Header
            .addInterceptor(new CommonParamsInterceptor())
            .addInterceptor(new HttpCacheInterceptor())
            .dns(new OkHttpDns(Utils.getApp()))
            .hostnameVerifier(new HttpsUtils.UnSafeHostnameVerifier())
            .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
            .readTimeout(DEFAULT_READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_WRITE_TIMEOUT, TimeUnit.SECONDS)
            .connectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .cache(new Cache(Utils.getApp().getCacheDir(), DEFAULT_CACHE_SIZE))
            .cookieJar(new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(Utils.getApp()))) // 持久化cookie到本地（服务器控制cookie、免登陆）
            .connectionSpecs(Arrays.asList(ConnectionSpec.CLEARTEXT, ConnectionSpec.MODERN_TLS)) //支持HTTPS，明文Http与比较新的Https
            .build();

    /**
     * 通过此方法创建ApiService：
     * 注意：createService自动包装RxJava适配器，如果不使用RxJava请使用createServiceOriginal
     * Create diff module ApiService
     * @param serviceClass
     * @param <T>
     * @return
     */
    public static <T> T createService(Class<T> serviceClass) {
        String baseUrl = "";
        try {
            Field field1 = serviceClass.getField("BASE_URL");
            baseUrl = (String) field1.get(serviceClass);
        } catch (Exception ignored) {
            baseUrl = BASE_URL;
        }
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpClient)
                .addConverterFactory(FastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(serviceClass);
    }

    /**
     * 不适用RxJava
     * @param serviceClass
     * @param <T>
     * @return
     */
    public static <T> T createServiceOriginal(Class<T> serviceClass) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient)
                .addConverterFactory(FastJsonConverterFactory.create())
                .build();
        return retrofit.create(serviceClass);
    }

    // 网络请求公共头信息插入器
    private static class HttpHeaderInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request original = chain.request();
            Request request = original.newBuilder()
                    .header("User-Agent", "Android-" + AppUtils.getAppVersionName() + "-" + AppUtils.getAppVersionCode())
                    .header("Accept", "application/json")
                    .header("Content-type", "application/json")
                    .header("token", Header.token)
                    .method(original.method(), original.body())
                    .build();
            return chain.proceed(request);
        }
    }

    // 网络请求公共参数插入器
    private static class CommonParamsInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            if (request.method().equals("GET")) {
                HttpUrl httpUrl = request.url().newBuilder()
                        .addQueryParameter("version", AppUtils.getAppVersionCode() + "")
                        .addQueryParameter("device", DeviceUtils.getAndroidID())
                        .addQueryParameter("timestamp", String.valueOf(System.currentTimeMillis()))
                        .build();
                request = request.newBuilder().url(httpUrl).build();
            } else if (request.method().equals("POST")) {
                if (request.body() instanceof FormBody) {
                    FormBody.Builder bodyBuilder = new FormBody.Builder();
                    FormBody formBody = (FormBody) request.body();

                    for (int i = 0; i < formBody.size(); i++) {
                        bodyBuilder.addEncoded(formBody.encodedName(i), formBody.encodedValue(i));
                    }

                    formBody = bodyBuilder
                            .addEncoded("version", AppUtils.getAppVersionCode() + "")
                            .addEncoded("device", DeviceUtils.getAndroidID())
                            .addEncoded("timestamp", String.valueOf(System.currentTimeMillis()))
                            .build();

                    request = request.newBuilder().post(formBody).build();
                }
            }

            return chain.proceed(request);
        }
    }

    // 网络请求缓存策略插入器
    private static class HttpCacheInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            // 无网络时，始终使用本地Cache
            if (!NetworkUtils.isConnected()) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            }

            Response response = chain.proceed(request);
            if (NetworkUtils.isConnected()) {
                // 有网络时，设置缓存过期时间0个小时
                int maxAge = 0;
                response.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .removeHeader("Pragma") // 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                        .build();
            } else {
                // 无网络时，设置缓存过期超时时间为4周
                int maxStale = 60 * 60 * 24 * 28;
                response.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .removeHeader("Pragma")
                        .build();
            }
            return response;
        }
    }
}
