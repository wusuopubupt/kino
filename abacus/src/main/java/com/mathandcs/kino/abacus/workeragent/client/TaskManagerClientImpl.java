package com.mathandcs.kino.abacus.workeragent.client;

import com.mathandcs.kino.workeragent.core.Task;
import org.springframework.stereotype.Component;

/**
 * Created by wangdongxu on 6/4/17.
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
