package com.mathandcs.kino.abacus.streaming.api.functions.sink;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class PrintSinkFunction<IN> extends RichSinkFunction<IN> {

    private static final Logger LOG = LoggerFactory.getLogger(PrintSinkFunction.class);

    public PrintSinkFunction() {
    }

    @Override
    public void open(Map<String, Object> parameters) throws Exception {
        super.open(parameters);
    }

    @Override
    public String toString() {
        return "PrintSinkFunction";
    }

    @Override
    public void invoke(IN record, Context context) {
        LOG.info(record.toString());
    }
}
