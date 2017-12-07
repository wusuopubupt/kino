package com.mathandcs.kino.effectivejava.serviceloader;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Created by dashwang on 12/7/17.
 */
public class Loader {

    public static <S> List<S> loadService(Class<S> s) {
        ServiceLoader<S> serviceLoader = ServiceLoader.load(s);
        Iterator<S> serviceIter = serviceLoader.iterator();

        List<S> services = new LinkedList<>();
        while (serviceIter.hasNext()) {
            S service = serviceIter.next();
            services.add(service);
        }
        return services;
    }

    public static void main(String[] args) throws Exception{
        List<MyPrintService> myPrintServices = loadService(MyPrintService.class);
        for(MyPrintService myPrintService : myPrintServices) {
            // 使用service loader创建出来的对象, 用于无状态的场景
            myPrintService.print();
            // foo
            // bar
        }

        for(MyPrintService myPrintService : myPrintServices) {
            // 通过反射创建一个新对象, 用于有状态的场景
            myPrintService.getClass().newInstance().print();
            // foo
            // bar
        }
    }
}
