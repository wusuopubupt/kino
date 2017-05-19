package com.mathandcs.kino.effectivejava.annocation;

/**
 * Created by wangdongxu on 5/18/17.
 */
public class FloatType {
    float value;
    float defaultValue;
    Restriction restriction;

    public static class Restriction {
        float min;
        float max;
        String openClose;

        public Restriction(float min, float max, String openClose) {
            this.min = min;
            this.max = max;
            this.openClose = openClose;
        }
    }

    public FloatType(float value, float min, float max, String openClose) {
        this.value = value;
        this.defaultValue = value;
        this.restriction = new Restriction(min, max, openClose);
    }

}
