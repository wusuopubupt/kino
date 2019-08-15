package com.mathandcs.kino.abacus.streaming.api.functions.sink;

import com.mathandcs.kino.abacus.streaming.api.functions.AbstractRichFunction;
import com.mathandcs.kino.abacus.streaming.api.functions.SinkFunction;

public abstract class RichSinkFunction<IN> extends AbstractRichFunction implements SinkFunction<IN> {

    private static final long serialVersionUID = 1L;
}
