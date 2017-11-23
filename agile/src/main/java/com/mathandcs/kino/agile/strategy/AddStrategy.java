package com.mathandcs.kino.agile.strategy;

/**
 * Created by dashwang on 11/23/17.
 * 
 * ref: http://www.runoob.com/design-pattern/strategy-pattern.html
 */
public class AddStrategy implements Strategy{
    public int calculate(int a, int b) {
		return a + b;
	}
}
