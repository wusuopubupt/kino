package com.mathandcs.kino.abacus.api.fetcher;

import com.mathandcs.kino.abacus.api.record.StreamRecord;
import com.mathandcs.kino.abacus.core.io.RecordReader;

public class StreamFetcher implements Fetcher<StreamRecord> {

    private final RecordReader<StreamRecord> recordReader;

    public StreamFetcher(RecordReader recordReader) {
      this.recordReader = recordReader;
    }

  @Override
    public StreamRecord fetchNext() {
      return recordReader.read();
    }

}
