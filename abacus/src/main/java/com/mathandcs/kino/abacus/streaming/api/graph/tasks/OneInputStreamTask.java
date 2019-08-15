package com.mathandcs.kino.abacus.streaming.api.graph.tasks;

import com.mathandcs.kino.abacus.streaming.api.operators.OneInputOperator;

public class OneInputStreamTask<IN, OUT> extends StreamTask<OUT, OneInputOperator<IN, OUT>> {

	@Override
	public void init() throws Exception {

	}
}
