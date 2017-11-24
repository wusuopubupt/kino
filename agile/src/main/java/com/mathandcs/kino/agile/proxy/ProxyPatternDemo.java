package com.mathandcs.kino.agile.proxy;

/**
 * Created by dashwang on 11/23/17.
 * 
 * ref: https://github.com/iluwatar/java-design-patterns/blob/master/proxy/README.md
 * 
 * Imagine a tower where the local wizards go to study their spells. 
 * The ivory tower can only be accessed through a proxy which ensures that only the first three wizards can enter. 
 * Here the proxy represents the functionality of the tower and adds access control to it.
 */
public class ProxyPatternDemo {
	public static void main(String[] args) {
		WizardTowerProxy proxy = new WizardTowerProxy(new IvoryTower());
		proxy.enter(new Wizard("Red wizard")); // Red wizard enters the tower.
		proxy.enter(new Wizard("White wizard")); // White wizard enters the tower.
		proxy.enter(new Wizard("Black wizard")); // Black wizard enters the tower.
		proxy.enter(new Wizard("Green wizard")); // Green wizard is not allowed to enter!
		proxy.enter(new Wizard("Brown wizard")); // Brown wizard is not allowed to enter!
	}
}
