package com.mathandcs.kino.abacus.streaming.runtime.utils;

import java.util.UUID;

import com.google.common.base.MoreObjects;
import lombok.Data;

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