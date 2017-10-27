package com.mathandcs.kino.agile.visitor;

class Car implements CarElement {
    CarElement[] elements;

    public Car() {
        this.elements = new CarElement[] {
                new Wheel("front left"), new Wheel("front right"),
                new Wheel("back left"), new Wheel("back right"),
                new Body(), new Engine()
        };
    }

    public void accept(final CarElementVisitor visitor) {
        for (CarElement elem : elements) {
            elem.accept(visitor);
        }
        visitor.visit(this);
    }
}
