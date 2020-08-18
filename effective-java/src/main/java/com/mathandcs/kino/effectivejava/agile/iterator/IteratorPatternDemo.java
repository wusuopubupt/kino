package com.mathandcs.kino.effectivejava.agile.iterator;

/**
 * Created by dashwang on 11/24/17.
 */
public class IteratorPatternDemo {
    public static void main(String[] args) {
        NameRepository nr = new NameRepository();
        Iterator iter = nr.getIterator();
        while(iter.hasNext()) {
            String name = (String)iter.next();
            System.out.println("Name: " + name);
        }
    }
}
