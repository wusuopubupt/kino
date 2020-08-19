package com.mathandcs.kino.abacus.runtime.actors.master;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.mathandcs.kino.abacus.api.datastream.DataStream;
import com.mathandcs.kino.abacus.api.datastream.DataStreamSource;
import com.mathandcs.kino.abacus.api.env.ExecutionEnvironment;
import com.mathandcs.kino.abacus.api.plan.logical.LogicalPlan;
import com.mathandcs.kino.abacus.api.plan.logical.LogicalPlanGenerator;
import com.mathandcs.kino.abacus.runtime.actors.Master;
import com.mathandcs.kino.abacus.runtime.io.messages.master.JobSubmissionMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
    public void testCreateReceive() throws Exception {
        ActorSystem actorSystem = ActorSystem.create("abacus");
        ActorRef jobMaster = actorSystem.actorOf(Master.props(actorSystem), "job-master-1");

        LOG.info("Created actor job-master1: {}.", jobMaster);
        Assert.assertNotNull(jobMaster);

        jobMaster.tell("hello", ActorRef.noSender());

        LogicalPlan logicalPlan = buildPipelinePlan();

        jobMaster.tell(new JobSubmissionMessage(logicalPlan), ActorRef.noSender());

        ActorSystem actorSystem2 = ActorSystem.create("actor-system-2");
        ActorRef jobMaster2 = actorSystem2.actorOf(Master.props(actorSystem2), "job-master-2");
        jobMaster.tell("world", jobMaster2);

        TimeUnit.SECONDS.sleep(10);

        actorSystem.terminate();
        actorSystem2.terminate();
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