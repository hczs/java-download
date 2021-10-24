# java-download
java实现多线程下载
## 介绍
使用slf4j做日志打印，java.net包做网络连接，以及基本的IO流操作和多线程，实现多线程下载文件资源，可以自动解析url中文件资源类型及文件名，无需手动设置

主要是为了复习多线程和IO操作
## 效果展示
浏览器下载，速度在 181KB/s
![浏览器](https://user-images.githubusercontent.com/43227582/136026466-f24ba879-d535-48bd-9f55-ab8b7eef94a4.png)
多线程下载，速度在 1.25MB/s
![多线程](https://user-images.githubusercontent.com/43227582/136026545-3c0266fd-93da-499f-a256-f45786b60c3e.png)
## 基本使用
1. 克隆本项目到本地
2. 下载相关依赖包
3. 查看Main类，执行main方法即可

基本使用可以查看Main类
```java
public static void main(String[] args) {
        // 测试数据一：下载图片
        String imgUrl = "http://e.hiphotos.baidu.com/image/pic/item/a1ec08fa513d2697e542494057fbb2fb4316d81e.jpg";
        // 测试数据二：下载镜像
        String isoUrl = "http://hk.mirrors.thegigabit.com/centos/8.4.2105/isos/x86_64/CentOS-8.4.2105-x86_64-boot.iso";
        // 测试数据三：下载视频
        String videoUrl = "http://mirror.aarnet.edu.au/pub/TED-talks/911Mothers_2010W-480p.mp4";
        DownloadThreadPool.downLoadFile(videoUrl, 16);
    }
```

主要是封装了DownLoadThreadPool类的downLoadFile方法，方法定义如下：
```java
    /**
     * 获取文件，根据线程数和文件大小，给每个线程分任务（就是每个线程负责下载哪一部分），提交执行下载任务
     * 默认存放在当前工程的目录下，文件名默认为生成的随机UUID
     * @param urlLocation 文件下载地址
     * @param poolLength 线程数
     */
    public static void downLoadFile(String urlLocation, int poolLength) {...}

    /**
     * 获取文件，根据线程数和文件大小，给每个线程分任务（就是每个线程负责下载哪一部分），提交执行下载任务
     * @param urlLocation 文件下载地址
     * @param filePath 文件本地存储路径，为空则存放在当前工程目录下
     * @param fileName 文件名，为空就设置为随机生成的UUID
     * @param poolLength 线程池大小
     */
    public static void downLoadFile(String urlLocation, String filePath, String fileName, int poolLength){...}
```
## 后续工作
1. 实现下载可暂停
2. 在性能达标后编写界面