package com.podcastcatalog.service.job;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class JobManagerServiceTest {

    @Test
    public void enable_state() throws InterruptedException {
        StateJob job = new StateJob();

        JobManagerService.getInstance().registerJob(job, 1, TimeUnit.MILLISECONDS);
        JobManagerService.getInstance().startAsync();
        Thread.sleep(300);

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


