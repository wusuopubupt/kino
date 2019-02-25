package com.mathandcs.kino.effectivejava.highavailability.leaderretrieval;

public class LeaderRetrievalException extends Exception {

	private static final long serialVersionUID = 42;

	public LeaderRetrievalException(String message) {
		super(message);
	}

	public LeaderRetrievalException(Throwable cause) {
		super(cause);
	}

	public LeaderRetrievalException(String message, Throwable cause) {
		super(message, cause);
	}
}
