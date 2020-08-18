package com.mathandcs.kino.effectivejava.agile.visitor;

/**
 * ref: https://en.wikipedia.org/wiki/Visitor_pattern
 * */
public class VisitorDemo {
    public static void main(final String[] args) {
        final Car car = new Car();

        car.accept(new CarElementPrintVisitor());
        car.accept(new CarElementDoVisitor());
    }
}