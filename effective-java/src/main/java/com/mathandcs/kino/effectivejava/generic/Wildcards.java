package com.mathandcs.kino.effectivejava.generic;

import java.util.*;

/**
 * Created by dashwang on 8/28/17.
 */
public class Wildcards {

    public static void printListV1(List<Object> list) {
        for (Object obj : list) {
            System.out.print(obj + " ");
        }
        System.out.println();
    }

    // 无界通配符(unbounded wildcards)
    // List<Integer> 是 List<? extends Object>的子类型, 但不是List<Object>的子类型
    // 当泛型方法的参数是Object类型,且方法内部实现依赖泛型参数T的话,就用?通配符
    public static void printListV2(List<? extends Object> list) {
        for (Object obj : list) {
            System.out.print(obj + " ");
        }
        System.out.println();
    }

    // 上界通配符和下界通配符
    // T extends Comparable 代表Comprable类型及其子类
    // ? super T 代表T和T的父类
    public static <T extends Comparable<? super T>> void sort(List<T> list) {
        Object[] a = list.toArray();
        Arrays.sort(a);
        ListIterator<T> i = list.listIterator();
        for (int j=0; j<a.length; j++) {
            i.next();
            i.set((T)a[j]);
        }
    }

    public static void main(String[] args) {
        List<Integer> li = Arrays.asList(1, 2, 3);
        List<String> ls = Arrays.asList("one", "two", "three");


        // 编译错误: InCompatible types, Required Object, found Integer
        //List<Object> lo = Arrays.asList(1, 2, 3);

        // 编译错误: List<Object> cannot be apply to List<Integer>
        // printListV1(li);
        //printListV1(ls);


        printListV2(li);
        printListV2(ls);
    }
}
