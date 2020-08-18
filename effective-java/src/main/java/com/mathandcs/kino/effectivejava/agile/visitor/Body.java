package com.mathandcs.kino.effectivejava.agile.visitor;

class Body implements CarElement {
    public void accept(final CarElementVisitor visitor) {
        visitor.visit(this);
    }
}