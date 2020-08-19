package com.mathandcs.kino.abacus.api.operators;

import com.mathandcs.kino.abacus.api.emitter.Emitter;
import com.mathandcs.kino.abacus.api.env.ExecutionEnvironment;
import java.io.Serializable;
import java.util.List;

public interface Operator<OUT> extends Serializable {

	String getName();

	// ------------------------------------------------------------------------
	//  life cycle
	// ------------------------------------------------------------------------

	void open(ExecutionEnvironment env, List<Emitter> emitters) throws Exception;

	void close() throws Exception;

}
