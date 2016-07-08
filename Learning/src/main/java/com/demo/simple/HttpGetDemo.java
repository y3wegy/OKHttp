package com.demo.simple;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * Created by a549238 on 1/19/2016.
 */
public class HttpGetDemo {


    private static final OkHttpClient client = new OkHttpClient();

    @BeforeClass
    public static void initNetWork() {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("161.194.32.195", 80));
        client.setProxy(proxy);

    }

    @Test
    public void testNormalRequest() {
        Request request = new Request.Builder().url("http://www.google.com").build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                System.out.println(response.code());
                System.out.println(response.body().string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testNotSyncRequest() throws Exception {
        Request request = new Request.Builder().url("http://www.google.com").build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                System.out.println("request failed");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                System.out.println("response code :" + response.code());
                System.out.println("response body:" + response.body().string());
            }
        });
        Thread.sleep(5 * 1000);
    }
}
