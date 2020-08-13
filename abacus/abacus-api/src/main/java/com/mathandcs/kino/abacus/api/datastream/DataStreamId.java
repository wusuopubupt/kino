package com.mathandcs.kino.abacus.api.datastream;

public class DataStreamId {
    private int index;
    private String operatorName;

    public DataStreamId(int index, String operatorName) {
      this.index = index;
      this.operatorName = operatorName;
    }

  @Override
  public String toString() {
    return String.format("%s_%d", operatorName, index);
  }
}
