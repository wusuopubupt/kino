package com.mathandcs.kino.abacus.api.plan.logical;

import com.mathandcs.kino.abacus.api.datastream.DataStream;
import com.mathandcs.kino.abacus.api.datastream.DataStreamSource;
import com.mathandcs.kino.abacus.api.env.ExecutionEnvironment;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogicalPlanGeneratorTest {
  
  private static final Logger LOG = LoggerFactory.getLogger(LogicalPlanGeneratorTest.class);

  @Test
  public void testGeneratePlan() {
    LogicalPlan logicalPlan = buildPipelinePlan();

    System.out.println(logicalPlan.toDigraph());

    Assert.assertNotNull(logicalPlan);
    Assert.assertEquals(4, logicalPlan.getIdToNodeMap().size());
  }

  public LogicalPlan buildPipelinePlan() {
      ExecutionEnvironment env = new ExecutionEnvironment();

      List<String> words = new ArrayList<>();
      words.add("a");
      words.add("bb");
      words.add("ccc");

      DataStream<String> source = DataStreamSource.buildSource(env, words);
      source
          .map(x -> "Word-" + x)
          .filter(x -> x.length() > 6)
          .sink(value -> LOG.info("Sink value is: {}.", value));

      LogicalPlanGenerator planGenerator = new LogicalPlanGenerator(env);
      return planGenerator.generate();
  }

}