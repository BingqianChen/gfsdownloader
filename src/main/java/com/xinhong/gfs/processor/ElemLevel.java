package com.xinhong.gfs.processor;

import com.xinhong.gfs.download.GFSStarter;
import org.apache.log4j.Logger;
import test.ManualGrib2binary;

import java.io.*;
import java.util.HashMap;

/**
 * Created by shijunna on 2016/7/19.
 */
public class ElemLevel {
    static Logger logger= Logger.getLogger(ElemLevel.class);

    static HashMap<String, String> elemPatchMap;

    public static HashMap<String,String> getElemMap(){
        // read elem properties
        if(elemPatchMap!=null)return elemPatchMap;
        elemPatchMap = new HashMap<>();
        BufferedReader reader=null;
        String path=ElemLevel.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String rootpath=new File(path).getParent();
        try{
            reader=new BufferedReader(new FileReader(rootpath
                    +"/gfsConf/jsybElem.dat"));
            System.out.println("加载包外JSYB元素配置文件");
        }catch (Exception e){
            ClassLoader classLoader = ElemLevel.class.getClassLoader();
            InputStream stream=classLoader.getResourceAsStream("gfsConf/jsybElem.dat");
            reader=new BufferedReader(new InputStreamReader(
                    classLoader.getResourceAsStream("gfsConf/jsybElem.dat")));
            System.out.println("加载包外JSYB元素配置文件");
        }
        try {
            String str=null;
            while((str=reader.readLine())!=null){
                if(str.startsWith("#"))continue;
                String[] strs=str.split("@");
                if(strs.length>1) {
                    elemPatchMap.put(strs[0], strs[1]);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error("JSYB元素配置文件加载错误");
        } catch (IOException e) {
            e.printStackTrace();

        }
        return elemPatchMap;
    }


}
