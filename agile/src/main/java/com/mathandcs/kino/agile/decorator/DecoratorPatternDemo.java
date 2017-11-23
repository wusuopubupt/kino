package com.mathandcs.kino.agile.decorator;

/**
 * Created by dashwang on 11/23/17.
 * 
 * ref: http://www.runoob.com/design-pattern/decorator-pattern.html
 */
public class DecoratorPatternDemo {
	public static void main(String[] args) {
		Shape circle = new Circle();
		Shape redCircle = new RedShapeDecorator(new Circle());
		Shape redRectangle = new RedShapeDecorator(new Rectangle());

    	System.out.println("Circle with normal border");
      	circle.draw();

      	System.out.println("\nCircle of red border");
      	redCircle.draw();

      	System.out.println("\nRectangle of red border");
      	redRectangle.draw();
	}
}
