package com.mathandcs.kino.abacus.streaming.runtime.processor;

import com.mathandcs.kino.abacus.streaming.api.environment.ExecutionEnvironment;

public interface TaskProcessor {
    void setUp(ExecutionEnvironment executionEnvironment);
    boolean process() throws Exception;
}
