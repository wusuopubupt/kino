package com.mathandcs.kino.workeragent.client;

import com.mathandcs.kino.workeragent.core.Task;

/**
 * Created by wangdongxu on 6/4/17.
 */
public interface TaskManagerClient {
    Task pullTask();
}
