package com.mathandcs.kino.abacus.runtime.processor;

import com.mathandcs.kino.abacus.api.emitter.Emitter;
import com.mathandcs.kino.abacus.api.env.ExecutionEnvironment;
import com.mathandcs.kino.abacus.api.record.StreamRecord;
import java.io.Serializable;
import java.util.List;

public interface Processor extends Serializable {

    /**
     * Open processor.
     * @param env The execution environment
     * @param emitters record emitters
     */
    void open(ExecutionEnvironment env, List<Emitter> emitters) throws Exception;

    /**
     * Process stream record.
     * @param record
     */
    void process(StreamRecord record);

}
