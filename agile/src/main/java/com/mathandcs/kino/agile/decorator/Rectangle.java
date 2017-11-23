package com.mathandcs.kino.agile.decorator;

/**
 * Created by dashwang on 11/23/17.
 * 
 * ref: http://www.runoob.com/design-pattern/decorator-pattern.html
 */
public class Rectangle implements Shape {
    @Override 
    public void draw(){
		System.out.println("Shape: rectangle");
	}
}
