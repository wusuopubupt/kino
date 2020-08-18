package com.mathandcs.kino.effectivejava.agile.composite;

/**
 * Created by dashwang on 11/24/17.
 */
public class CompositePatternDemo {
    public static void main(String[] args) {
        Employee CEO = new Employee("Jack Wang", "CEO", 50000);
        Employee CTO = new Employee("Stephen Curry", "Tech", 40000);
        Employee headSales = new Employee("Mike James", "Sale", 40000);

        CEO.addSubordinate(CTO);
        CEO.addSubordinate(headSales);

        Employee techManager = new Employee("LeBron James", "Tech", 30000);
        Employee programmer = new Employee("Dash Wang", "Tech", 30000);
		techManager.addSubordinate(programmer);
		CTO.addSubordinate(techManager);

		Employee saler = new Employee("Kevin Durant", "Sale", 20000);	
		headSales.addSubordinate(saler);

        System.out.println(CEO);
        for(Employee headEmployee : CEO.getSubordinates()) {
            System.out.println("  " + headEmployee);
            for(Employee employee : headEmployee.getSubordinates()) {
                System.out.println("    " + employee);
            }
        }
    }


}
