package com.mathandcs.kino.abacus.api.operators;

import com.google.common.base.Preconditions;
import com.mathandcs.kino.abacus.api.emitter.Emitter;
import com.mathandcs.kino.abacus.api.env.ExecutionEnvironment;
import com.mathandcs.kino.abacus.api.functions.SourceFunction;
import com.mathandcs.kino.abacus.api.common.OperatorName;
import com.mathandcs.kino.abacus.api.record.StreamRecord;
import java.util.List;

public class SourceOperator<OUT, F extends SourceFunction<OUT>> extends AbstractOperator<OUT, F> {

    private transient SourceFunction.SourceContext<OUT> ctx;

    public SourceOperator(F sourceFunction) {
        super(sourceFunction);
    }

    public void run() throws Exception {
        Preconditions.checkNotNull(ctx, "Source Context must not be null.");
        try {
            userFunction.run(ctx);
        } finally {
            // make sure that the context is closed in any case
            ctx.close();
        }
    }

    @Override
    public void open(ExecutionEnvironment env, List<Emitter> emitters) throws Exception {
        super.open(env, emitters);
        this.ctx = new DefaultSourceContext(emitters);
    }

    @Override
    public String getName() {
        return OperatorName.SOURCE.toString();
    }

    /**
     * The default source context implementation.
     */
    class DefaultSourceContext implements SourceFunction.SourceContext<OUT> {

        private List<Emitter> emitters;

        public DefaultSourceContext(List<Emitter> emitters) {
            this.emitters = emitters;
        }

        @Override
        public void emit(OUT element) {
            for (Emitter emitter : emitters) {
                emitter.emit(new StreamRecord(element));
            }
        }

        @Override
        public void close() {

        }
    }
}
