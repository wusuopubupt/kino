package com.mathandcs.kino.abacus.streaming.api.windowing.windowns;

public class GlobalWindow extends Window {

	private static final GlobalWindow INSTANCE = new GlobalWindow();

	private GlobalWindow() { }

	public static GlobalWindow get() {
		return INSTANCE;
	}

	@Override
	public long maxTimestamp() {
		return Long.MAX_VALUE;
	}

	@Override
	public boolean equals(Object o) {
		return this == o || !(o == null || getClass() != o.getClass());
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public String toString() {
		return "GlobalWindow";
	}
}
