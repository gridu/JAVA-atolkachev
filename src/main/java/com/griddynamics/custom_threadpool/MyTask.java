package com.griddynamics.custom_threadpool;

public class MyTask implements Runnable {

    private int taskNum;

    public MyTask(int taskNum) {
        this.taskNum = taskNum;
    }

    @Override
    public void run() {
        try {
            System.out.println("Start executing of task number: " + taskNum);

            Thread.sleep(1000);

        } catch (InterruptedException e) {
            // skip
        }
        System.out.println("End executing of task number: " + taskNum);
    }
}
