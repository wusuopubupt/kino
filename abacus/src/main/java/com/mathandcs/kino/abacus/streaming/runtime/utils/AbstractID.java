package com.mathandcs.kino.abacus.streaming.runtime.utils;

import java.util.UUID;
import lombok.Data;

@Data
public class AbstractID {

    private UUID id;

    public AbstractID() {
        id = UUID.randomUUID();
    }
}