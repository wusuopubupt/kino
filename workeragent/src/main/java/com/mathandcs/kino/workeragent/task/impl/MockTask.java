package com.mathandcs.kino.workeragent.task.impl;

import com.mathandcs.kino.workeragent.task.Task;

/**
 * Created by wangdongxu on 6/4/17.
 */
public class MockTask implements Task {
    @Override
    public String getType() {
        return "mock";
    }
}
