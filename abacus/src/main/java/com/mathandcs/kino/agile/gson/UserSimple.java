package com.mathandcs.kino.agile.gson;

import com.google.gson.annotations.Expose;

/**
 * Created by wangdongxu on 3/7/17.
 */
public class UserSimple {
    @Expose()
    String name; // equals serialize & deserialize

    @Expose(serialize = false, deserialize = false)
    String email; // equals neither serialize nor deserialize

    @Expose(serialize = false)
    int age; // equals only deserialize

    @Expose(deserialize = false)
    boolean isDeveloper; // equals only serialize
}