package com.mathandcs.kino.abacus.runtime.actors.master;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.mathandcs.kino.abacus.api.datastream.DataStream;
import com.mathandcs.kino.abacus.api.datastream.DataStreamSource;
import com.mathandcs.kino.abacus.api.env.ExecutionEnvironment;
import com.mathandcs.kino.abacus.api.plan.logical.LogicalPlan;
import com.mathandcs.kino.abacus.api.plan.logical.LogicalPlanGenerator;
import com.mathandcs.kino.abacus.runtime.actors.Master;
import com.mathandcs.kino.abacus.runtime.io.messages.JobSubmissionMessage;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MasterTest {

    private static final Logger LOG = LoggerFactory.getLogger(MasterTest.class);

    @Test
    public void props() {

    }

    @Test
    public void preStart() {
    }

    @Test
    public void postStop() {
    }

    @Test
    public void testCreateReceive() {
        ActorSystem actorSystem = ActorSystem.create("abacus");
        ActorRef jobMaster = actorSystem.actorOf(Master.props(actorSystem), "job-master-1");

        LOG.info("Created actor job-master1: {}.", jobMaster);
        Assert.assertNotNull(jobMaster);

        jobMaster.tell("hello", ActorRef.noSender());

        LogicalPlan logicalPlan = buildPipelinePlan();
        LOG.info("LogicalPlan is: {}.", logicalPlan.toDigraph());

        jobMaster.tell(new JobSubmissionMessage(logicalPlan), ActorRef.noSender());

        actorSystem.terminate();
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