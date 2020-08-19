package com.mathandcs.kino.abacus.runtime.schedule;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.mathandcs.kino.abacus.api.plan.logical.LogicalNode;
import com.mathandcs.kino.abacus.api.plan.logical.LogicalPlan;
import com.mathandcs.kino.abacus.runtime.actors.Worker;
import com.mathandcs.kino.abacus.runtime.io.messages.worker.OpeningMessage;
import com.mathandcs.kino.abacus.runtime.io.messages.worker.StartingMessage;
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
        List<LogicalNode> allNodes = logicalPlan.getAllNodes();

        // 1. Create worker actors and update ActorRef in Node
        for (LogicalNode node : allNodes) {
            ActorRef workerActor = actorSystem.actorOf(Worker.props());
            node.setActor(workerActor);
            LOG.info("Created worker actor: {}. operator: {}.", workerActor, node.getOperator().getName());

            if (node.isSource()) {
                sources.add(workerActor);
            }
        }

        // 2. Open all worker actors
        for (LogicalNode node : allNodes) {
            node.getActor().tell(new OpeningMessage(node), ActorRef.noSender());
        }

        // 3. Start source actors
        for (ActorRef sourceActor : sources) {
            LOG.info("Telling source actor {} to start processing.", sourceActor);
            sourceActor.tell(new StartingMessage(), ActorRef.noSender());
        }

    }
}
