package com.mathandcs.kino.abacus.api.operators;

import com.google.common.base.Preconditions;
import com.mathandcs.kino.abacus.api.emitter.StreamEmitter;
import com.mathandcs.kino.abacus.api.functions.SourceFunction;
import com.mathandcs.kino.abacus.api.common.OperatorName;
import com.mathandcs.kino.abacus.api.record.StreamRecord;

public class SourceOperator<OUT, F extends SourceFunction<OUT>> extends AbstractOperator<OUT, F> {

    private transient SourceFunction.SourceContext<OUT> ctx;

    private transient volatile boolean canceledOrStopped = false;

    public SourceOperator(F sourceFunction) {
        super(sourceFunction);
    }

    public void run(final StreamEmitter<StreamRecord<OUT>> collector) throws Exception {
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
