package com.mathandcs.kino.abacus.api.operators;

import java.io.Serializable;

public interface Operator<OUT> extends Serializable {

	String getName();

	// ------------------------------------------------------------------------
	//  life cycle
	// ------------------------------------------------------------------------

	void open() throws Exception;
	void close() throws Exception;

}
