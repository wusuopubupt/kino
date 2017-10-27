package com.mathandcs.kino.agile.visitor;

class Body implements CarElement {
    public void accept(final CarElementVisitor visitor) {
        visitor.visit(this);
    }
}