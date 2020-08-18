package com.mathandcs.kino.effectivejava.agile.proxy;

/**
 * Created by dashwang on 11/23/17.
 */
public class IvoryTower implements WizardTower {
    public void enter(Wizard wizard){
		System.out.println(wizard + " enters the tower.");
	}
}
