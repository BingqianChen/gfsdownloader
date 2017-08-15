package com.xinhong.gfs.download;

import com.xinhong.ftp.util.FTPUtil;
import com.xinhong.util.*;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016-06-14.
 */
public class GFSStarter {
    private final static String threadtimePath = ConfigUtil.getProperty(ConfigCommon.THREAD_TIME_PATH);
    private final Logger logger = Logger.getLogger(GFSStarter.class);

    static {
        init();
        FtpConfig.init();
    }

    // init config
    public static void init() {
        try {
            //不能用log4j.properties ,如果别的jar包中有log4j.properties，将会影响
            //classLoader.getResourceAsStream()==null 的判断，会加载jar别的类库中的log4j.properties
            FileInputStream fileInputStream = new FileInputStream(new File("gfsConf/log4j.properties"));
            PropertyConfigurator.configure(fileInputStream);
            System.out.println("配置文件加载失败：FileInputStream fileInputStream = new FileInputStream(new File(gfsConf/log4j.properties));\n" +
                    "            PropertyConfigurator.configure(fileInputStream);\r\n" +
                    "采用ClassLoader加载方式");
        } catch (FileNotFoundException e) {
            try {
                ClassLoader classLoader = GFSStarter.class.getClassLoader();
                PropertyConfigurator.configure(classLoader.getResourceAsStream("gfsConf/log4j.properties"));
            } catch (Exception e1) {
                System.out.println("ClassLoader加载配置文件失败，ClassLoader classLoader = PoolMainThread.class.getClassLoader();\n" +
                        "                PropertyConfigurator.configure(classLoader.getResourceAsStream(gfsConf/log4j.properties));");
                e1.printStackTrace();
            }
        }
    }

    private ExecutorService pool = null;
    /**
     * 关闭线程池
     */
    private void shutdownPool() {
        pool.shutdown();
    }

    private String getHH(String hour) {
        String HH = null;
        if (hour.matches("\\d+")) {
            int ihour = Integer.valueOf(hour);
            if (ihour <= 15 && ihour >= 4) HH = "00";
            else {
                if ((ihour <= 24 && ihour >= 16) || (ihour < 4 && ihour >= 0)) HH = "12";
            }
        }
        return HH;
    }

    private void start() {
        // 启动下载消息中心
        new Thread(new Runnable() {
            @Override
            public void run() {
                new DownloadTaskCenter().init();
            }
        }).start();
        pool = Executors.newFixedThreadPool(FtpConfig.getThreads());
        //下载文件
        while (true) {
            //请求下载任务
            DownloadTask remote = DownloadTaskCenter.getInstance().asignTask();
            if(remote!=null&&remote.getRemote()!=null) {
                PoolFTPDownloader ftpDownloader = new PoolFTPDownloader(FtpConfig.getHOST(),
                        FtpConfig.getUsername(), FtpConfig.getPassword(), FtpConfig.getPORT(),
                        remote.getRemote(), remote.getLocal());
                pool.execute(ftpDownloader);
            }
            try {
                Thread.sleep(50000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    public static void main(String[] args) {
        //首次启动时，删除下载记录中为正在下载的数据
        //因正在下载的数据未完全下载完成，需继续下载
        GFSStarter starter = new GFSStarter();
        starter.start();
    }


}