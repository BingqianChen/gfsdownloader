package com.xinhong.gfs.download;

import com.xinhong.ftp.util.DownloadStatus;
import com.xinhong.gfs.processor.GfsParseProcess;
import com.xinhong.util.ConfigUtil;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by wingsby on 2017/8/14.
 */
public class DownloadTaskCenter {
    private static BlockingQueue<DownloadTask> normalTaskList = new PriorityBlockingQueue<>();
    private static BlockingQueue<DownloadTask> lazyTaskList = new LinkedBlockingQueue<>();
    // 分配出去的任务 0
    // 按重要等级分的任务// 一般以时间为准// 1 ,2 ,3
    private static Stack<DownloadTask> emgencyTaskList = new Stack<DownloadTask>();
    private static BlockingQueue<DownloadTask> recordTaskList = new LinkedBlockingQueue<>();
    private static Map<String, DownloadTask> downloadingMap = new ConcurrentHashMap<>();
    private static BlockingQueue<DownloadTask> postList = new LinkedBlockingQueue<>();

    private int persistentGap = 5* 60*1000; //持久化时间间隔
    private static DownloadTaskCenter instance = new DownloadTaskCenter();

    static Logger logger=Logger.getLogger(DownloadTaskCenter.class);

    public synchronized void persistence() {
        String perPath = ConfigUtil.getProperty("center.path");
        //emgencyTaskList
        String emFile = perPath + "/" + ConfigUtil.getProperty("center.emgency");
        String normalFile = perPath + "/" + ConfigUtil.getProperty("center.normal");
        String lazyFile = perPath + "/" + ConfigUtil.getProperty("center.lazy");
        String recFile = perPath + "/" + ConfigUtil.getProperty("center.record");
        String downFile = perPath + "/" + ConfigUtil.getProperty("center.downloading");
        String postFile = perPath + "/" + ConfigUtil.getProperty("center.postprocess");
        persistenceEach(emFile, emgencyTaskList);
        persistenceEach(normalFile, normalTaskList);
        persistenceEach(lazyFile, lazyTaskList);
        persistenceEach(recFile, recordTaskList);
        persistenceEach(postFile, postList);
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
        String perPath = ConfigUtil.getProperty("center.path");
        //emgencyTaskList
        String emFile = perPath + "/" + ConfigUtil.getProperty("center.emgency");
        String normalFile = perPath + "/" + ConfigUtil.getProperty("center.normal");
        String lazyFile = perPath + "/" + ConfigUtil.getProperty("center.lazy");
        String recFile = perPath + "/" + ConfigUtil.getProperty("center.record");
        String downFile = perPath + "/" + ConfigUtil.getProperty("center.downloading");
        String postFile = perPath + "/" + ConfigUtil.getProperty("center.postprocess");
        loadPersistenceCommon(emFile, emgencyTaskList);
        loadPersistenceCommon(downFile, normalTaskList);
        loadPersistenceCommon(normalFile, normalTaskList);
        loadPersistenceCommon(lazyFile, lazyTaskList);
        loadPersistenceCommon(recFile, recordTaskList);
        loadPersistenceCommon(postFile, postList);
        System.out.println("persistent ok!");
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
        System.out.println(file);
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
                        if(task.getRemote()!=null&&task.getRemote().length()>0)
                        ((Map) collections).put(task.getRemote(), task);
                    } else if (collections instanceof Collection)
                        if(task.getRemote()!=null&&task.getRemote().length()>0)
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
        DownloadTask tmp=null;
        int presize=normalTaskList.size();
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
                    tmp=task;
                }
            }
        }
        logger.info("DownloadTaskCenter is still alive! now updatgtask size is "+normalTaskList.size()+
                ", and increase size is "+(normalTaskList.size()-presize)+",and update the newest task is "+
                (tmp==null?"none":tmp.getRemote()));
    }


    //分配任务  有问题，下载程序如何通知center
    public static DownloadTask asignTask() {
        DownloadTask task = null;
        if (emgencyTaskList.size() > 0) task = emgencyTaskList.pop();
        else if (normalTaskList.size() > 0) {
            task = normalTaskList.poll();
        } else if (lazyTaskList.size() > 0) {
            task = lazyTaskList.poll();
        }
        if (task != null) {
            if(task.getDownInfos()!=null&&task.getDownInfos().size()>0){
                task.getDownInfos().get(task.getDownInfos().size() - 1).
                        setDownstatus(DownloadStatus.Downloading);
            }else{
                DownloadTask.DownInfo info=task.new DownInfo();
            }
            downloadingMap.put(task.getRemote(), task);
        }
        return task;
    }


    public static void notifyDownloadStatus(DownloadTask task){
        if(downloadingMap.containsKey(task.getRemote())){
            downloadingMap.put(task.getRemote(),task);
        }
    }

    public static void notifyDownloadStatus(DownloadTask task,TaskOperation op) {
        String filename=task.getRemote();
        if(op!=null){
            if(downloadingMap.containsKey(task.getRemote()))
                downloadingMap.remove(task.getRemote());
            switch (op){
                case InsertEmegency:emgencyTaskList.push(task);break;
                case InsertLazy:lazyTaskList.offer(task);break;
                case RemoveDowning:
                    postList.add(task);
                    break;
                case Delete:
                    postList.remove(task);
                    break;
                default:;
            }
        }
    }


    public static DownloadTaskCenter getInstance() {
        return instance;
    }


    public void init() {
        // 加载持久化任务
        loadPersistence();
        while (true) {
            updateTask();
            persistence();
            try {
                Thread.sleep(persistentGap);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static GfsParseProcess asignPostProcessTask() {
        //从下载文件夹搜索文件
        if(postList.size()==0)return null;
        DownloadTask task=postList.poll();
        if(task.getLocal()==null)return null;
        String fname=task.getLocal();
        if(fname.endsWith("idx"))return null;
        String fnameidx=fname.trim()+".idx";
        if(new File(fname).exists()&&new File(fnameidx).exists())
        return new GfsParseProcess(task);
        else{
            postList.offer(task);
            return null;
        }
    }
}
