package com.mathandcs.kino.abacus.streaming.runtime.jobgraph;

/**
 * A distribution pattern determines, which sub tasks of a producing task are connected to which
 * consuming sub tasks.
 */
public enum DistributionPattern {

	ALL_TO_ALL,

	POINTWISE
}
