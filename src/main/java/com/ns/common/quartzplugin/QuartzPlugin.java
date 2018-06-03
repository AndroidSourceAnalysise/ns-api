package com.ns.common.quartzplugin;

import com.jfinal.plugin.IPlugin;
import org.apache.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
//import com.jyb.job.TestJob;
//import com.jyb.job.TestTestJob;

@SuppressWarnings("rawtypes")
public class QuartzPlugin implements IPlugin {

    private Logger logger = Logger.getLogger(QuartzPlugin.class);
    private static SchedulerFactory sf = new StdSchedulerFactory();
    private Class[] objects;
    private List<Scheduler> Schedulers=new ArrayList<Scheduler>();

    public QuartzPlugin(Class...objects) {
        this.objects=objects;
    }

    @SuppressWarnings({"unchecked" })
    public boolean start() {

        if(objects!=null){
            for (Class clazz : objects) {
                Scheduled scheduled=(Scheduled)clazz.getAnnotation(Scheduled.class);
                try {
                    if(scheduled==null){
                        logger.warn("JOB 没有注解。");
                        continue;
                    }
                    Scheduler sched = sf.getScheduler();
                    String jobClassName = clazz.getName();
                    String jobCronExp = scheduled.cron();
                    int fixedDelay =scheduled.fixedDelay();
                    boolean enable=scheduled.enable();

                    if (!enable) {
                        continue;
                    }

                    try {
                        clazz = Class.forName(jobClassName);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                    CronTrigger cronTrigger=null;
                    Trigger trigger=null;

                    JobDetail jobDetail = JobBuilder.newJob(clazz)
                            .withIdentity(jobClassName, jobClassName).build();

                    if(fixedDelay>0){

                        int second=fixedDelay/1000;
                        trigger=TriggerBuilder.newTrigger()
                                .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(second))
                                .startNow()
                                .build();
                        sched.scheduleJob(jobDetail, trigger);
                        sched.start();
                    }

                    if(jobCronExp!=null&&!"".equals(jobCronExp)){
                        cronTrigger=TriggerBuilder.newTrigger()
                                .withSchedule(CronScheduleBuilder.cronSchedule(jobCronExp))
                                .startNow()
                                .build();

                        sched.scheduleJob(jobDetail, cronTrigger);
                        sched.start();
                    }
                } catch (SchedulerException e) {
                    new RuntimeException(e);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }

        return true;
    }

    public boolean stop() {
        for (Scheduler scheduler : Schedulers) {
            try {
                scheduler.shutdown();
            } catch (SchedulerException e) {
                logger.error("shutdown error", e);
            }
        }
        return true;
    }

    //public static void main(String[] args) {
    //    QuartzPlugin plugin = new QuartzPlugin(TestJob.class,TestTestJob.class);
    //    plugin.start();
    //    System.out.println("执行成功！！！");
    //
    //}

}