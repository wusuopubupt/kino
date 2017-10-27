package com.mathandcs.kino.agile.visitor;

class Engine implements CarElement {
    public void accept(final CarElementVisitor visitor) {
        visitor.visit(this);
    }
}