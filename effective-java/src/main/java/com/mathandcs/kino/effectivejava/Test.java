package com.mathandcs.kino.effectivejava;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangdongxu on 4/14/17.
 */
public class Test {
    public static void main(String[] args) {
        Map<String, Double> m = new HashMap<>();

        m.put(String.valueOf(1), 2.0);
        m.put(String.valueOf(2), 3.0);

        String result = "";

        for (Map.Entry<String, Double> entry : m.entrySet()) {
            String key = entry.getKey();
            Double value = entry.getValue();

            result += String.format(";%s:%f", key, value);
        }

        System.out.println(result);
    }
}
