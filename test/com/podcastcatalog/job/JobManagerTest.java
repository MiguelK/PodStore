package com.podcastcatalog.job;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class JobManagerTest {

    @Test
    public void enable_state() throws InterruptedException {
        StateJob job = new StateJob();

        JobManager.getInstance().registerJob(job, 1, TimeUnit.MILLISECONDS);
        JobManager.getInstance().startAsync();
        Thread.sleep(100);

        Assert.assertTrue(job.getState() > 10);
    }


    class StateJob implements Job {
        int state;

        @Override
        public void doWork() {
            state++;
        }

        public int getState() {
            return state;
        }
    }
}


