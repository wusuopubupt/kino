package com.mathandcs.kino.abacus.api.partition;

public interface Partitioner<T> {

  /**
   * Returns the logical channel indexes, to which the given record should be
   * written.
   *
   * @param record      the record to the determine the output channels for
   * @param numPartitions the total number of output channels which are attached to respective output gate
   * @return a (possibly empty) array of integer numbers which indicate the indices of the output channels through
   * which the record shall be forwarded
   */
  int[] partition(T record, int numPartitions);

}
