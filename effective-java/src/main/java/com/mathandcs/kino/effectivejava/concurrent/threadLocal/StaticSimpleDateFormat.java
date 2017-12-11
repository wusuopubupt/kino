package com.mathandcs.kino.effectivejava.concurrent.threadLocal;

import lombok.Getter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by dashwang on 12/8/17.
 */
public class StaticSimpleDateFormat{

    @Getter
    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

}
