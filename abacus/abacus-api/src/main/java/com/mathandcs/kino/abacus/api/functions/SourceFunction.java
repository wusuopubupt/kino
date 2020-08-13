package com.mathandcs.kino.abacus.api.functions;

import java.io.Serializable;

public interface SourceFunction<T> extends Function, Serializable {

	void run(SourceContext<T> ctx) throws Exception;

	void cancel();

	interface SourceContext<T> {

		/**
		 * Emits one element from the source, without attaching a timestamp.
		 *
		 * @param element The element to emit
		 */
		void emit(T element);

		/**
		 * This method is called by the system to shut down the context.
		 */
		void close();
	}
}
