package com.hc.download;

import com.hc.speed.SpeedUtil;
import com.hc.util.HttpUtil;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
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
     * 默认存放在当前工程的目录下，文件名默认为生成的随机UUID
     * @param urlLocation 文件下载地址
     * @param poolLength 线程数
     */
    public static void downLoadFile(String urlLocation, int poolLength) {
        downLoadFile(urlLocation, null, null, poolLength);
    }

    /**
     * 获取文件，根据线程数和文件大小，给每个线程分任务（就是每个线程负责下载哪一部分），提交执行下载任务
     * @param urlLocation 文件下载地址
     * @param filePath 文件本地存储路径，为空则存放在当前工程目录下
     * @param fileName 文件名，为空就设置为随机生成的UUID
     * @param poolLength 线程池大小
     */
    public static void downLoadFile(String urlLocation, String filePath, String fileName, int poolLength){
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

            // 获取本地存储文件路径
            try {
                filePath = handleFileName(httpConnection, filePath, fileName);
            } catch (MimeTypeException e) {
                log.error("未识别到文件类型，请检查链接url是否合法！异常信息：{}", e.getMessage());
            }

            // 开始下载
            startDownload(fileLength, poolLength, urlLocation, filePath);
        }
    }

    /**
     * 开始下载
     * @param fileLength 文件总长度（字节）
     * @param poolLength 线程数（个）
     * @param urlLocation url地址字符串
     * @param filePath 本地文件存储路径（要求是完整路径，带扩展名）
     */
    private static void startDownload(long fileLength, int poolLength, String urlLocation, String filePath) {
        // 创建线程池
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(poolLength, poolLength,
                0, TimeUnit.SECONDS, new LinkedBlockingDeque<>(),
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

        // 文件小于1MB无需分割
        if ( (fileLength / 1024.0) <= 1024) {
            poolLength = 1;
        }
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

    /**
     * 文件名处理，根据url解析文件类型及后缀，设置文件本地存储的路径
     * @param connection url链接对象
     * @param filePath 文件路径
     * @param fileName 文件名
     * @return 返回完整的本地文件存储的路径，当url不合法时返回null
     */
    private static String handleFileName(HttpURLConnection connection, String filePath, String fileName) throws MimeTypeException {
        // 获取文件扩展名
        String contentType = connection.getContentType();
        MimeTypes mimeTypes = MimeTypes.getDefaultMimeTypes();
        String extension;
        MimeType registeredMimeType = mimeTypes.getRegisteredMimeType(contentType);
        extension = registeredMimeType.getExtension();
        // 处理文件存储路径及文件名
        if (filePath != null && filePath.length() != 0) {
            log.info("检测到设置的文件路径为：{}", filePath);
        } else {
            filePath = System.getProperty("user.dir") + "/";
            log.info("未检测到文件存储路径，默认文件存储路径改当前项目目录下, 文件路径为：{}", filePath);
        }
        if (fileName != null && fileName.length() != 0) {
            log.info("检测到设置的文件名为：{}", fileName);
        } else {
            fileName = UUID.randomUUID().toString();
            log.info("未检测到文件名，系统自动生成的文件名为：{}", fileName);
        }
        String fileLocalPath = filePath + fileName + extension;
        log.info("当前文件本地存储路径为：{}", fileLocalPath);
        return fileLocalPath;
    }
}
