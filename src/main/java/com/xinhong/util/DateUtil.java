package com.xinhong.util;

import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shijunna on 2016/8/10.
 */
public class DateUtil {
    private static final Logger logger = Logger.getLogger(DateUtil.class);

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(ConfigCommon.DATE_DEFAULT_FORMAT_YMDH);
        long time = System.currentTimeMillis();
        Date d = new Date(time);
        return sdf.format(d);
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getSystemTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss");
        long time = System.currentTimeMillis();
        Date d = new Date(time);
        return sdf.format(d);
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getCurrentDate_YMDHMS() {
        SimpleDateFormat sdf = new SimpleDateFormat(ConfigCommon.DATE_DEFAULT_FORMAT_YMDHMS);
        long time = System.currentTimeMillis();
        Date d = new Date(time);
        return sdf.format(d);
    }

    /**
     * @param date    当前时间
     * @param addhour 当前时间需要处理的时间
     * @return
     */
    public static String dateAddHour(String date, int addhour) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(ConfigCommon.DATE_DEFAULT_FORMAT_YMDH);
            long time = sdf.parse(date).getTime();
            time = time + addhour * 60 * 60 * 1000;
            Date d = new Date(time);
            return sdf.format(d);
        } catch (ParseException e) {
            logger.info("所输入的日期错误,:" + date);
            return null;
        }
    }

    /**
     * @param date    当前时间（天）
     * @param addhour 当前时间需要处理的天数
     * @return
     */
    public static String dateAddDay(String date, int addhour) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(ConfigCommon.DATE_DEFAULT_FORMAT_YMD);
            long time = sdf.parse(date).getTime();
            time = time + addhour * 60 * 60 * 1000 * 24;
            Date d = new Date(time);
            return sdf.format(d);
        } catch (ParseException e) {
            logger.info("所输入的日期错误,:" + date);
            return null;
        }
    }
    /**
     * @param date    当前时间（天）
     * @param addhour 当前时间需要处理的天数
     * @return
     */
    public static String dateAddDATA(String date, int addhour) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(ConfigCommon.DATE_DEFAULT_FORMAT_DATA);
            long time = sdf.parse(date).getTime();
            time = time + addhour * 60 * 60 * 1000 * 24;
            Date d = new Date(time);
            return sdf.format(d);
        } catch (ParseException e) {
            logger.info("所输入的日期错误,:" + date);
            return null;
        }
    }
}
