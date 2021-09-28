# java-download
java实现多线程下载
## 介绍
使用slf4j做日志打印，java.net包做网络连接，以及基本的IO流操作和多线程，实现多线程下载文件资源

主要是为了复习多线程和IO操作

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
     * 默认存D盘根目录下，以url最后一个 "/" 后面的字符串作为文件名
     * @param urlLocation 文件下载地址
     * @param poolLength 线程数
     */
    public static void downLoadFile(String urlLocation, int poolLength){...}
    
    /**
     * 获取文件，根据线程数和文件大小，给每个线程分任务（就是每个线程负责下载哪一部分），提交执行下载任务
     * @param urlLocation 文件下载地址
     * @param filePath 文件本地存储路径，为空则
     * @param poolLength 线程池大小
     */
    public static void downLoadFile(String urlLocation, String filePath, int poolLength)
```
