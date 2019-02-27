package com.mathandcs.kino.abacus.streaming.api.functions;

import com.mathandcs.kino.abacus.streaming.api.context.RuntimeContext;
import java.util.Map;

public interface RichFunction extends Function {
	void open(Map<String, Object> config) throws Exception;
	void close() throws Exception;
	RuntimeContext getRuntimeContext();
}