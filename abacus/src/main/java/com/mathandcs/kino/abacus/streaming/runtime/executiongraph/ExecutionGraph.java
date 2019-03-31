package com.mathandcs.kino.abacus.streaming.runtime.executiongraph;

import com.mathandcs.kino.abacus.streaming.runtime.jobgraph.IntermediateDataSetID;
import com.mathandcs.kino.abacus.streaming.runtime.jobgraph.JobVertexID;
import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import lombok.Data;

@Data
public class ExecutionGraph implements IExecutionGraph, Serializable {

    private JobInformation                                 jobInformation;
    private Map<JobVertexID, ExecutionJobVertex>           executionJobVertices;
    private List<ExecutionJobVertex>                       verticesInCreationOrder;
    private Map<IntermediateDataSetID, IntermediateResult> intermediateResults;

    @Override
    public String getJobName() {
        return jobInformation.getJobName();
    }

    @Override
    public ExecutionJobVertex getJobVertex(JobVertexID id) {
        return executionJobVertices.get(id);
    }

    @Override
    public Map<JobVertexID, ExecutionJobVertex> getAllVertices() {
        return Collections.unmodifiableMap(executionJobVertices);
    }

    @Override
    public Iterable<ExecutionJobVertex> getVerticesTopologically() {
        // we return a specific iterator that does not fail with concurrent modifications
        // the list is append only, so it is safe for that
        final int numElements = this.verticesInCreationOrder.size();

        return new Iterable<ExecutionJobVertex>() {
            @Override
            public Iterator<ExecutionJobVertex> iterator() {
                return new Iterator<ExecutionJobVertex>() {
                    private int pos = 0;

                    @Override
                    public boolean hasNext() {
                        return pos < numElements;
                    }

                    @Override
                    public ExecutionJobVertex next() {
                        if (hasNext()) {
                            return verticesInCreationOrder.get(pos++);
                        } else {
                            throw new NoSuchElementException();
                        }
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }
}