package com.mathandcs.kino.abacus.streaming.api.graph;

import com.mathandcs.kino.abacus.streaming.api.datastream.DataStream;
import com.mathandcs.kino.abacus.streaming.api.environment.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(GraphBuilder.class);

    private Graph       graph;
    private Environment env;

    public GraphBuilder(Environment env) {
        this.env = env;
    }

    private static Graph build(Environment env, DataStream dataStream) {
        return new GraphBuilder(env).buildInternal(dataStream);
    }

    private Graph buildInternal(DataStream dataStream) {

        return graph;
    }


}