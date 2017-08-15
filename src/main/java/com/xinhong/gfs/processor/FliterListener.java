package com.xinhong.gfs.processor;

import org.apache.log4j.Logger;

import java.util.Observable;
import java.util.Observer;

public class FliterListener implements Observer{
    private final Logger logger = Logger.getLogger(FliterListener.class);
    @Override
    public void update(Observable o, Object arg) {
        logger.error("FliterRunThread死机:"+arg);
        GfsParseProcess run = new GfsParseProcess(arg.toString());
//        run.addObserver(this);
        new Thread(run).start();
        logger.info("FliterRunThread重启");
    }
}