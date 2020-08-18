package com.mathandcs.kino.abacus.runtime.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.mathandcs.kino.abacus.api.plan.logical.LogicalPlan;
import com.mathandcs.kino.abacus.runtime.io.messages.JobSubmissionMessage;
import com.mathandcs.kino.abacus.runtime.schedule.Scheduler;

/**
 * Abacus Job Master
 */
public class Master extends AbstractActor {

    private final LoggingAdapter LOG = Logging.getLogger(getContext().getSystem(), this);

    private final ActorSystem actorSystem;

    private final Scheduler scheduler;

    public static Props props(ActorSystem actorSystem) {
        return Props.create(Master.class, () -> new Master(actorSystem));
    }

    public Master(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
        this.scheduler = new Scheduler(actorSystem);
    }

  @Override
    public void preStart() throws Exception {
        super.preStart();
        LOG.info("JobMaster started.");
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        LOG.info("JobMaster stopped.");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(String.class, x -> LOG.info("Received String: {}.", x))
            .match(JobSubmissionMessage.class, msg -> handleJobSubmissionMessage(msg))
            .build();
    }

    private void handleJobSubmissionMessage(JobSubmissionMessage msg) {
        LogicalPlan logicalPlan = msg.getLogicalPlan();
        scheduler.schedule(logicalPlan);
    }
}
