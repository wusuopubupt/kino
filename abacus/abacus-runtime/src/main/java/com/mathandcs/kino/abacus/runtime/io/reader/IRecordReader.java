package com.mathandcs.kino.abacus.runtime.io.reader;

public interface IRecordReader<T> {

  /**
   * @return record from upstream.
   */
  T read();

}
