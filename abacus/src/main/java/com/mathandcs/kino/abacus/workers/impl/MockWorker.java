package com.mathandcs.kino.abacus.workers.impl;

import com.google.common.base.Preconditions;
import com.mathandcs.kino.abacus.core.Task;
import com.mathandcs.kino.abacus.workers.Worker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by dashwang on 6/4/17.
 *
 * A mock worker
 */
public class MockWorker implements Worker {

    private static final String TASK_TYPE = "mock";
    private static final Logger LOGGER = LoggerFactory.getLogger(MockWorker.class);

    @Override
    public String getTaskType() {
        return TASK_TYPE;
    }

    @Override
    public void run(Task task) {
        LOGGER.info("MockWorker start to run task.");
        Preconditions.checkState(TASK_TYPE.equals(task.getType()));
        System.out.print("Hello, World!\n");
        LOGGER.info("MockWorker finished to run task");
    }
}
