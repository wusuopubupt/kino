package com.mathandcs.kino.abacus.runtime.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.mathandcs.kino.abacus.api.emitter.Emitter;
import com.mathandcs.kino.abacus.api.env.ExecutionEnvironment;
import com.mathandcs.kino.abacus.api.plan.logical.LogicalEdge;
import com.mathandcs.kino.abacus.api.plan.logical.LogicalNode;
import com.mathandcs.kino.abacus.api.record.StreamRecord;
import com.mathandcs.kino.abacus.runtime.io.messages.worker.OpeningMessage;
import com.mathandcs.kino.abacus.runtime.io.messages.worker.StartingMessage;
import com.mathandcs.kino.abacus.runtime.io.writer.StreamEmitter;
import com.mathandcs.kino.abacus.runtime.processor.Processor;
import com.mathandcs.kino.abacus.runtime.processor.ProcessorFactory;
import com.mathandcs.kino.abacus.runtime.processor.SourceProcessor;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abacus Worker Actor.
 */
public class Worker extends AbstractActor {

    private static final Logger LOG = LoggerFactory.getLogger(Worker.class);

    private Processor processor;
    private List<LogicalEdge> outputEdges;
    private List<Emitter> emitters = new ArrayList<>();

    public static Props props() {
      return Props.create(Worker.class, Worker::new);
    }

    public Worker() {

    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
            .match(OpeningMessage.class, msg -> handleOpeningMessage(msg))
            .match(StartingMessage.class, msg -> handleStartingMessage(msg))
            .match(StreamRecord.class, record -> process(record))
            .build();
    }

    private void handleOpeningMessage(OpeningMessage msg) {
        LOG.info("Handling OpeningMessage: {}.", msg);

        LogicalNode node = msg.getLogicalNode();

        processor = ProcessorFactory.createProcessor(node.getOperator());
        outputEdges = node.getOutputEdges();

        ExecutionEnvironment env = new ExecutionEnvironment();

        for (LogicalEdge edge : outputEdges) {
            emitters.add(new StreamEmitter(edge));
        }

        try {
            processor.open(env, emitters);
        } catch (Exception e) {
            throw new RuntimeException("Failed to open processor.", e);
        }
    }

    private void handleStartingMessage(StartingMessage msg) {
        LOG.info("Handling StartingMessage: {}.", msg);

        if (processor instanceof SourceProcessor) {
            processor.process(null);
        }
    }

    private void process(StreamRecord record) {
        processor.process(record);
    }

}
