package com.mathandcs.kino.abacus.api.plan.physical;

import com.mathandcs.kino.abacus.api.operators.Operator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PhysicalNode implements Serializable {

    private int index;
    private Operator operator;
    private List<PhysicalEdge> inputEdges = new ArrayList<>();
    private List<PhysicalEdge> outputEdges = new ArrayList<>();

    public PhysicalNode(int index, Operator operator) {
        this.index = index;
        this.operator = operator;
    }

    public void addInputEdge(PhysicalEdge edge) {
        if (!inputEdges.contains(edge)) {
            inputEdges.add(edge);
        }
    }

    public void addOutputEdge(PhysicalEdge edge) {
      if (!outputEdges.contains(edge)) {
          outputEdges.add(edge);
      }
    }

    public int getIndex() {
      return index;
    }

    public Operator getOperator() {
      return operator;
    }

    public List<PhysicalEdge> getInputEdges() {
      return inputEdges;
    }

    public List<PhysicalEdge> getOutputEdges() {
      return outputEdges;
    }
}
