package com.mathandcs.kino.effectivejava.annocation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

/**
 * Created by wangdongxu on 5/18/17.
 */
public class Test {

    @IntType(key = "intVal", defaultValue = 2, min = 0, max = 10, openClose = "OC")
    private int intVal;

    private FloatType floatVal = new FloatType(2.0F, 1.0F, 10.0F, "OO");

    public static void main(String[] args) {
        Test testConfig = new Test();

        Map<String, Object> map = AnnotationUtil.getConfig(testConfig.getClass());

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(map);
        System.out.println(json);

    }

}
