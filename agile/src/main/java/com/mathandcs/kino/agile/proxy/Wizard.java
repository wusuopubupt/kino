package com.mathandcs.kino.agile.proxy;

/**
 * Created by dashwang on 11/23/17.
 */
public class Wizard {

	private final String name;

	public Wizard(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
