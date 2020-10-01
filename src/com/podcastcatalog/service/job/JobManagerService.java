package com.podcastcatalog.service.job;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobManagerService {

    private final static Logger LOG = Logger.getLogger(JobManagerService.class.getName());

    private final ScheduledExecutorService threadPool;

    private static final JobManagerService INSTANCE = new JobManagerService();

    private final List<RegisterdJob> registerdJobs;

    private JobManagerService() {
        threadPool = Executors.newScheduledThreadPool(12);
        registerdJobs = new ArrayList<>();
    }

    public static JobManagerService getInstance() {
        return INSTANCE;
    }

    public void registerJob(Job job, int period, TimeUnit timeUnit) {
        registerdJobs.add(new RegisterdJob(new WorkExecutor(job),period, period, timeUnit));
    }

    public void registerJob(Job job,long initialDelay, int period, TimeUnit timeUnit) {
        registerdJobs.add(new RegisterdJob(new WorkExecutor(job),initialDelay, period, timeUnit));
    }

    public void executeOnce(Job job) {
        WorkExecutor workExecutor = new WorkExecutor(job);
        threadPool.submit(workExecutor);
    }

    public void startAsync() {
        for (RegisterdJob registerdJob : registerdJobs) {
            threadPool.scheduleAtFixedRate(registerdJob.getWorkExecutor(), registerdJob.getInitialDelay(), registerdJob.getPeriod(), registerdJob.getTimeUnit());
        }
    }

    private class RegisterdJob {
        final WorkExecutor workExecutor;
        final int period;
        final long initialDelay;
        final TimeUnit timeUnit;

        RegisterdJob(WorkExecutor workExecutor,long initialDelay, int period, TimeUnit timeUnit) {
            this.workExecutor = workExecutor;
            this.period = period;
            this.initialDelay = initialDelay;
            this.timeUnit = timeUnit;
        }

        WorkExecutor getWorkExecutor() {
            return workExecutor;
        }

        int getPeriod() {
            return period;
        }

        public long getInitialDelay() {
            return initialDelay;
        }

        TimeUnit getTimeUnit() {
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
                LOG.log(Level.SEVERE, "Failed execute Job " + target.getClass().getSimpleName(), e);
            }
        }

        @Override
        public String toString() {
            return "WorkExecutor{" +
                    "target=" + target.getClass().getSimpleName() +
                    '}';
        }
    }
}
