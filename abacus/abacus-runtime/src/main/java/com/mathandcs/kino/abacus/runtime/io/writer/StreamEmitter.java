package com.mathandcs.kino.abacus.runtime.io.writer;

import akka.actor.ActorRef;
import com.google.common.base.Preconditions;
import com.mathandcs.kino.abacus.api.emitter.Emitter;
import com.mathandcs.kino.abacus.api.plan.logical.LogicalEdge;
import com.mathandcs.kino.abacus.api.record.StreamRecord;

public class StreamEmitter<T> implements Emitter<StreamRecord<T>> {

    private ActorRef sourceActor;
    private ActorRef targetActor;

    public StreamEmitter(LogicalEdge edge) {
        sourceActor = Preconditions.checkNotNull(edge.getSource().getActor(),
            "Source actor can not be null.");
        targetActor = Preconditions.checkNotNull(edge.getTarget().getActor(),
            "Target actor can not be null.");
    }

    @Override
    public void emit(StreamRecord<T> record) {
        targetActor.tell(record, sourceActor);
    }

    @Override
    public void close() {

    }

}
