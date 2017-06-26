package com.mathandcs.kino.abacus.exception;

/**
 * Created by dashwang on 6/19/17.
 */
public class JobExecutionException extends RuntimeException {

    private final static long serialVersionUID = 1;

    public JobExecutionException(final String message) {
        super(message);
    }

    public JobExecutionException(final Throwable cause) {
        super(cause);
    }

    public JobExecutionException(final String message, final Throwable cause) {
        super(message, cause);
    }

}