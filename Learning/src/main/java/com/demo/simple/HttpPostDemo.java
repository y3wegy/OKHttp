package com.demo.simple;

import com.squareup.okhttp.*;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

/**
 * Created by a549238 on 1/19/2016.
 */
public class HttpPostDemo {

    private static final OkHttpClient client = new OkHttpClient();
    private static final OkHttpClient cachedClient = new OkHttpClient();
    private static final int CACHE_SIZE = 10 * 1024 * 1024;
    private static final String path = "cache";

    @BeforeClass
    public static void initNetWork() {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("161.194.32.195", 80));
        client.setProxy(proxy);

        URL absolutePath = HttpPostDemo.class.getClassLoader().getResource("cache");
        System.out.println("absolutePath:" + absolutePath);
        Cache cache = new Cache(new File(absolutePath.getPath()), CACHE_SIZE);
        cachedClient.setCache(cache);
        cachedClient.setProxy(proxy);
    }

    @Test
    public void testNormalRequest() {
        Request request = new Request.Builder().url("https://www.ibm.com/developerworks/cn/java/j-lo-okhttp/")
                .header("User-Agent", "OKHttp Headers.java")
                .addHeader("Accept", "application/json; q=0.5")
                .addHeader("Accept", "application/vnd.github.v3+json")
                .build();
        RequestBody formbody = new FormEncodingBuilder()
                .add("platform", "android")
                .add("name", "bug")
                .add("subject,", "XXXXXXXXXXXXXXXXX")
                .build();
        request = request.newBuilder().post(formbody).build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                System.out.println(response.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }

    @Test
    public void testCacheRequest() throws Exception {
        Request request = new Request.Builder()
                .url("http://c.csdnimg.cn/public/common/toolbar/css/index.css")
                .build();
        Response response = cachedClient.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }

        String responseBody = response.body().string();

        System.out.println("Response 1 response:" + response);
        System.out.println("Response 1 cache response:" + response.cacheResponse());
        System.out.println("Response 1 network response:" + response.networkResponse());

        Response response1 = cachedClient.newCall(request).execute();

        String responseBody1 = response1.body().string();
        System.out.println("Response 2 response:" + response1);
        System.out.println("Response 2 cache response:" + response1.cacheResponse());
        System.out.println("Response 2 network response:" + response1.networkResponse());

        System.out.println("response equals response1:" + response.equals(responseBody1));
    }

    @Test
    public void testSkipCacheRequest() throws Exception {
        Request request = new Request.Builder()
                .url("http://c.csdnimg.cn/public/common/toolbar/css/index.css")
                .build();
        Response response = cachedClient.newCall(request).execute();

        System.out.println("Response 1 response:" + response);
        System.out.println("Response 1 cache response:" + response.cacheResponse());
        System.out.println("Response 1 network response:" + response.networkResponse());

        request = request.newBuilder().cacheControl(CacheControl.FORCE_NETWORK).build();
        Response response1 = cachedClient.newCall(request).execute();


        System.out.println("Response 1 response:" + response1);
        System.out.println("Response 1 cache response:" + response1.cacheResponse());
        System.out.println("Response 1 network response:" + response1.networkResponse());

    }

    @Test
    public void testCancelRequest() throws Exception {
        Request request = new Request.Builder()
                .url("http://www.baidu.com")
                .build();
        final Call call = cachedClient.newCall(request);
        Response response = call.execute();
        call.cancel();
    }
}
