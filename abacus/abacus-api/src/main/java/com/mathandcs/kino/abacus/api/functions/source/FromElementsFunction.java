package com.mathandcs.kino.abacus.api.functions.source;

import com.mathandcs.kino.abacus.api.functions.SourceFunction;

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

    public FromElementsFunction(Iterable<T> elements){
        this.elements = elements;
        this.iterator = elements.iterator();
    }

    @Override
    public void run(SourceContext<T> ctx) {
        while (isRunning && iterator.hasNext()) {
            T next = iterator.next();
            ctx.emit(next);
        }
    }

    @Override
    public void cancel() {
        isRunning = false;
    }

}
