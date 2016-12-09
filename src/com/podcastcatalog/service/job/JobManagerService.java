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
    private static final int INITIAL_DELAY = 0;

    private final ScheduledExecutorService threadPool;

    private static final JobManagerService INSTANCE = new JobManagerService();

    private final List<RegisterdJob> registerdJobs;


    private JobManagerService() {
        threadPool = Executors.newScheduledThreadPool(5);
        registerdJobs = new ArrayList<>();
    }

    public static JobManagerService getInstance() {
        return INSTANCE;
    }

    public void registerJob(Job job, int period, TimeUnit timeUnit) {
        registerdJobs.add(new RegisterdJob(new WorkExecutor(job), period, timeUnit));
    }

    public void startAsync() {
        for (RegisterdJob registerdJob : registerdJobs) {
            threadPool.scheduleAtFixedRate(registerdJob.getWorkExecutor(), INITIAL_DELAY, registerdJob.getPeriod(), registerdJob.getTimeUnit());
        }
    }

    private class RegisterdJob {
        final WorkExecutor workExecutor;
        final int period;
        final TimeUnit timeUnit;

        RegisterdJob(WorkExecutor workExecutor, int period, TimeUnit timeUnit) {
            this.workExecutor = workExecutor;
            this.period = period;
            this.timeUnit = timeUnit;
        }

        WorkExecutor getWorkExecutor() {
            return workExecutor;
        }

        int getPeriod() {
            return period;
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
                LOG.log(Level.SEVERE, "Faile execute Job " + target.getClass().getSimpleName(), e);
            }
        }
    }
}
