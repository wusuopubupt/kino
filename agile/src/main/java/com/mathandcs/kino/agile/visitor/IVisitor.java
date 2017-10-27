package com.mathandcs.kino.agile.visitor;

public interface IVisitor {
    void visit(Wheel wheel);

    void visit(Engine engine);

    void visit(Body body);

    void visit(Car car);
}
