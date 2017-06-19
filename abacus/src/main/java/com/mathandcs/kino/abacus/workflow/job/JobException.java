package com.mathandcs.kino.abacus.workflow.job;

/**
 * Created by dashwang on 6/19/17.
 */
public class JobException extends RuntimeException {

    private final static long serialVersionUID = 1;

    public JobException(final String message) {
        super(message);
    }

    public JobException(final Throwable cause) {
        super(cause);
    }

    public JobException(final String message, final Throwable cause) {
        super(message, cause);
    }

}