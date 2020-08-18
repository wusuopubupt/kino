package com.mathandcs.kino.abacus.api.fetcher;

public interface Fetcher<T> {

  /**
   * @return next element
   */
  T fetchNext();

}
