package com.xinhong.gfs.processor;

import org.apache.log4j.Logger;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by shijunna on 2016/7/25.
 */
public class RedisListener implements Observer {
    private final Logger logger = Logger.getLogger(RedisListener.class);
    @Override
    public void update(Observable o, Object arg) {
        logger.error("RedisListener:"+arg);
        Redis run = new Redis(arg.toString());
        run.addObserver(this);
        new Thread(run).start();
        logger.info("RedisRunThread重启");
    }
}
