package com.xinhong.util;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

public class FileHandler {
    public FileHandler() {

    }

    public static final String download = "download";
    public static final String start = " start time is";
    public static final String downloading = "downloading";
    public static long bTimenum;
    public static long eTimenum;
    public static long UesedTimenum;
    private static final Logger logger = Logger.getLogger(FileHandler.class);

    public static List<String> getFilenameList(String txtPath) {
        File file = new File(txtPath);
        if (!file.exists())
            return null;

        try {
            List<String> fileList = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String s;
            while ((s = reader.readLine()) != null) {
                fileList.add(s.trim());
            }
            reader.close();
            return fileList;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.info("获取" + txtPath + "的下载列表失败，文件不存在");
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("获取" + txtPath + "的下载列表失败，IO操作失败");
        }
        return null;
    }

    public synchronized static boolean writeFilename(String filename, String txtPath) {
        File file = new File(txtPath);
        if (!file.exists()) {
            createPathifNotExists(txtPath);
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(txtPath), true));//true表示追加到末尾
            writer.append(filename + "\r\n");
            writer.flush();//使用Buffered***时一定要先清缓冲区再关闭流
            writer.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.info("写入" + txtPath + "的下载列表失败，文件不存在，写入值：" + filename);

        } catch (IOException e) {
            e.printStackTrace();
            logger.info("写入" + txtPath + "的下载列表失败，IO操作失败，写入值：" + filename);
        }
        return false;
    }


    public static Map<String, String> getFilenameMap(String txtPath) {
        logger.info(txtPath + "获取日期下载实际情况");
        File file = new File(txtPath);
        if (!file.exists())
            return null;
        try {
            Map<String, String> filemap = new HashMap<>();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String s;
            String[] ary = null;
            while ((s = reader.readLine()) != null) {
                if (s.trim().length() < 8)
                    continue;
                ary = s.trim().split(",");
                filemap.put(ary[0], ary[1]);
            }
            reader.close();
            return filemap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.info("获取" + txtPath + "的下载列表失败，文件不存在");
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("获取" + txtPath + "的下载列表失败，IO操作失败");
        }
        return null;
    }

    public synchronized static boolean writeDownloading(String filename, String txtPath) {
        DateUtil dateUtil = new DateUtil();
        String bTime = dateUtil.getSystemTime();
        bTimenum = System.currentTimeMillis();
        logger.info(txtPath + "写入日期" + filename + "状态为downloading" + "开始时间是：" + bTime);
        File file = new File(txtPath);
        if (!file.exists()) {
            createPathifNotExists(txtPath);
        }
        Map<String, String> filenameMap = getFilenameMap(txtPath);
        if (filenameMap != null && filenameMap.containsKey(filename)
                && filenameMap.get(filename).equals(download)) {
            return true;
        } else {
            if (filenameMap == null)
                filenameMap = new HashMap<>();
            filenameMap.put(filename, downloading + ":" + bTime);
            return map2File(filenameMap, txtPath);
        }
    }

    public synchronized static boolean startLog(String filename, String txtPath) {
        DateUtil dateUtil = new DateUtil();
        String bTime = dateUtil.getSystemTime();
        logger.info(filename + "程序启动时间：" + bTime);
        System.out.println(filename + "程序启动时间：" + bTime);
        File file = new File(txtPath);
        if (!file.exists()) {
            createPathifNotExists(txtPath);
        }
        Map<String, String> filenameMap = getFilenameMap(txtPath);
        if (filenameMap != null && filenameMap.containsKey(filename)) {
            return true;
        } else {
            if (filenameMap == null)
                filenameMap = new HashMap<>();
            filenameMap.put(filename, start + ":" + bTime);
            return map2File(filenameMap, txtPath);
        }
    }

    public synchronized static boolean writeDownload(String filename, String txtPath) {
        DateUtil dateUtil = new DateUtil();
        String eTime = dateUtil.getSystemTime();
        eTimenum = System.currentTimeMillis();
        UesedTimenum = (eTimenum - bTimenum) / 1000;
        logger.info(txtPath + "写入日期" + filename + "状态为download" + "结束时间是：" + eTime);
        //logger.info(txtPath + "写入日期" + filename + "状态为success" + "下载耗时：" + (eTimenum-bTimenum)/1000);
        File file = new File(txtPath);
        if (!file.exists()) {
            createPathifNotExists(txtPath);
        }
        Map<String, String> filenameMap = getFilenameMap(txtPath);
        if (filenameMap == null)
            filenameMap = new HashMap<>();
        filenameMap.put(filename, download + ":" + eTime + "耗时:" + UesedTimenum + "秒");

        return map2File(filenameMap, txtPath);
    }

    public synchronized static boolean map2File(Map<String, String> map, String txtPath) {
        File file = new File(txtPath);
        if (!file.exists()) {
            createPathifNotExists(txtPath);
        }
        if (map == null || map.size() < 1) {
            file.delete();
            return true;
        }
        if (!file.exists()) {
            createPathifNotExists(txtPath);
        }
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file, false));//
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                writer.write(key + "," + map.get(key) + "\r\n");
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.info("写入" + txtPath + "的下载列表失败，文件不存在，写入值：" + txtPath);
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("写入" + txtPath + "的下载列表失败，IO操作失败，写入值：" + txtPath);
        } finally {
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 建立本地文件所属目录
     *
     * @param local_file
     */
    public static void createPathifNotExists(String local_file) {
        String fs = new File(local_file).getParent();
        File f = new File(fs);
        if (!f.exists()) {
            //System.out.println("创建目录:" + f.getAbsolutePath());
            f.mkdirs();
        }

    }

    /**
     * 追加文件：使用FileWriter
     *
     * @param filePath 文件路径
     * @param content  追加内容
     */
    public static void appendContent(String filePath, String content) {
        try {
            createPathifNotExists(filePath);
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(filePath, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
//            e.printStackTrace();
            logger.error("文件追加信息出错，请查看，file = " + filePath + ", content = " + content);
        }
    }

    /**
     * 删除该目录下的目录，保留文件
     *
     * @param path 目录
     */
    public static void deleteSubDir(String path) {
        if (path == null || path.trim().length() < 1)
            return;
        File file = new File(path);
        if (!file.exists())
            return;
        File[] files = file.listFiles();
        if (files == null || files.length < 1)
            return;
        for (File file1 : files) {
            if (file1.isDirectory()) {
                deleteDir(file1);
            } else {
                continue;
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

    public static boolean deleteDir(String path) {
        if (path == null || path.trim().length() < 1)
            return false;
        File file = new File(path);
        if (!file.exists())
            return false;
        return deleteDir(new File(path));
    }
}
