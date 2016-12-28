package com.podcastcatalog.service.job;

import java.util.logging.Logger;

public class MemoryDumperJob implements Job {

    private final static Logger LOG = Logger.getLogger(MemoryDumperJob.class.getName());

    @Override
    public void doWork() {

        int mb = 1024*1024;

        //Getting the runtime reference from system
        Runtime runtime = Runtime.getRuntime();

        LOG.info("##### Heap utilization statistics [MB] #####");

        //Print used memory
        LOG.info("Used Memory:"
                + (runtime.totalMemory() - runtime.freeMemory()) / mb + "MB");

        //Print free memory
        LOG.info("Free Memory:"
                + runtime.freeMemory() / mb + " MB");

        //Print total available memory
        LOG.info("Total Memory:" + runtime.totalMemory() / mb + "MB");

        //Print Maximum available memory
        LOG.info("Max Memory:" + runtime.maxMemory() / mb + "MB");

    }
}
