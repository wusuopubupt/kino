package com.mathandcs.kino.effectivejava.generic;

/**
 * Created by dashwang on 8/28/17.
 * <p>
 * 类型擦除:
 */
public class TypeErasure {

    // Java编译器会把无界通配符T替换为Object
    public static class Node1<T> {
        private T data;
        private Node1<T> next;

        public Node1(T data) {
            this.data = data;
            this.next = null;
        }

        public void setData(T data) {
            System.out.println("Node1.setData");
            this.data = data;
        }
    }

    // Java编译器会把有界通配符T extends Comparable 替换为Comparable
    public static class Node2<T extends Comparable<? super T>> {
        private T data;
        private Node2<T> next;

        public Node2(T data) {
            this.data = data;
            this.next = null;
        }
    }

    public static class MyNode1 extends Node1 {

        // 泛型擦除后, Node1的方法是setData(Object), 而MyNode1的方法是setData(Integer)
        // Java编译器自动生成的桥梁方法(Bridge method)做类型转换:
        //
        //public void setData(Object data) {
        //    setData((Integer) data);
        //}

        public MyNode1(Integer data) {
            super(data);
        }

        public void setData(Integer data) {
            System.out.println("MyNode1.setData");
            super.setData(data);
        }
    }
}
