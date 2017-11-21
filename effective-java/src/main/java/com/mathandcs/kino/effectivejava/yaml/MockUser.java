package com.mathandcs.kino.effectivejava.yaml;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dashwang on 3/8/17.
 */
public class MockUser {

    static class Student {
        public String name;
        public String sex;
        public int age;
        public double asset;
    }

    public List<Student> students;
    public int number;

    public MockUser(int n){
        number = n;
        students = new ArrayList<Student>();
    }

    public MockUser(){}
}
