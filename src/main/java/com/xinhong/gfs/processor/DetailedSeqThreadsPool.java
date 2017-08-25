package com.xinhong.gfs.processor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by wingsby on 2017/8/23.
 */
public class DetailedSeqThreadsPool {
    BlockingQueue<Thread> queue = new LinkedBlockingQueue<Thread>();
    BlockingQueue<Thread> exequeue = new LinkedBlockingQueue<Thread>();
    int maxThreadNum = 1;
    int capcity=10;
    private boolean shutdowflag = false;


    public DetailedSeqThreadsPool(int threadnum) {
        maxThreadNum = threadnum;
        start();
    }

    public void addAndStart(Thread thread) {
        queue.add(thread);
    }

    int executingThreads = 0;


    //分配线程至执行线程队列
    private synchronized void notifyAssign() {
        if (!queue.isEmpty()) {
            Thread t = queue.poll();
            exequeue.add(t);
            t.start();
        }
    }

    public int getQueueThreadSize() {
        return queue.size();
    }

    public int getExecutingThreadSize() {
        return exequeue.size();
    }

    public void shutDownPool(DetailedSeqThreadsPool pool) {
        shutdowflag = true;
        pool = null;
    }

    public void shutDownPool() {
        shutdowflag = true;
    }


    private void start() {
        new Thread(new Runnable() {
            public void run() {
                while (!shutdowflag) { //停止
                    while (!queue.isEmpty()) { // 任务
                        while (exequeue.size() < maxThreadNum) {
                            notifyAssign();
                        }
                        if(exequeue.size()>0) {
                            for (Thread t : exequeue) {
                                if (t.isAlive()) {
                                } else {
                                    exequeue.remove(t);
//                                    notifyAssign();
                                }
                            }
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                }
            }
        }).start();
    }
}



