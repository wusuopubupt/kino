package com.mathandcs.kino.abacus.core.io;

public interface RecordReader<T> {

  /**
   * @return record from upstream.
   */
  T read();

}
