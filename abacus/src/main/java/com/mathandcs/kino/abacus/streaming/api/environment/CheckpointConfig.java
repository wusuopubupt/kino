package com.mathandcs.kino.abacus.streaming.api.environment;

import static java.util.Objects.requireNonNull;

public class CheckpointConfig implements java.io.Serializable {


	/** The default checkpoint mode: exactly once. */
	public static final CheckpointingMode DEFAULT_MODE = CheckpointingMode.EXACTLY_ONCE;

	/** The default timeout of a checkpoint attempt: 10 minutes. */
	public static final long DEFAULT_TIMEOUT = 10 * 60 * 1000;

	/** The default minimum pause to be made between checkpoints: none. */
	public static final long DEFAULT_MIN_PAUSE_BETWEEN_CHECKPOINTS = 0;

	/** The default limit of concurrently happening checkpoints: one. */
	public static final int DEFAULT_MAX_CONCURRENT_CHECKPOINTS = 1;

	// ------------------------------------------------------------------------

	/** Checkpointing mode (exactly-once vs. at-least-once). */
	private CheckpointingMode checkpointingMode = DEFAULT_MODE;

	/** Periodic checkpoint triggering interval. */
	private long checkpointInterval = -1; // disabled

	/** Maximum time checkpoint may take before being discarded. */
	private long checkpointTimeout = DEFAULT_TIMEOUT;

	/** Minimal pause between checkpointing attempts. */
	private long minPauseBetweenCheckpoints = DEFAULT_MIN_PAUSE_BETWEEN_CHECKPOINTS;

	/** Maximum number of checkpoint attempts in progress at the same time. */
	private int maxConcurrentCheckpoints = DEFAULT_MAX_CONCURRENT_CHECKPOINTS;

	/** Flag to force checkpointing in iterative jobs. */
	private boolean forceCheckpointing;

	/** Determines if a tasks are failed or not if there is an error in their checkpointing. Default: true */
	private boolean failOnCheckpointingErrors = true;

	// ------------------------------------------------------------------------

	/**
	 * Checks whether checkpointing is enabled.
	 *
	 * @return True if checkpointing is enables, false otherwise.
	 */
	public boolean isCheckpointingEnabled() {
		return checkpointInterval > 0;
	}

	/**
	 * Gets the checkpointing mode (exactly-once vs. at-least-once).
	 *
	 * @return The checkpointing mode.
	 */
	public CheckpointingMode getCheckpointingMode() {
		return checkpointingMode;
	}

	/**
	 * Sets the checkpointing mode (exactly-once vs. at-least-once).
	 *
	 * @param checkpointingMode The checkpointing mode.
	 */
	public void setCheckpointingMode(CheckpointingMode checkpointingMode) {
		this.checkpointingMode = requireNonNull(checkpointingMode);
	}

	/**
	 * Gets the interval in which checkpoints are periodically scheduled.
	 *
	 * <p>This setting defines the base interval. Checkpoint triggering may be delayed by the settings
	 * {@link #getMaxConcurrentCheckpoints()} and {@link #getMinPauseBetweenCheckpoints()}.
	 *
	 * @return The checkpoint interval, in milliseconds.
	 */
	public long getCheckpointInterval() {
		return checkpointInterval;
	}

	/**
	 * Sets the interval in which checkpoints are periodically scheduled.
	 *
	 * <p>This setting defines the base interval. Checkpoint triggering may be delayed by the settings
	 * {@link #setMaxConcurrentCheckpoints(int)} and {@link #setMinPauseBetweenCheckpoints(long)}.
	 *
	 * @param checkpointInterval The checkpoint interval, in milliseconds.
	 */
	public void setCheckpointInterval(long checkpointInterval) {
		if (checkpointInterval <= 0) {
			throw new IllegalArgumentException("Checkpoint interval must be larger than zero");
		}
		this.checkpointInterval = checkpointInterval;
	}

	/**
	 * Gets the maximum time that a checkpoint may take before being discarded.
	 *
	 * @return The checkpoint timeout, in milliseconds.
	 */
	public long getCheckpointTimeout() {
		return checkpointTimeout;
	}

	/**
	 * Sets the maximum time that a checkpoint may take before being discarded.
	 *
	 * @param checkpointTimeout The checkpoint timeout, in milliseconds.
	 */
	public void setCheckpointTimeout(long checkpointTimeout) {
		if (checkpointTimeout <= 0) {
			throw new IllegalArgumentException("Checkpoint timeout must be larger than zero");
		}
		this.checkpointTimeout = checkpointTimeout;
	}

	/**
	 * Gets the minimal pause between checkpointing attempts. This setting defines how soon the
	 * checkpoint coordinator may trigger another checkpoint after it becomes possible to trigger
	 * another checkpoint with respect to the maximum number of concurrent checkpoints
	 * (see {@link #getMaxConcurrentCheckpoints()}).
	 *
	 * @return The minimal pause before the next checkpoint is triggered.
	 */
	public long getMinPauseBetweenCheckpoints() {
		return minPauseBetweenCheckpoints;
	}

	/**
	 * Sets the minimal pause between checkpointing attempts. This setting defines how soon the
	 * checkpoint coordinator may trigger another checkpoint after it becomes possible to trigger
	 * another checkpoint with respect to the maximum number of concurrent checkpoints
	 * (see {@link #setMaxConcurrentCheckpoints(int)}).
	 *
	 * <p>If the maximum number of concurrent checkpoints is set to one, this setting makes effectively sure
	 * that a minimum amount of time passes where no checkpoint is in progress at all.
	 *
	 * @param minPauseBetweenCheckpoints The minimal pause before the next checkpoint is triggered.
	 */
	public void setMinPauseBetweenCheckpoints(long minPauseBetweenCheckpoints) {
		if (minPauseBetweenCheckpoints < 0) {
			throw new IllegalArgumentException("Pause value must be zero or positive");
		}
		this.minPauseBetweenCheckpoints = minPauseBetweenCheckpoints;
	}

	/**
	 * Gets the maximum number of checkpoint attempts that may be in progress at the same time. If this
	 * value is <i>n</i>, then no checkpoints will be triggered while <i>n</i> checkpoint attempts are
	 * currently in flight. For the next checkpoint to be triggered, one checkpoint attempt would need
	 * to finish or expire.
	 *
	 * @return The maximum number of concurrent checkpoint attempts.
	 */
	public int getMaxConcurrentCheckpoints() {
		return maxConcurrentCheckpoints;
	}

	/**
	 * Sets the maximum number of checkpoint attempts that may be in progress at the same time. If this
	 * value is <i>n</i>, then no checkpoints will be triggered while <i>n</i> checkpoint attempts are
	 * currently in flight. For the next checkpoint to be triggered, one checkpoint attempt would need
	 * to finish or expire.
	 *
	 * @param maxConcurrentCheckpoints The maximum number of concurrent checkpoint attempts.
	 */
	public void setMaxConcurrentCheckpoints(int maxConcurrentCheckpoints) {
		if (maxConcurrentCheckpoints < 1) {
			throw new IllegalArgumentException("The maximum number of concurrent attempts must be at least one.");
		}
		this.maxConcurrentCheckpoints = maxConcurrentCheckpoints;
	}

	/**
	 * Checks whether checkpointing is forced, despite currently non-checkpointable iteration feedback.
	 *
	 * @return True, if checkpointing is forced, false otherwise.
	 *
	 * @deprecated This will be removed once iterations properly participate in checkpointing.
	 */
	@Deprecated
	public boolean isForceCheckpointing() {
		return forceCheckpointing;
	}

	/**
	 * Checks whether checkpointing is forced, despite currently non-checkpointable iteration feedback.
	 *
	 * @param forceCheckpointing The flag to force checkpointing.
	 *
	 * @deprecated This will be removed once iterations properly participate in checkpointing.
	 */
	@Deprecated
	public void setForceCheckpointing(boolean forceCheckpointing) {
		this.forceCheckpointing = forceCheckpointing;
	}

	/**
	 * This determines the behaviour of tasks if there is an error in their local checkpointing. If this returns true,
	 * tasks will fail as a reaction. If this returns false, task will only decline the failed checkpoint.
	 */
	public boolean isFailOnCheckpointingErrors() {
		return failOnCheckpointingErrors;
	}

	/**
	 * Sets the expected behaviour for tasks in case that they encounter an error in their checkpointing procedure.
	 * If this is set to true, the task will fail on checkpointing error. If this is set to false, the task will only
	 * decline a the checkpoint and continue running. The default is true.
	 */
	public void setFailOnCheckpointingErrors(boolean failOnCheckpointingErrors) {
		this.failOnCheckpointingErrors = failOnCheckpointingErrors;
	}

}
