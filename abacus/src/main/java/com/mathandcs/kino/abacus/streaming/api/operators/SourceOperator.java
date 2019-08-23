package com.mathandcs.kino.abacus.streaming.api.operators;

import com.mathandcs.kino.abacus.streaming.api.collector.Output;
import com.mathandcs.kino.abacus.streaming.api.common.OperatorName;
import com.mathandcs.kino.abacus.streaming.api.functions.SourceFunction;
import com.mathandcs.kino.abacus.streaming.runtime.record.StreamRecord;
import com.mathandcs.kino.abacus.streaming.runtime.record.Watermark;
import lombok.Getter;
import lombok.Setter;
import parquet.Preconditions;

public class SourceOperator<OUT, F extends SourceFunction<OUT>> extends AbstractOperator<OUT, F> {

    @Getter
    @Setter
    private transient SourceFunction.SourceContext<OUT> ctx;

    private transient volatile boolean canceledOrStopped = false;

    public SourceOperator(F sourceFunction) {
        super(sourceFunction);

        this.chainingStrategy = ChainingStrategy.HEAD;
    }

    public void run(final Output<StreamRecord<OUT>> collector) throws Exception {
        Preconditions.checkNotNull(ctx, "Source Context must not be null.");
        try {
            userFunction.run(ctx);

            // if we get here, then the user function either exited after being done (finite source)
            // or the function was canceled or stopped. For the finite source case, we should emit
            // a final watermark that indicates that we reached the end of event-time
            if (!isCanceledOrStopped()) {
                ctx.emitWatermark(Watermark.MAX_WATERMARK);
            }
        } finally {
            // make sure that the context is closed in any case
            ctx.close();
        }
    }

    public void cancel() {
        // important: marking the source as stopped has to happen before the function is stopped.
        // the flag that tracks this status is volatile, so the memory model also guarantees
        // the happens-before relationship
        markCanceledOrStopped();
        userFunction.cancel();

        // the context may not be initialized if the source was never running.
        if (ctx != null) {
            ctx.close();
        }
    }

    protected void markCanceledOrStopped() {
        this.canceledOrStopped = true;
    }

    /**
     * Checks whether the source has been canceled or stopped.
     * @return True, if the source is canceled or stopped, false is not.
     */
    protected boolean isCanceledOrStopped() {
        return canceledOrStopped;
    }

    @Override
    public String getName() {
        return OperatorName.SOURCE.toString();
    }
}
