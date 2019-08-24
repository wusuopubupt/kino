package com.mathandcs.kino.abacus;

import com.mathandcs.kino.abacus.streaming.api.collector.Collector;
import com.mathandcs.kino.abacus.streaming.api.common.ExecutionConfig;
import com.mathandcs.kino.abacus.streaming.api.common.RunMode;
import com.mathandcs.kino.abacus.streaming.api.datastream.DataStream;
import com.mathandcs.kino.abacus.streaming.api.environment.ExecutionEnvironment;
import com.mathandcs.kino.abacus.streaming.api.functions.FlatMapFunction;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WordCount {

    public static void main(String[] args) throws Exception {

        // set up the execution environment
        ExecutionConfig config = new ExecutionConfig();
        config.setJobName("Streaming WordCount");
        config.setRunMode(RunMode.LOCAL);

        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment(config);

        List<String> collection = new ArrayList<>(Arrays.asList("hello world this is kino"));

        // get input data
        DataStream<String> text = env.fromCollection(collection, 1);

        DataStream<String> counts = text
                .flatMap(new Tokenizer())
                .filter(v -> v.endsWith("o"));
        //.keyBy(0).sum(1);
        counts.print();

        // execute program
        env.execute();
    }

    // *************************************************************************
    // USER FUNCTIONS
    // *************************************************************************

    /**
     * Implements the string tokenizer that splits sentences into words as a
     * user-defined FlatMapFunction. The function takes a line (String) and
     * splits it into multiple pairs in the form of "(word,1)" ({@code Tuple2<String,
     * Integer>}).
     */
    public static final class Tokenizer implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
            // normalize and split the line
            String[] tokens = value.toLowerCase().split("\\W+");

            // emit the pairs
            for (String token : tokens) {
                if (token.length() > 0) {
                    out.collect(new Tuple2<>(token, 1));
                }
            }
        }
    }

}
