package com.mathandcs.kino.abacus.quartz;

import com.mathandcs.kino.abacus.quartz.job.SimpleJob;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * refer: https://www.cnblogs.com/drift-ice/p/3817269.html
 */
public class QuartzDemo {

    public static void main(String[] args) {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            Trigger trigger = newTrigger().withIdentity("trigger1", "group1")
                    .startNow()
                    .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(1)
                            .repeatForever())
                    .build();

            JobDetail job = newJob(SimpleJob.class)
                    .usingJobData("name", "dash' quartz job!")
                    .build();

            scheduler.scheduleJob(job, trigger);
            scheduler.start();
            Thread.sleep(100000);
            scheduler.shutdown(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
