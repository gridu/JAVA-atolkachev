package com.griddynamics.external_sorting.util;

public final class TimeMetric {

    private String name;
    private Long start;

    public TimeMetric(String name) {
        this.name = name;
        this.start = System.currentTimeMillis();
    }

    public void print() {
        Long end = System.currentTimeMillis();
        System.out.println(name + " took " + (end - start) / 1000f + " s.");
    }
}

