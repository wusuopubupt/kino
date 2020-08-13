package com.mathandcs.kino.abacus.api.tasks;

import com.mathandcs.kino.abacus.api.operators.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class StreamTask<OUT, OP extends Operator<OUT>> extends AbstractInvokable {

	protected static final Logger LOG = LoggerFactory.getLogger(StreamTask.class);

	/** the head operator that consumes the input streams of this task. */
	protected OP headOperator;

	public StreamTask() {
	}

	// ------------------------------------------------------------------------
	//  Life cycle methods for specific implementations
	// ------------------------------------------------------------------------

	protected abstract void init() throws Exception;

	@Override
	public final void invoke() throws Exception {

	}
}
