package com.mathandcs.kino.abacus.streaming.api.operators;

import java.io.Serializable;

public interface Operator<OUT> extends Serializable {

	// ------------------------------------------------------------------------
	//  life cycle
	// ------------------------------------------------------------------------

	void open() throws Exception;
	void close() throws Exception;
}
