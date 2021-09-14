package com.hc.download;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

/**
 * Http连接工具类，主要是获取HttpURLConnection
 * @author: houcheng
 * @date: 2021/9/9 16:22
 * @version: V1.0
 * @description:
 * @modify:
 */
public class HttpUtil {

    private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);

    /**
     * 通过url地址获取HttpURLConnection连接对象
     * 不使用代理
     * @param urlLocation url
     * @return HttpURLConnection连接对象
     */
    public static HttpURLConnection getHttpConnection(String urlLocation) {
        return getHttpConnection(urlLocation, null);
    }

    /**
     * 通过url地址获取HttpURLConnection连接对象
     * 使用代理
     * @param urlLocation url
     * @param proxy 代理对象
     * @return HttpURLConnection连接对象
     */
    public static HttpURLConnection getHttpConnection(String urlLocation, Proxy proxy) {
        try {
            URL url = new URL(urlLocation);
            HttpURLConnection urlConnection;
            if (proxy == null) {
                urlConnection = (HttpURLConnection) url.openConnection();
            } else {
                urlConnection = (HttpURLConnection) url.openConnection(proxy);
            }
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestMethod("GET");
            return urlConnection;
        } catch (IOException e) {
            log.error("url连接异常：" + e.getMessage());
        }
        return null;
    }
}
