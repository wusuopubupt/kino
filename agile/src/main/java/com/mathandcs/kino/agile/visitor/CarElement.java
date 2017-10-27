package com.mathandcs.kino.agile.visitor;

interface CarElement {
    void accept(CarElementVisitor visitor);
}