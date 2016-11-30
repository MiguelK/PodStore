package com.podcastcatalog.job;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobManager {

    private final static Logger LOG = Logger.getLogger(JobManager.class.getName());

    private final ScheduledExecutorService threadPool;

    private static final JobManager INSTANCE = new JobManager();

    private final List<RegisterdJob> registerdJobs;


    private JobManager() {
        threadPool = Executors.newScheduledThreadPool(5);
        registerdJobs = new ArrayList<>();
    }

    public static JobManager getInstance() {
        return INSTANCE;
    }


    public void registerJob(Job job, int period, TimeUnit timeUnit) {
        RegisterdJob registerdJob = new RegisterdJob(new WorkExecutor(job), period, timeUnit);
        registerdJobs.add(registerdJob);
    }

    public void startAsync() {
        for (RegisterdJob registerdJob : registerdJobs) {
            threadPool.scheduleAtFixedRate(registerdJob.getWorkExecutor(), 0, registerdJob.getPeriod(), registerdJob.getTimeUnit());
        }
    }

    private class RegisterdJob {
        final WorkExecutor workExecutor;
        final int period;
        final TimeUnit timeUnit;

        public RegisterdJob(WorkExecutor workExecutor, int period, TimeUnit timeUnit) {
            this.workExecutor = workExecutor;
            this.period = period;
            this.timeUnit = timeUnit;
        }

        public WorkExecutor getWorkExecutor() {
            return workExecutor;
        }

        public int getPeriod() {
            return period;
        }

        public TimeUnit getTimeUnit() {
            return timeUnit;
        }
    }

    private class WorkExecutor implements Runnable {
        private final Job target;

        public WorkExecutor(Job target) {
            this.target = target;
        }

        @Override
        public void run() {

            try {
                this.target.doWork();
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Faile execute Job " + target.getClass().getSimpleName(), e);
            }
        }
    }
}
