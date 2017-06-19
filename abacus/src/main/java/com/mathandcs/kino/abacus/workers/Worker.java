package com.mathandcs.kino.abacus.workers;

import com.mathandcs.kino.abacus.workflow.Task;

/**
 * Created by wangdongxu on 6/3/17.
 */
public interface Worker {

    // task type that worker can run
    String getTaskType();

    void run(Task task);
}
