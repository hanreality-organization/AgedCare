package com.punuo.sys.sdk.httplib;

import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by han.chen.
 * Date on 2019/4/23.
 **/
public class HttpManager {
    //timeout 30s
    public static final long DEFAULT_TIME_OUT = 30L;
    private static volatile OkHttpClient sOkHttpClient;
    private static OkHttpClient.Builder sBuilder = new OkHttpClient.Builder();
    private static boolean isDebug = false;
    private static final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    private static final String cacheDir = "/storage/emulated/0/Android/data/com.punuo.sys.app.agedcare/cache";
    public static OkHttpClient getOkHttpClient() {
        init();
        return sOkHttpClient;
    }

    public static void init() {
        if (sOkHttpClient == null) {
            synchronized (HttpManager.class) {
                if (sOkHttpClient == null) {
                    sBuilder.connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                            .readTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                            .writeTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                            .followRedirects(true)
                            .retryOnConnectionFailure(true)
                            .cookieJar(new CookieJar() {
                                @Override
                                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                                    cookieStore.put(url.host(), cookies);
                                }

                                @Override
                                public List<Cookie> loadForRequest(HttpUrl url) {
                                    List<Cookie> cookies = cookieStore.get(url.host());
                                    return cookies != null ? cookies : new ArrayList<Cookie>();
                                }
                            })
                            .cache(new Cache(new File(cacheDir, "okhttp"),
                                    500 * 1024 * 1024));
                    debugInit();
                    sOkHttpClient = sBuilder.build();
                }
            }
        }
    }

    /**
     * debug 模式允许所有证书有效
     */
    private static void debugInit() {
        if (isDebug) {
            try {
                final X509TrustManager[] trustAllcerts = new X509TrustManager[]{
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                            }

                            @Override
                            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                            }

                            @Override
                            public X509Certificate[] getAcceptedIssuers() {
                                return new X509Certificate[0];
                            }
                        }
                };
                SSLContext sslContext = null;
                try {
                    sslContext = SSLContext.getInstance("TLSv1.2");
                } catch (NoSuchAlgorithmException e) {
                    try {
                        sslContext = SSLContext.getInstance("TLSv1.1");
                    } catch (NoSuchAlgorithmException e1) {
                        sslContext = SSLContext.getInstance("TLS");
                    }
                }

                sslContext.init(null, trustAllcerts, new SecureRandom());

                sBuilder.sslSocketFactory(sslContext.getSocketFactory(), trustAllcerts[0]);
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setDebug(boolean debug) {
        isDebug = debug;
    }

    /**
     * 必须在OkHttpClient初始化之前调用
     * @param interceptor
     * @return
     */
    public static OkHttpClient.Builder addInterceptor(Interceptor interceptor) {
        if (interceptor == null) {
            throw  new IllegalArgumentException("interceptor is null");
        }
        sBuilder.addInterceptor(interceptor);
        return sBuilder;
    }

    private static ExecutorDelivery sDelivery = new ExecutorDelivery(new Handler(Looper
            .getMainLooper()));

    public static void addRequest(final NetRequest netRequest) {
        final Request request = netRequest.build();
        HttpManager.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sDelivery.postError(netRequest, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    handlerResponse(netRequest, response, true);
                } catch (Exception e) {
                    sDelivery.postError(netRequest, e);
                }
            }
        });
    }

    /**
     * 执行网络请求
     * 同步执行
     * 注意：使用完一定要注意最后是否已经close
     *
     * @param request NetRequest obj
     * @return Response obj
     */
    public static Response execute(NetRequest request) {
        Request requestTemp = request.build();

        try {
            Response okHttpResponse = HttpManager.getOkHttpClient().newCall(requestTemp).execute();
            return handlerResponse(request, okHttpResponse, false);
        } catch (Exception e) {
            sDelivery.postCacheResponse(request, null, e);
            return null;
        }
    }

    private static Response handlerResponse(NetRequest request,
                                            Response response, boolean async) throws IOException {
        if (response.isSuccessful()) {
            if (async) {
                sDelivery.postResponse(request, response);
            }
            return response;
        } else {
            ErrorTipException error;
            if (response.isRedirect()) {
                error = new ErrorTipException("当前请求被劫持");
            } else {
                switch (response.code()) {
                    case 400:
                    case 403:
                    case 404:
                    case 405:
                    case 406:
                        error = new ErrorTipException("请求资源失效");
                        break;
                    case 408:
                    case 504:
                        error = new ErrorTipException("网络超时");
                        break;
                    case 500:
                    case 501:
                    case 502:
                    case 503:
                        error = new ErrorTipException("服务器异常");
                        break;
                    default:
                        error = new ErrorTipException("网络服务繁忙");
                        break;
                }
            }
            if (response.body() != null) {
                response.close();
            }
            error.mResposeCode = response.code();
            if (async) {
                sDelivery.postError(request, error);
            }
            return response;
        }
    }
}
