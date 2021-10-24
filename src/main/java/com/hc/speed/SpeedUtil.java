package com.hc.speed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * 计算下载速度工具类
 * @author: houcheng
 * @date: 2021/9/13 9:16
 * @version: V1.0
 * @description:
 * @modify:
 */
public class SpeedUtil {

    private static final Logger log = LoggerFactory.getLogger(SpeedUtil.class);

    /**
     * 已下载的长度 单位（byte）
     * long最大是 (2^63 - 1) byte = 9.22337204 × 10^9 GB = 9 223 372.04 TB
     * 完全够用
     */
    public static long downloaded = 0L;

    /**
     * 要下载的文件总大小 单位（byte）
     */
    public static long sum;

    /**
     * 文件下载开始时间
     */
    public static long startTime = System.currentTimeMillis();

    /**
     * 用于终止定时任务
     */
    public static ScheduledExecutorService scheduledExecutor = null;

    /**
     * 文件下载保存到本地路径
     */
    public static String filePath;

    /**
     * 计算下载速度
     */
    public static void calculateSpeed() {
        // 开一个定时任务线程，专门用来统计计算下载速度
        scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutor.scheduleAtFixedRate(new SpeedScheduleTask(), 0, 1, TimeUnit.SECONDS);
    }

    /**
     * 停止任务，关闭线程池，打印结束语
     * @param tips 是否打印结束语
     */
    public static void stopTask(boolean tips) {
        if (tips) {
            long sumTime = (System.currentTimeMillis() - startTime) / 1000;
            log.info("下载完成！共耗时：" + sumTime + " 秒" + "，平均速度：" + speedUnit(sum, sumTime) + "文件路径为：" + filePath);
        } else {
            log.info("下载任务结束！");
        }
        scheduledExecutor.shutdownNow();
    }

    /**
     * 计算具体速度，根据实际情况进行转换当前网速，比如不够 1mb/s 展示为具体的多少kb每秒
     * @param byteLength 字节长度，单位（字节）
     * @param time 时间单位（秒）
     * @return 具体速度（保留两位小数）带单位. 单位 B/S 或 KB/S 或 MB/S
     */
    public static String speedUnit(long byteLength, long time) {
        double speedByte = (double) byteLength / time;
        double speedKb = speedByte / 1024.0;
        double speedMb = speedKb / 1024.0;
        // 注意判断顺序，优先返回大的
        if ( speedKb > 1024) {
            return String.format("%.2f", speedMb) + " MB/S";
        } else if (speedByte > 1024) {
            return String.format("%.2f", speedKb) + " KB/S";
        }
        return String.format("%.2f", speedByte) + " B/S";
    }

    /**
     * 文件长度byte，转换为kb或mb表示
     * @param byteLength 文件长度，单位byte
     * @return 转换后的
     */
    public static String byte2kb2mb(long byteLength) {
        double kb = byteLength / 1024.0;
        double mb = kb / 1024.0;
        // 判断顺序，优先GB，其次MB，其次KB，最后byte
        if (mb > 1024) {
            return (mb / 1024.0) + " GB";
        } else if (kb > 1024) {
            return mb + " MB";
        } else if (byteLength > 1024) {
            return kb + " KB";
        }
        return byteLength + " B";
    }

}
