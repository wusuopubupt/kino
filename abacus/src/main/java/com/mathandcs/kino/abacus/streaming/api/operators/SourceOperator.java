package com.mathandcs.kino.abacus.streaming.api.operators;

import com.mathandcs.kino.abacus.streaming.api.collector.Output;
import com.mathandcs.kino.abacus.streaming.api.common.OperatorName;
import com.mathandcs.kino.abacus.streaming.api.functions.SourceFunction;
import com.mathandcs.kino.abacus.streaming.runtime.record.StreamRecord;
import parquet.Preconditions;

public class SourceOperator<OUT, F extends SourceFunction<OUT>> extends AbstractOperator<OUT, F> {

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
        } finally {
            // make sure that the context is closed in any case
            ctx.close();
        }
    }

    @Override
    public String getName() {
        return OperatorName.SOURCE.toString();
    }
}
