package com.hc.download;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;

/**
 * 具体的文件下载类
 * @author: houcheng
 * @date: 2021/9/9 16:45
 * @version: V1.0
 * @description:
 * @modify:
 */
public class DownloadFileTask implements Runnable{

    private static final Logger log = LoggerFactory.getLogger(DownloadFileTask.class);

    /**
     * 文件开始位置
     */
    private final long start ;

    /**
     * 文件结束位置
     */
    private final long end;

    /**
     * url地址
     */
    private final String urlLocation;

    /**
     * 文件存储位置
     */
    private final String filePath;

    public DownloadFileTask(long start, long end, String urlLocation, String filePath) {
        this.start = start;
        this.end = end;
        this.urlLocation = urlLocation;
        this.filePath = filePath;
    }

    @Override
    public void run() {
        HttpURLConnection httpConnection = HttpUtil.getHttpConnection(urlLocation);
        if (httpConnection != null) {
            // 最关键，就是在这里获取要下载那一部分内容
            httpConnection.setRequestProperty("Range", "bytes=" + start +"-"+end);
            log.info(Thread.currentThread().getName() + "负责下载: " + start + "——————" + end);
            File file = new File(filePath);
            SpeedUtil.filePath = filePath;
            try (RandomAccessFile out = new RandomAccessFile(file, "rw");
                 InputStream inputStream = httpConnection.getInputStream()) {
                // 设置文件开始位置
                out.seek(start);
                // 网络流数据读取和写入本地
                byte[] data = new byte[1024];
                int len;
                while ( (len = inputStream.read(data)) != -1) {
                    out.write(data, 0, len);
                    synchronized (SpeedUtil.class) {
                        SpeedUtil.downloaded += len;
                        // 加完一次之后就检查是否下载完成，等每一秒的任务线程查看会出问题，因为可能小文件在1s内会下载完成
                        if (SpeedUtil.downloaded == SpeedUtil.sum) {
                            SpeedUtil.stopTask(true);
                        }
                    }
                }
            } catch (IOException e) {
                log.info("read network stream or write file exception: " + e.getMessage());
            }
        }
    }
}
