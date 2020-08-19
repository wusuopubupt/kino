package com.mathandcs.kino.abacus.api.plan.logical;

import com.mathandcs.kino.abacus.api.datastream.DataStreamId;
import com.mathandcs.kino.abacus.api.datastream.DataStreamSink;
import com.mathandcs.kino.abacus.api.datastream.DataStreamSource;
import com.mathandcs.kino.abacus.api.datastream.IDataStream;
import com.mathandcs.kino.abacus.api.datastream.OneInputDataStream;
import com.mathandcs.kino.abacus.api.env.ExecutionEnvironment;
import com.mathandcs.kino.abacus.api.plan.PlanGenerator;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogicalPlanGenerator implements PlanGenerator<LogicalPlan> {

    private static final Logger LOG = LoggerFactory.getLogger(LogicalPlanGenerator.class);

    private List<IDataStream> dataStreams;
    private Set<DataStreamId> alreadyProcessed;
    private LogicalPlan logicalPlan;

    public LogicalPlanGenerator(ExecutionEnvironment env) {
        this.dataStreams = env.getDataStreams();
        this.alreadyProcessed = new HashSet<>();
    }

    @Override
    public LogicalPlan generate() {
        clear();

        logicalPlan = new LogicalPlan();

        for (IDataStream dataStream : dataStreams) {
          alreadyProcessed.addAll(process(dataStream));
        }

        return logicalPlan;
    }
    
    private void clear() {
        alreadyProcessed.clear();
        logicalPlan = null;
    }

    private Collection<DataStreamId> process(IDataStream dataStream) {
        // Skip already processed DataStreams.
        if (alreadyProcessed.contains(dataStream.getId())) {
            return Collections.emptySet();
        }

        LOG.info("Processing DataStream: {}.", dataStream.toString());

        Collection<DataStreamId> processedIds;
        if (dataStream instanceof DataStreamSource) {
            // source operator
            processedIds = processSource((DataStreamSource) dataStream);
        } else if (dataStream instanceof DataStreamSink) {
            // sink operator
            processedIds = processSink((DataStreamSink) dataStream);
        } else if (dataStream instanceof OneInputDataStream) {
            // one input operator
            processedIds = processOneInput((OneInputDataStream) dataStream);
        } else {
            throw new IllegalStateException("Unknown DataStream: " + dataStream);
        }
        return processedIds;
    }

    private LogicalNode buildLocalNodeByDataStream(IDataStream dataStream) {
      return new LogicalNode(
          dataStream.getId(),
          dataStream.getOperator(),
          dataStream.getParallelism()
      );
    }

    private Collection<DataStreamId> processSource(DataStreamSource source) {
      // Add current node to logical plan.
      LogicalNode node = buildLocalNodeByDataStream(source);
      logicalPlan.addNode(node);
      return Collections.singleton(source.getId());
    }

    private Collection<DataStreamId> processOneInput(OneInputDataStream stream) {
        // Add current node to logical plan.
        LogicalNode node = buildLocalNodeByDataStream(stream);

        // Get input node, if input node already in logicalPlan we just use it, else create a new one.
        IDataStream input = stream.getInput();
        LogicalNode inputNode = logicalPlan.getNode(input.getId());
        if (null == inputNode) {
            inputNode = new LogicalNode(input.getId(), input.getOperator(), input.getParallelism());
        }

        // Set input/output edges for current node and input node.
        LogicalEdge edge = new LogicalEdge(inputNode, node, input.getPartitioner());
        node.addInputEdge(edge);
        inputNode.addOutputEdge(edge);

        logicalPlan.addNode(node);
        logicalPlan.addNode(inputNode);

        return Collections.singleton(stream.getId());
    }

    private Collection<DataStreamId> processSink(DataStreamSink sink) {
        // Add current node to logical plan.
        LogicalNode node = buildLocalNodeByDataStream(sink);

        // Get input node, if input node already in logicalPlan we just use it, else create a new one.
        IDataStream input = sink.getInput();
        LogicalNode inputNode = logicalPlan.getNode(input.getId());
        if (null == inputNode) {
            inputNode = new LogicalNode(input.getId(), input.getOperator(), input.getParallelism());
        }

        // Set input/output edges for current node and input node.
        LogicalEdge edge = new LogicalEdge(inputNode, node, input.getPartitioner());
        node.addInputEdge(edge);
        inputNode.addOutputEdge(edge);

        logicalPlan.addNode(node);
        logicalPlan.addNode(inputNode);

        return Collections.singleton(sink.getId());
    }

}
