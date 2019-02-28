package com.mathandcs.kino.abacus.streaming.api.windowing.time;

import java.util.concurrent.TimeUnit;
import lombok.Getter;

/**
 * The definition of a time interval for windowing. The time characteristic referred
 * to is the default time characteristic set on the execution environment.
 */
public class Time {

    @Getter
    private final long     size;
    @Getter
    private final TimeUnit unit;

    public Time(long size, TimeUnit unit) {
        this.size = size;
        this.unit = unit;
    }

    public long toMilliseconds() {
        return unit.toMillis(size);
    }

    /**
     * Creates a new {@link Time} of the given duration and {@link TimeUnit}.
     *
     * @param size The duration of time.
     * @param unit The unit of time of the duration, for example {@code TimeUnit.SECONDS}.
     * @return The time policy.
     */
    public static Time of(long size, TimeUnit unit) {
        return new Time(size, unit);
    }

    /**
     * Creates a new {@link Time} that represents the given number of milliseconds.
     */
    public static Time milliseconds(long milliseconds) {
        return of(milliseconds, TimeUnit.MILLISECONDS);
    }

    /**
     * Creates a new {@link Time} that represents the given number of seconds.
     */
    public static Time seconds(long seconds) {
        return of(seconds, TimeUnit.SECONDS);
    }

    /**
     * Creates a new {@link Time} that represents the given number of minutes.
     */
    public static Time minutes(long minutes) {
        return of(minutes, TimeUnit.MINUTES);
    }

    /**
     * Creates a new {@link Time} that represents the given number of hours.
     */
    public static Time hours(long hours) {
        return of(hours, TimeUnit.HOURS);
    }

    /**
     * Creates a new {@link Time} that represents the given number of days.
     */
    public static Time days(long days) {
        return of(days, TimeUnit.DAYS);
    }
}