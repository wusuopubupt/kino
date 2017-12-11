package com.mathandcs.kino.effectivejava.concurrent.threadLocal;

import lombok.Getter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by dashwang on 12/8/17.
 * <p>
 * ref: https://www.cnblogs.com/peida/archive/2013/05/31/3070790.html
 */
public class ThreadLocalSimpleDateFormat {

    @Getter
    private static final ThreadLocal<DateFormat> df = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

}
