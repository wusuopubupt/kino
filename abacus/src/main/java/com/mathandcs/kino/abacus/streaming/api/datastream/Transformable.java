package com.mathandcs.kino.abacus.streaming.api.datastream;

import com.mathandcs.kino.abacus.streaming.api.operators.Operator;
import com.mathandcs.kino.abacus.streaming.api.common.UniqueId;

public interface Transformable {

    UniqueId getId();

    Operator getOperator();

    Transformable getInput();

}
