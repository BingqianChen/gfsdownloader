package com.xinhong.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Properties;


/**
 * Description: <br>
 *
 * @author 作者 <a href=ds.lht@163.com>stone</a>
 * @version 创建时间：2016/6/2.
 */
public  class ConfigUtil {

    private static Properties proper = new Properties();

    static {
        try {
            proper.load(new FileInputStream(new File("gfsConf/config-common.properties")));
            proper.load(new FileInputStream(new File("gfsConf/config-gfs-ftp.properties")));
            proper.load(new FileInputStream(new File("gfsConf/config-cfs-ftp.properties")));
            proper.load(new FileInputStream(new File("gfsConf/config-cfs1-ftp.properties")));
            proper.load(new FileInputStream(new File("gfsConf/config-rtofs-ftp.properties")));
            proper.load(new FileInputStream(new File("gfsConf/config-gtspp-ftp.properties")));
            proper.load(new FileInputStream(new File("gfsConf/config-wari8old-ftp.properties")));
            proper.load(new FileInputStream(new File("gfsConf/config-hy-ftp.properties")));
            proper.load(new FileInputStream(new File("gfsConf/config-ocean1-ftp.properties")));
            proper.load(new FileInputStream(new File("gfsConf/config-haidiao-ftp.properties")));
            proper.load(new FileInputStream(new File("gfsConf/ha.properties")));

            System.out.println("配置文件加载失败，proper.load(new FileInputStream(new File(gfsConf/config-common.properties)))");
            System.out.println("采用ClassLoader加载");
        } catch (IOException e) {
            try {
                ClassLoader classLoader = ConfigUtil.class.getClassLoader();
                proper.load(classLoader.getResourceAsStream("gfsConf/config-common.properties"));
                proper.load(classLoader.getResourceAsStream("gfsConf/config-gfs-ftp.properties"));
                proper.load(classLoader.getResourceAsStream("gfsConf/config-cfs-ftp.properties"));
                proper.load(classLoader.getResourceAsStream("gfsConf/config-cfs1-ftp.properties"));
                //proper.load(classLoader.getResourceAsStream("gfsConf/config-rtofs-ftp.properties"));
                proper.load(classLoader.getResourceAsStream("gfsConf/config-gtspp-ftp.properties"));
                proper.load(classLoader.getResourceAsStream("gfsConf/config-wari8-ftp.properties"));
                proper.load(classLoader.getResourceAsStream("gfsConf/config-ocean1-ftp.properties"));
                proper.load(classLoader.getResourceAsStream("gfsConf/config-haidiao-ftp.properties"));
                proper.load(classLoader.getResourceAsStream("gfsConf/ha.properties"));
                proper.load(classLoader.getResourceAsStream("gfsConf/config-hy-ftp.properties"));
            } catch (IOException e1) {
                System.out.println("ClassLoader加载失败，ClassLoader classLoader = ConfigUtil.class.getClassLoader();\n" +
                        "                proper.load(classLoader.getResourceAsStream(gfsConf/config-common.properties))");
                e1.printStackTrace();
            }
        }



    }

    private ConfigUtil(){

    }


    /**
     * 取出Property，但以System的Property优先,取不到返回空字符串.
     */
    private static String getValue(String key) {
        String systemProperty = System.getProperty(key);
        if (systemProperty != null) {
            return systemProperty;
        }
        if (proper.containsKey(key)) {
            return proper.getProperty(key);
        }
        return "";
    }

    /**
     * 取出String类型的Property，但以System的Property优先,如果都为Null则抛出异常.
     */
    public static String getProperty(String key) {
        String value = getValue(key);
        if (value == null) {
            throw new NoSuchElementException();
        }
        return value;
    }

    /**
     * 取出String类型的Property，但以System的Property优先.如果都为Null则返回Default值.
     */
    public static String getProperty(String key, String defaultValue) {
        String value = getValue(key);
        return value != null ? value : defaultValue;
    }

    /**
     * 取出Integer类型的Property，但以System的Property优先.如果都为Null或内容错误则抛出异常.
     */
    public static Integer getInteger(String key) {
        String value = getValue(key);
        if (value == null) {
            throw new NoSuchElementException();
        }
        return Integer.valueOf(value);
    }

    /**
     * 取出Integer类型的Property，但以System的Property优先.如果都为Null则返回Default值，如果内容错误则抛出异常
     */
    public static Integer getInteger(String key, Integer defaultValue) {
        String value = getValue(key);
        return value != null ? Integer.valueOf(value) : defaultValue;
    }

    /**
     * 取出Double类型的Property，但以System的Property优先.如果都为Null或内容错误则抛出异常.
     */
    public static Double getDouble(String key) {
        String value = getValue(key);
        if (value == null) {
            throw new NoSuchElementException();
        }
        return Double.valueOf(value);
    }

    /**
     * 取出Double类型的Property，但以System的Property优先.如果都为Null则返回Default值，如果内容错误则抛出异常
     */
    public static Double getDouble(String key, Integer defaultValue) {
        String value = getValue(key);
        return value != null ? Double.valueOf(value) : defaultValue;
    }

    /**
     * 取出Boolean类型的Property，但以System的Property优先.如果都为Null抛出异常,如果内容不是true/false则返回false.
     */
    public static Boolean getBoolean(String key) {
        String value = getValue(key);
        if (value == null) {
            throw new NoSuchElementException();
        }
        return Boolean.valueOf(value);
    }

    /**
     * 取出Boolean类型的Property，但以System的Property优先.如果都为Null则返回Default值,如果内容不为true/false则返回false.
     */
    public static Boolean getBoolean(String key, boolean defaultValue) {
        String value = getValue(key);
        return value != null ? Boolean.valueOf(value) : defaultValue;
    }
}
