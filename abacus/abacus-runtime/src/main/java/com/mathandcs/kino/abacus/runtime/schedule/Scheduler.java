package com.mathandcs.kino.abacus.runtime.schedule;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.mathandcs.kino.abacus.api.plan.logical.LogicalNode;
import com.mathandcs.kino.abacus.api.plan.logical.LogicalPlan;
import com.mathandcs.kino.abacus.runtime.actors.Worker;
import com.mathandcs.kino.abacus.runtime.io.messages.StartingMessage;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Scheduler implements IScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(Scheduler.class);

    private final ActorSystem actorSystem;

    public Scheduler(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
    }

    @Override
    public void schedule(LogicalPlan logicalPlan) {
        LOG.info("Scheduling plan: {}.", logicalPlan.toDigraph());
        List<ActorRef> sources = new ArrayList<>();
        for (LogicalNode node : logicalPlan.getAllNodes()) {
            ActorRef worker = actorSystem.actorOf(Worker.props(node));
            if (node.isSource()) {
                sources.add(worker);
            }
        }

        for (ActorRef sourceActor : sources) {
            sourceActor.tell(new StartingMessage(), ActorRef.noSender());
        }

    }
}
