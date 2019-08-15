package com.mathandcs.kino.abacus.streaming.api.datastream;

import com.mathandcs.kino.abacus.streaming.api.operators.Operator;
import com.mathandcs.kino.abacus.streaming.runtime.utils.AbstractID;

public interface Transformable {

    AbstractID getId();

    Operator getOperator();

    Transformable getInput();
}
