package com.xinhong.gfs.download;

import com.xinhong.gfs.processor.DetailedSeqThreadsPool;
import com.xinhong.gfs.processor.GfsParseProcess;
import com.xinhong.util.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2016-06-14.
 */
public class GFSStarter {
    private final static String threadtimePath = ConfigUtil.getProperty(ConfigCommon.THREAD_TIME_PATH);
    private final Logger logger = Logger.getLogger(GFSStarter.class);
    boolean requestFlag=true;

    static {
        init();
        FtpConfig.init();
    }

    // init config
    public static void init() {
        try {
            //不能用log4j.properties ,如果别的jar包中有log4j.properties，将会影响
            //classLoader.getResourceAsStream()==null 的判断，会加载jar别的类库中的log4j.properties
            String path=GFSStarter.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            String rootpath=new File(path).getParent();
            FileInputStream fileInputStream = new FileInputStream(new File(rootpath+"/gfsConf/log4j.properties"));
            PropertyConfigurator.configure(fileInputStream);
            System.out.println("加载包外log4j配置文件");
        } catch (FileNotFoundException e) {
            try {
                ClassLoader classLoader = GFSStarter.class.getClassLoader();
                PropertyConfigurator.configure(classLoader.getResourceAsStream("gfsConf/log4j.properties"));
                System.out.println("加载包内log4j配置文件");
            } catch (Exception e1) {
                System.out.println("log4j配置文件加载失败;");
                e1.printStackTrace();
            }
        }
    }

    private static int postThreadNum =1;

    static {
        System.out.println("test 1");
        String str=ConfigUtil.getProperty("thread.postprocess");
        System.out.println("test 2");
        if(str!=null)
            postThreadNum =Integer.valueOf(str.matches("\\d+?")?
                str:"1");
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

    static int downThreadNum=ConfigUtil.getProperty("thread.postprocess")==null?
            1:Integer.valueOf(ConfigUtil.getProperty("thread.postprocess"));
    DetailedSeqThreadsPool pool=null;

    private void start() {
        // 启动下载消息中心
        Thread centerThread=new Thread(new Runnable() {
            @Override
            public void run() {
                new DownloadTaskCenter().init();
            }
        });
//        postpool = Executors.newFixedThreadPool(postThreadNum);
        pool=new DetailedSeqThreadsPool(downThreadNum);
        for(int i=0;i<postThreadNum;i++)new postThread().start();
        centerThread.setPriority(3);
        centerThread.start();
        //下载文件
        while (true) {
            //请求下载任务
            if(requestFlag) {
                DownloadTask task = DownloadTaskCenter.getInstance().asignTask();
                if (task != null && task.getRemote() != null) {
                    logger.info("DownloadTask is alive,newest task is " + task.getRemote());
                    PoolFTPDownloader ftpDownloader = new PoolFTPDownloader(FtpConfig.getHOST(),
                            FtpConfig.getUsername(), FtpConfig.getPassword(), FtpConfig.getPORT(),
                            task);
                    pool.addAndStart(new Thread(ftpDownloader));
                }
            }
            if (pool.getQueueThreadSize() < downThreadNum) requestFlag = true ;
            else requestFlag=false;
            try {
                Thread.sleep(5000);
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

    class postThread extends Thread{

        @Override
        public void run() {
            while (true){
                //单线程
                GfsParseProcess process=DownloadTaskCenter.getInstance().asignPostProcessTask();
                if(process!=null)process.run();
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}