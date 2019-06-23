package com.griddynamics.custom_threadpool;

public class Worker extends Thread {

    private CustomThreadPool pool;
    private boolean isActive = true;

    public void setPool(CustomThreadPool pool) {
        this.pool = pool;
    }

    public boolean isActive() {
        return isActive;
    }

    public void run() {
        Runnable task;
        while (true) {
            task = pool.removeFromQueue();
            if (task != null) {
                try {
                    try {
                        System.out.println("Delay the task for the " + Thread.currentThread().getName());
                        Thread.sleep(pool.getDelayMills());
                        System.out.println("Delay is over for the " + Thread.currentThread().getName());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Run the task by : " + Thread.currentThread().getName());
                    task.run();
                } catch (Exception e) {
                    e.getStackTrace();
                }
            } else {
                if (!isActive()) {
                    break;
                } else {
                    synchronized (pool.getKey()) {
                        try {
                            pool.getKey().wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public void shutDown() {
        isActive = false;
    }
}
