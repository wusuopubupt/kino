package com.mathandcs.kino.abacus.streaming.api.functions.source;

import com.mathandcs.kino.abacus.streaming.api.functions.SourceFunction;

import java.io.IOException;
import java.util.Iterator;

/**
 * A stream source function that returns a sequence of elements.
 *
 * @param <T> The type of elements returned by this function.
 */
public class FromElementsFunction<T> implements SourceFunction<T> {

    private static final long serialVersionUID = 1L;

    private Iterable<T> elements;
    private Iterator<T> iterator;
    private volatile int numElementsEmitted;
    private volatile boolean isRunning = true;

    public FromElementsFunction(Iterable<T> elements) throws IOException {
        this.elements = elements;
        this.iterator = elements.iterator();
    }

    @Override
    public void run(SourceContext<T> ctx) throws Exception {
        while (isRunning && iterator.hasNext()) {
            T next = iterator.next();
            ctx.collect(next);
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }

}
