package com.mathandcs.kino.abacus.workeragent;

import com.google.common.collect.Sets;
import com.google.common.util.concurrent.*;
import com.mathandcs.kino.abacus.core.Task;
import com.mathandcs.kino.abacus.workeragent.client.TaskManagerClient;
import com.mathandcs.kino.abacus.workeragent.client.TaskManagerClientImpl;
import com.mathandcs.kino.abacus.workers.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Set;
import java.util.concurrent.Executors;

/**
 * Created by dashwang on 2017-06-03
 */
@Component("WorkerAgent")
public class WorkerAgent implements CommandLineRunner {

    @Autowired
    private WorkerRegister workerRegister = new WorkerRegister();
    @Autowired
    private TaskManagerClient taskManagerClient = new TaskManagerClientImpl();
    @Value("${server.port}")
    private int workerAgentPort;

    @PostConstruct
    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LOGGER.info("Worker agent shutting down, calling shutdown hook!");
                // DO SOMETHING HERE
            }
        });
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerAgent.class);
    private static final String REQUEST_ID = "requestId";
    private Set<Task> unfinishedTasks = Sets.newConcurrentHashSet();
    private ListeningExecutorService workers = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

    @Override
    public void run(String... args) throws Exception {
        try {
            LOGGER.info("Worker agent starting");
            workerRegister.registerWorkers();
            loopOverTasks();
        } catch (Throwable e) {
            LOGGER.error("Exception encountered for worker agent", e);
            System.exit(1);
        }
    }

    private void loopOverTasks() {
        while (true) {
            Task task = null;
            try {
                task = pullTask();
                if (task != null) {
                    LOGGER.info("New task pulled: " + task.toString());
                    //mark task as dispatched
                    unfinishedTasks.add(task);
                    runTask(task);
                    Thread.sleep(100);
                }
                Thread.sleep(1000);
            } catch (Throwable e) {
                LOGGER.error("Failed to execute job", e);
            }
        }
    }

    private Task pullTask() {
        // TODO
        return taskManagerClient.pullTask();
    }

    private void runTask(final Task task) throws Exception {
        final Worker finalWorker = workerRegister.getWorkerTypeTaskType(task.getType());
        ListenableFuture future = workers.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    finalWorker.run(task);
                    LOGGER.info("Worker finished to run task!");
                    Thread.sleep(2000);
                } catch (Throwable e) {
                    LOGGER.error("Failed to run node", e);
                } finally {

                }
            }
        });

        Futures.addCallback(future, new FutureCallback() {
            @Override
            public void onSuccess(Object result) {
                //remove task from dispatched set
                unfinishedTasks.remove(task);
            }

            @Override
            public void onFailure(Throwable throwable) {
                //remove task from dispatched set
                unfinishedTasks.remove(task);
            }
        });
    }

}
