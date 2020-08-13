package com.mathandcs.kino.abacus.runtime.processor;

import com.mathandcs.kino.abacus.api.env.ExecutionEnvironment;

public interface TaskProcessor {
    void setUp(ExecutionEnvironment executionEnvironment);
    boolean process() throws Exception;
}
