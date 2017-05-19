package com.mathandcs.kino.effectivejava.annocation;

import java.lang.annotation.*;

/**
 * Created by dashwang on 5/18/17.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IntType {

    public String key() default "";

    public int defaultValue() default -1;

    public String valueType() default "int";

    public int max();

    public int min();

    public String openClose() default "OO";

}
