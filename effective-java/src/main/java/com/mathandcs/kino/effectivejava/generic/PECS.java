package com.mathandcs.kino.effectivejava.generic;

import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/**
 * Created by wangdongxu on 8/28/17.
 *
 * ref: http://www.cnblogs.com/en-heng/p/5041124.html
 *
 * <p>
 * PECS: Producer extends, Comsumer super
 * -- from Effective Java
 * PECS总结：
 * <p>
 * 读: 用extends, 实现了泛型的协变
 * 写: 用super, 实现了泛型的逆变
 * 既要取又要写，就不用通配符（即extends与super都不用）。
 */
public class PECS {

    private static final int COPY_THRESHOLD = 10;

    public static <T> void copy(List<? super T> dest, List<? extends T> src) {
        int srcSize = src.size();
        if (srcSize > dest.size())
            throw new IndexOutOfBoundsException("Source does not fit in dest");

        if (srcSize < COPY_THRESHOLD ||
                (src instanceof RandomAccess && dest instanceof RandomAccess)) {
            for (int i = 0; i < srcSize; i++)
                dest.set(i, src.get(i));
        } else {
            ListIterator<? super T> di = dest.listIterator();
            ListIterator<? extends T> si = src.listIterator();
            for (int i = 0; i < srcSize; i++) {
                di.next();
                di.set(si.next());
            }
        }
    }
}
