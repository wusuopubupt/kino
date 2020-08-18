package com.mathandcs.kino.effectivejava.agile.strategy;

/**
 * Created by dashwang on 11/23/17.
 * 
 * ref: http://www.runoob.com/design-pattern/strategy-pattern.html
 */
public class Context {

	private Strategy strategy;

	public Context(Strategy strategy) {
		this.strategy = strategy;
	}

	public int calculate(int a, int b) {
		return strategy.calculate(a, b);
	}
}
