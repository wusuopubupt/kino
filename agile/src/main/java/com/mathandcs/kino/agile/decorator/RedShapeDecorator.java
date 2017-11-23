package com.mathandcs.kino.agile.decorator;

/**
 * Created by dashwang on 11/23/17.
 * 
 * ref: http://www.runoob.com/design-pattern/decorator-pattern.html
 */
public class RedShapeDecorator extends ShapeDecorator {

	public RedShapeDecorator(Shape shape) {
		super(shape);
	}

    @Override 
    public void draw(){
		decoratedShape.draw();
		setRedBorder(decoratedShape);
	}

	private void setRedBorder(Shape shape) {
		System.out.println("Border corlor: red");	
	}
}
