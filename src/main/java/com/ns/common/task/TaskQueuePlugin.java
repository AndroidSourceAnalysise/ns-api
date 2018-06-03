package com.ns.common.task;

import com.jfinal.log.Log;
import com.jfinal.plugin.IPlugin;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class TaskQueuePlugin implements Runnable,IPlugin {
    static Log logger = Log.getLog(TaskQueuePlugin.class);
    private static final HashMap<String, TaskQueuePlugin> map = new HashMap<String, TaskQueuePlugin>();
    private ExecutorService taskExecutor;
    private ArrayBlockingQueue<Task> taskQueue;

    public TaskQueuePlugin(String taskQueuePluginName, int taskQueueSize, int taskQueueThreads) {
        taskExecutor = Executors.newFixedThreadPool(taskQueueThreads);
        taskQueue = new ArrayBlockingQueue<Task>(taskQueueSize);
        map.put(taskQueuePluginName, this);
    }

    public static TaskQueuePlugin use(String taskQueuePluginName){
        return map.get(taskQueuePluginName);
    }
    @Override
    public void run() {
        while (true) {
            try {
                Task task = taskQueue.poll(2, TimeUnit.SECONDS);
                if (task != null){
                    task.execute();
                }
            }
            catch (InterruptedException ie){

            }
            catch (Exception e){
                logger.error(e.getLocalizedMessage());
            }
        }
    }

    public void runForever(){
        taskExecutor.execute(this);
    }

    public void putTask(Task task){
        try {
            taskQueue.put(task);
        }
        catch (InterruptedException e){
            logger.error(e.getLocalizedMessage());
        }
    }

    //public void runService(){
    //    TaskQueuePlugin taskService = new TaskQueuePlugin();
    //    taskService.runForever();
    //}

    //public static void main(String[] args) throws Exception{
    //    System.out.println("TaskQueuePlugin");
    //    TaskQueuePlugin taskService = new TaskQueuePlugin("test");
    //    taskService.runForever();
    //    taskService.putTask(new Task("a"));
    //    taskService.putTask(new Task("b"));
    //    taskService.putTask(new Task("c"));
    //    System.out.println("TaskQueuePlugin End.");
    //}

    @Override
    public boolean start() {
        runForever();
        return true;
    }

    @Override
    public boolean stop() {
        taskExecutor.shutdown();
        return true;
    }
}
