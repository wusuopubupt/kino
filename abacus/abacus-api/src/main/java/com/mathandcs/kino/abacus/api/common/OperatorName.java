package com.mathandcs.kino.abacus.api.common;

public enum OperatorName {

    SOURCE("source"),
    SINK("sink"),
    MAP("map"),
    FILTER("filter"),
    FLATMAP("flatmap");

    private String name;

    OperatorName(String name) {
        this.name = name;
    }

}
