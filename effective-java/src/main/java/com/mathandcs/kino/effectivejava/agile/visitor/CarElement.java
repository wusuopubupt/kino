package com.mathandcs.kino.effectivejava.agile.visitor;

interface CarElement {
    void accept(CarElementVisitor visitor);
}