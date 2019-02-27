package com.mathandcs.kino.abacus.streaming.api.functions;

import java.io.Serializable;

@FunctionalInterface
public interface KeySelector<IN, KEY> extends Function, Serializable {
	KEY getKey(IN value) throws Exception;
}
