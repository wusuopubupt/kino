package com.mathandcs.kino.effectivejava.agile.decorator;

/**
 * Created by dashwang on 11/23/17.
 * 
 * ref: http://www.runoob.com/design-pattern/decorator-pattern.html
 */
public class ShapeDecorator implements Shape {

	protected Shape decoratedShape;

	public ShapeDecorator(Shape shape) {
		this.decoratedShape = shape;
	}

    @Override 
    public void draw(){
		decoratedShape.draw();
	}
}
