package com.mathandcs.kino.workeragent.worker;

import com.mathandcs.kino.workeragent.task.Task;

/**
 * Created by wangdongxu on 6/3/17.
 */
public interface Worker {

    // task type that worker can run
    String getTaskType();

    void run(Task task);
}
