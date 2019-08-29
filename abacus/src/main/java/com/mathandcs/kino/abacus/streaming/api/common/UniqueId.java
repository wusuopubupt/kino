package com.mathandcs.kino.abacus.streaming.api.common;

import java.util.UUID;

public class UniqueId {

    private UUID id;

    public UniqueId() {
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