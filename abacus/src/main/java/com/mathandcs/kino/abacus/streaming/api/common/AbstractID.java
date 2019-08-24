package com.mathandcs.kino.abacus.streaming.api.common;

import java.util.UUID;

public class AbstractID {

    private UUID id;

    public AbstractID() {
        id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }

    @Override
    public String toString() {
        return id.toString();
    }
}