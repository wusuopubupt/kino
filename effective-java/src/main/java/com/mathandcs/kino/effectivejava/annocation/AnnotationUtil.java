package com.mathandcs.kino.effectivejava.annocation;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dashwang on 5/19/17.
 */
public class AnnotationUtil {

    public static Map<String, Object> getConfig(Class<?> clazz) {
        Map<String, Object> map = new HashMap<>();

        Field fields[] = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(IntType.class)) {
                IntType intVal = field.getAnnotation(IntType.class);

                map.put(field.getName(),
                        new FloatType(intVal.defaultValue(), intVal.min() * 1.0f, intVal.max() * 1.0f, intVal.openClose()));
            }
        }

        return map;
    }

}