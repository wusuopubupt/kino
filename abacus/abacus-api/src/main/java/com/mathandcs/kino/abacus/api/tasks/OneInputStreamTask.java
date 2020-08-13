package com.mathandcs.kino.abacus.api.tasks;

import com.mathandcs.kino.abacus.api.operators.OneInputOperator;

public class OneInputStreamTask<IN, OUT> extends StreamTask<OUT, OneInputOperator<IN, OUT>> {

	@Override
	public void init() throws Exception {

	}
}
