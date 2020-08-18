package com.mathandcs.kino.abacus.runtime.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.mathandcs.kino.abacus.api.plan.logical.LogicalNode;
import com.mathandcs.kino.abacus.api.record.StreamRecord;
import com.mathandcs.kino.abacus.runtime.io.messages.StartingMessage;
import com.mathandcs.kino.abacus.runtime.processor.Processor;
import com.mathandcs.kino.abacus.runtime.processor.ProcessorFactory;
import com.mathandcs.kino.abacus.runtime.processor.SourceProcessor;

public class Worker extends AbstractActor {

    private Processor processor;

    public static Props props(LogicalNode node) {
      return Props.create(Worker.class, () -> new Worker(node));
    }

    public Worker(LogicalNode node) {
        processor = ProcessorFactory.createProcessor(node.getOperator());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
          .match(StartingMessage.class, msg -> handleStartingMessage(msg))
          .match(StreamRecord.class, record -> process(record))
          .build();
    }

    private void handleStartingMessage(StartingMessage msg) {
        if (processor instanceof SourceProcessor) {
            processor.process(null);
        }
    }

    private void process(StreamRecord record) {
        processor.process(record);
    }

}
