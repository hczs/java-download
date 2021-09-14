package com.hc.download;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * 分片，多线程下载
 * @author: houcheng
 * @date: 2021/9/9 16:30
 * @version: V1.0
 * @description:
 * @modify:
 */
public class DownloadThreadPool {

    private static final Logger log = LoggerFactory.getLogger(DownloadThreadPool.class);

    /**
     * 获取文件，根据线程数和文件大小，给每个线程分任务（就是每个线程负责下载哪一部分），提交执行下载任务
     * 默认存D盘根目录下，以url最后一个 "/" 后面的字符串作为文件名
     * @param urlLocation 文件下载地址
     * @param poolLength 线程数
     */
    public static void downLoadFile(String urlLocation, int poolLength) {
        downLoadFile(urlLocation, null, poolLength);
    }

    /**
     * 获取文件，根据线程数和文件大小，给每个线程分任务（就是每个线程负责下载哪一部分），提交执行下载任务
     * @param urlLocation 文件下载地址
     * @param filePath 文件本地存储路径，为空则
     * @param poolLength 线程池大小
     */
    public static void downLoadFile(String urlLocation, String filePath, int poolLength) {

        // 获取文件下载长度
        HttpURLConnection httpConnection = HttpUtil.getHttpConnection(urlLocation);
        if (httpConnection != null) {
            long fileLength = httpConnection.getContentLengthLong();
            httpConnection.disconnect();
            SpeedUtil.sum = fileLength;
            // 开启计算速度
            SpeedUtil.calculateSpeed();
            // 无文件长度就不下载
            if (SpeedUtil.sum <= 0) {
                log.info("未获取到文件长度信息，请检查下载链接！");
                SpeedUtil.stopTask(false);
                return;
            } else {
                log.info("文件总大小：" + SpeedUtil.byte2kb2mb(SpeedUtil.sum));
            }

            // 文件小于1MB无需分割
            if ( (fileLength / 1024.0) <= 1024) {
                poolLength = 1;
            }
            // 获取本地存储文件路径
            filePath = handleFileName(urlLocation, filePath);

            // 创建线程池
            ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(poolLength, poolLength,
                    0, TimeUnit.SECONDS, new LinkedBlockingDeque<>(),
                    Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

            // 获取每一片的大小
            long slice = fileLength / poolLength;

            // 将每一片发给不同的线程执行
            for (int i = 0; i < poolLength; i++) {
                long start = i * slice;
                long end = (i + 1) * slice - 1;

                // 如果是最后一组分片，就直接执行到底，可能最后剩下的不够完整的一片
                if (i == poolLength - 1) {
                    start = i * slice;
                    end = fileLength;
                }
                // 创建下载类
                DownloadFileTask downloadFileTask = new DownloadFileTask(start, end, urlLocation, filePath);
                // 将任务添加到线程池中
                poolExecutor.execute(downloadFileTask);
            }
            // 等待已提交执行的任务，之后关闭
            poolExecutor.shutdown();
        }
    }

    /**
     * 文件名处理，为空存入D盘，不为空从URL中取
     * @param urlLocation url
     * @param filePath filePath
     * @return finaPath
     */
    private static String handleFileName(String urlLocation, String filePath) {
        if (filePath == null || filePath.length() == 0) {
            String fileName = urlLocation.substring(urlLocation.lastIndexOf("/") + 1);
            if (fileName.length() == 0) {
                log.info("未识别到文件名，已生成随机字符为文件命名");
                fileName = UUID.randomUUID().toString();
            }
            return "D:\\" + fileName;
        }
        return filePath;
    }
}
