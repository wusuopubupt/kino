package com.mathandcs.kino.effectivejava.generic;

/**
 * Created by dashwang on 8/28/17.
 * <p>
 * L is the first element's type
 * R is the second element's type
 */
public class Pair<L, R> {
    private L first;
    private R second;

    public Pair(L first, R second) {
        this.first = first;
        this.second = second;
    }

    public L getFirst() {
        return this.first;
    }

    public R getSecond() {
        return this.second;
    }

    public void setFirst(L first) {
        this.first = first;
    }

    public void setSecond(R second) {
        this.second = second;
    }

    private static boolean equals(Object x, Object y) {
        return (x == null && y == null) || (x != null && x.equals(y));
    }

    @Override
    public boolean equals(Object other) {
        return
                other instanceof Pair &&
                        equals(first, ((Pair) other).first) &&
                        equals(second, ((Pair) other).second);
    }

    @Override
    public int hashCode() {
        if (first == null) return (second == null) ? 0 : second.hashCode() + 1;
        else if (second == null) return first.hashCode() + 2;
        else return first.hashCode() * 17 + second.hashCode();
    }
}
