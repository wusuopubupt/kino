package com.mathandcs.kino.effectivejava.concurrent.threadLocal;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dashwang on 12/8/17.
 */
public class SyncSimpleDateFormat {

    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String formatDate(Date date)throws ParseException {
        synchronized(df){
            return df.format(date);
        }
    }

    public static Date parse(String strDate) throws ParseException{
        synchronized(df){
            return df.parse(strDate);
        }
    }
}
