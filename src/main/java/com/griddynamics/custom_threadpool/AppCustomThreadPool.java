package com.griddynamics.custom_threadpool;

import com.griddynamics.custom_threadpool.exception.CustomException;

public class AppCustomThreadPool {

    public static void main(String[] args) throws CustomException {
        displayInfo();
        int delay;
        try {
            delay = Integer.valueOf(args[0]);
            if (delay < 0) {
                delay = Math.abs(delay);
            }
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            throw new CustomException("\nArgument is empty! Please, set up an argument!", e.getCause());
        } catch (NumberFormatException ex) {
            throw new CustomException("\n'" + args[0] + "'" + " - is wrong argument! Argument should be a digit", ex.getCause());
        }
        CustomThreadPool pool2 = new CustomThreadPool(Runtime.getRuntime().availableProcessors());
        for (int i = 0; i < 15; i++) {
            pool2.submit(new MyTask(i), delay);
        }
        pool2.stop();
    }

    private static void displayInfo() {
        System.out.println("\ncom.griddynamics.AppCustomThreadPool <delayInMills>");
    }
}

