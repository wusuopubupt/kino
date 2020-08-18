package com.mathandcs.kino.effectivejava.agile.composite;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dashwang on 11/24/17.
 */
public class Employee {
    private String name;
    private String dept;
    private int salary;
    // 组合
    private List<Employee> subordinates;

    public Employee(String name, String dept, int salary) {
        this.name = name;
        this.dept = dept;
        this.salary = salary;
        this.subordinates = new ArrayList<>();
    }

    public void addSubordinate(Employee employee) {
        subordinates.add(employee);
    }

    public void removeSubordinate(Employee employee) {
        subordinates.remove(employee);
    }

    public List<Employee> getSubordinates() {
        return subordinates;
    }

    @Override
    public String toString() {
        return String.format("%s(%s, %d)", dept, name, salary);
    }
}
