package com.mathandcs.kino.abacus.streaming.api.functions;

import com.mathandcs.kino.abacus.streaming.api.context.RuntimeContext;

import java.io.Serializable;
import java.util.Map;

public abstract class AbstractRichFunction implements RichFunction, Serializable {

	private static final long serialVersionUID = 1L;

	// --------------------------------------------------------------------------------------------
	//  Runtime context access
	// --------------------------------------------------------------------------------------------

	private transient RuntimeContext runtimeContext;

	@Override
	public void setRuntimeContext(RuntimeContext t) {
		this.runtimeContext = t;
	}

	@Override
	public RuntimeContext getRuntimeContext() {
		if (this.runtimeContext != null) {
			return this.runtimeContext;
		} else {
			throw new IllegalStateException("The runtime context has not been initialized.");
		}
	}

	// --------------------------------------------------------------------------------------------
	//  Default life cycle methods
	// --------------------------------------------------------------------------------------------

	@Override
	public void open(Map<String, Object> parameters) throws Exception {}

	@Override
	public void close() throws Exception {}
}
