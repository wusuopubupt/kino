package com.mathandcs.kino.abacus.runtime.master;

import akka.actor.AbstractActor;
import akka.actor.Props;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abacus Job Master
 */
public class JobMaster extends AbstractActor {

    private static final Logger LOG = LoggerFactory.getLogger(JobMaster.class);

    public static Props props() {
      return Props.create(JobMaster.class, JobMaster::new);
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
      return receiveBuilder().build();
    }

}
