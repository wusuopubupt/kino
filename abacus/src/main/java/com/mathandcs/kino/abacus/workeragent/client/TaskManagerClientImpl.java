package com.mathandcs.kino.abacus.workeragent.client;

import com.mathandcs.kino.abacus.workflow.Task;
import org.springframework.stereotype.Component;

/**
 * Created by dashwang on 6/4/17.
 */
@Component
public class TaskManagerClientImpl implements TaskManagerClient {

    // mock method
    @Override
    public Task pullTask() {
        Task task = new Task();
        task.setType("mock");
        return task;
    }
}
