package com.mathandcs.kino.abacus.api.functions;

import java.io.Serializable;

public interface SinkFunction<IN> extends Function, Serializable {

    /**
     * Writes the given value to the sink. This function is called for every record.
     *
     * @param value The input record.
     *
     * @throws Exception
     */
    void sink(IN value);

}
