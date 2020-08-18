package com.mathandcs.kino.abacus.runtime.master;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobMasterTest {

    private static final Logger LOG = LoggerFactory.getLogger(JobMasterTest.class);

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
    public void createReceive() {
        ActorSystem actorSystem = ActorSystem.create("abacus");
        ActorRef jobMaster = actorSystem.actorOf(JobMaster.props(), "job-master");
        LOG.info("Created actor job-master: {}.", jobMaster);
        Assert.assertNotNull(jobMaster);
    }
}