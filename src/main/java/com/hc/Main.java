package com.hc;


import com.hc.download.DownloadThreadPool;

/**
 * @author: houcheng
 * @date: 2021/9/9 16:57
 * @version: V1.0
 * @description:
 * @modify:
 */
public class Main {

    public static void main(String[] args) {
        // 测试数据一：下载图片
        String imgUrl = "http://e.hiphotos.baidu.com/image/pic/item/a1ec08fa513d2697e542494057fbb2fb4316d81e.jpg";
        // 测试数据二：下载镜像
        String isoUrl = "http://hk.mirrors.thegigabit.com/centos/8.4.2105/isos/x86_64/CentOS-8.4.2105-x86_64-boot.iso";
        // 测试数据三：下载视频
        String videoUrl = "http://mirror.aarnet.edu.au/pub/TED-talks/911Mothers_2010W-480p.mp4";
        DownloadThreadPool.downLoadFile(videoUrl, 16);
    }
}
