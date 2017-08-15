package com.xinhong.gfs.download;

import com.alibaba.fastjson.JSONObject;
import com.xinhong.ftp.util.DownloadStatus;
import com.xinhong.util.ConfigUtil;

import java.io.*;
import java.util.*;

/**
 * Created by wingsby on 2017/8/14.
 */
public class DownloadTaskCenter {
    private static LinkedList<DownloadTask> normalTaskList = new LinkedList<>();
    private static LinkedList<DownloadTask> lazyTaskList = new LinkedList<>();
    // 分配出去的任务 0
    // 按重要等级分的任务// 一般以时间为准// 1 ,2 ,3
    private static Stack<DownloadTask> emgencyTaskList = new Stack<DownloadTask>();
    private static List<DownloadTask> recordTaskList = new ArrayList<>();

    private static Map<String, DownloadTask> downloadingMap = new HashMap();

    private int persistentGap = 5* 60*1000; //持久化时间间隔
    private static DownloadTaskCenter instance = new DownloadTaskCenter();

    public synchronized void persistence() {
        String perPath = ConfigUtil.getProperty("center.path");
        //emgencyTaskList
        String emFile = perPath + "/" + ConfigUtil.getProperty("center.emgency");
        String normalFile = perPath + "/" + ConfigUtil.getProperty("center.normal");
        String lazyFile = perPath + "/" + ConfigUtil.getProperty("center.lazy");
        String recFile = perPath + "/" + ConfigUtil.getProperty("center.record");
        String downFile = perPath + "/" + ConfigUtil.getProperty("center.downloading");
        persistenceEach(emFile, emgencyTaskList);
        persistenceEach(normalFile, normalTaskList);
        persistenceEach(lazyFile, lazyTaskList);
        persistenceEach(recFile, recordTaskList);
        try {
            BufferedWriter downWriter = new BufferedWriter(new FileWriter(downFile));
            if (downloadingMap.size() > 0) {
                for (String key : downloadingMap.keySet()) {
                    DownloadTask task = downloadingMap.get(key);
                    String str = task.getPersistentString();
                    downWriter.write(str);
                }
            }
            downWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void persistenceEach(String file, Collection<DownloadTask> collections) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            if (collections.size() > 0) {
                for (DownloadTask task : collections) {
                    String str = task.getPersistentString();
                    writer.write(str);
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //清除最近持久化数据
    public synchronized void clearPerstnece() {

    }

    public synchronized void loadPersistence() {
        // 加载完成时将其设为2线
        String perPath = ConfigUtil.getProperty("center.path");
        //emgencyTaskList
        String emFile = perPath + "/" + ConfigUtil.getProperty("center.emgency");
        String normalFile = perPath + "/" + ConfigUtil.getProperty("center.normal");
        String lazyFile = perPath + "/" + ConfigUtil.getProperty("center.lazy");
        String recFile = perPath + "/" + ConfigUtil.getProperty("center.record");
        String downFile = perPath + "/" + ConfigUtil.getProperty("center.downloading");
        loadPersistenceCommon(emFile, emgencyTaskList);
        loadPersistenceCommon(normalFile, normalTaskList);
        loadPersistenceCommon(lazyFile, lazyTaskList);
        loadPersistenceCommon(recFile, recordTaskList);
        loadPersistenceCommon(downFile, downloadingMap);
    }

    public static void notifyDownloadStatus(DownloadTask task,DownloadTask.DownInfo info){
         task.addInfo(info);
         if(downloadingMap.containsKey(task.getRemote())){
             downloadingMap.put(task.getRemote(),task);
        }
    }

    private void createFile(String file){
        File cfile=new File(file);
        if(cfile.exists()){
            return;
        }else{
            if(cfile.getParentFile().exists()){
                try {

                    if(cfile.isDirectory()){
                        cfile.mkdir();
                    }else{
                        cfile.createNewFile();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else createFile(cfile.getParent());
        }
    }


    private void loadPersistenceCommon(String file, Object collections) {
        File cfile=new File(file);
        if(!cfile.exists())createFile(file);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String str = null;
            StringBuilder stringBuilder = new StringBuilder();
            boolean flag = false;
            boolean endflag = false;
            while ((str = reader.readLine()) != null) {
                if (str.startsWith("@head@")) {
                    flag = true;
                }
                if (str.startsWith("@end@")) {
                    flag = true;
                    endflag = true;
                }
                if (flag) {
                    stringBuilder.append(str);
                    if (!str.endsWith("\n")) stringBuilder.append("\n");
                }
                if (endflag) {
                    DownloadTask task = new DownloadTask();
                    task.loadPersistentString(stringBuilder.toString());
                    stringBuilder = new StringBuilder();
                    endflag = false;
                    if (collections instanceof Map) {
                        ((Map) collections).put(task.getRemote(), task);
                    } else if (collections instanceof Collection)
                        ((Collection) collections).add(task);
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void updateTask() {
        List<DownloadTask> newTask = GFSFilenameUtil.getCurrent();
        Collections.sort(newTask);
        if (recordTaskList.size() == 0) {
            for (DownloadTask task : newTask) {
                normalTaskList.add(task);
                recordTaskList.add(task);
            }
        } else {
            for (DownloadTask task : newTask) {
                if (!recordTaskList.contains(task)) {
                    normalTaskList.add(task);
                    recordTaskList.add(task);
                }
            }
        }
    }


    //分配任务  有问题，下载程序如何通知center
    public static DownloadTask asignTask() {
        DownloadTask task = null;
        if (emgencyTaskList.size() > 0) task = emgencyTaskList.peek();
        else if (normalTaskList.size() > 0) {
            task = normalTaskList.peek();
        } else if (lazyTaskList.size() > 0) {
            task = lazyTaskList.peek();
        }
        if (task != null) {
            if(task.getDownInfos()!=null){
                task.getDownInfos().get(task.getDownInfos().size() - 1).
                        setDownstatus(DownloadStatus.Downloading);
            }else{
                DownloadTask.DownInfo info=task.new DownInfo();
            }

            downloadingMap.put(task.getRemote(), task);
        }
        return task;
    }

    public static void notifyDownloadStatus(JSONObject json) {
        String filename = (String) json.get("filename");
        if (downloadingMap.containsKey(filename)) {
            DownloadStatus status = (DownloadStatus) json.get("downloadstatus");
            switch (status) {
                case Remote_File_Noexist:
                    break;
                case Local_Bigger_Remote:
                    break;
                case Download_From_Break_Success:
                    downloadingMap.remove(filename);
                    break;
                case Download_From_Break_Failed:
                    break;
                case Download_New_Success:
                    downloadingMap.remove(filename);
                    break;
                case Download_New_Failed:
                    //再分配任务时将其移至特殊list
                    break;
            }
        } else {
            //
        }
    }


    public static DownloadTaskCenter getInstance() {
        return instance;
    }


    public void init() {
        // 加载持久化任务
        loadPersistence();
        // 持久化线程
        new Thread(new PersistentRunnable()).start();
        while (true) {
            updateTask();
            try {
                Thread.sleep(20 * 60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class PersistentRunnable implements Runnable {
        @Override
        public void run() {
            while (true) {
                persistence();
                try {
                    Thread.sleep(persistentGap);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
