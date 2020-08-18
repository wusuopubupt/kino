package com.mathandcs.kino.abacus.runtime.processor;

import com.mathandcs.kino.abacus.api.record.StreamRecord;
import java.io.Serializable;

public interface Processor extends Serializable {

    /**
     * Process stream record.
     * @param record
     */
    void process(StreamRecord record);

}
