package com.hc.speed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 定时任务，计算实时下载速度
 * @author: houcheng
 * @date: 2021/9/13 10:27
 * @version: V1.0
 * @description:
 * @modify:
 */
public class SpeedScheduleTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(SpeedScheduleTask.class);

    /**
     * 记录上一次计算网速下载了多少byte
     */
    private long old = 0;

    @Override
    public void run() {
        // now：记录上一秒下载了多少byte
        long now = SpeedUtil.downloaded - old;
        old = SpeedUtil.downloaded;
        double percent = ( (double)SpeedUtil.downloaded / (double) SpeedUtil.sum) * 100;
        //TODO 预计剩余时间: 剩余大小 / 过去的平均速度 过去的平均速度 = 当前已下载量 / (当前时间 - 开始时间)
        log.info("当前网速：" + SpeedUtil.speedUnit(now, 1) + " 已下载：" + SpeedUtil.byte2kb2mb(SpeedUtil.downloaded) + " 百分比：" + String.format("%.2f", percent) + "%");

        // 下载完成之后停止任务
        if (percent == 100) {
            SpeedUtil.stopTask(true);
        }
    }

}
