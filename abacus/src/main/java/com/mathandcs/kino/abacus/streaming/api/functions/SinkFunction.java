package com.mathandcs.kino.abacus.streaming.api.functions;

import java.io.Serializable;

public interface SinkFunction<IN> extends Function, Serializable {

    /**
     * Writes the given value to the sink. This function is called for every record.
     *
     * @param value The input record.
     * @param context Additional context about the input record.
     *
     * @throws Exception
     */
    void invoke(IN value, Context context);

    interface Context<T> {

        long currentProcessingTime();

        long currentWatermark();

        /**
         * Returns the timestamp of the current input record or {@code null} if the element does not
         * have an assigned timestamp.
         */
        Long timestamp();
    }
}
