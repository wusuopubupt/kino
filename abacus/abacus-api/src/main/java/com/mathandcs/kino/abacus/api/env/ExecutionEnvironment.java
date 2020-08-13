package com.mathandcs.kino.abacus.api.env;

import com.mathandcs.kino.abacus.api.datastream.IDataStream;
import java.util.ArrayList;
import java.util.List;

public class ExecutionEnvironment {

    private List<IDataStream> dataStreams = new ArrayList<>();

    public void addDataStream(IDataStream dataStream) {
        dataStreams.add(dataStream);
    }

    public List<IDataStream> getDataStreams() {
        return dataStreams;
    }

}