package com.griddynamics.custom_threadpool;

import java.util.LinkedList;

public class CustomThreadPool {

    private final Object key = new Object();
    private final LinkedList<Runnable> tasks;
    private final LinkedList<Worker> threads;
    private int delayMills;
    private volatile boolean shutdown;

    public CustomThreadPool(int nThreads) {
        shutdown = false;
        tasks = new LinkedList<>();
        threads = new LinkedList<>();
        for (int i = 0; i < nThreads; i++) {
            Worker thread = new Worker();
            thread.setPool(this);
            threads.add(thread);
            thread.start();
        }
    }

    public LinkedList<Runnable> getTasks() {
        return tasks;
    }

    public LinkedList<Worker> getThreads() {
        return threads;
    }

    public Object getKey() {
        return key;
    }

    public int getDelayMills() {
        return delayMills;
    }

    public synchronized Runnable removeFromQueue() {
        return tasks.poll();
    }

    public synchronized void addToTasks(Runnable r) {
        tasks.addLast(r);
    }

    public void submit(Runnable r, int delayTimeMills) {
        this.delayMills = delayTimeMills;
        if (!shutdown) {
            addToTasks(r);
            synchronized (this.key) {
                key.notify();
            }
        } else {
            System.out.println("Pool shutdown executed");
        }
    }

    public void stop() {
        shutdown = true;
        for (Worker thread : threads) {
            thread.shutDown();
        }
        synchronized(this.key) {
            key.notifyAll();
        }
        for (Worker thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
