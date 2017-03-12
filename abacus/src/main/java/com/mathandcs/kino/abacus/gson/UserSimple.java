package com.mathandcs.kino.abacus.gson;

import com.google.gson.annotations.Expose;
import lombok.Data;

/**
 * Created by wangdongxu on 3/7/17.
 */
@Data
public class UserSimple {
    //@Expose()
    transient String  name; // equals serialize & deserialize

    @Expose(serialize = false, deserialize = false)
    String email; // equals neither serialize nor deserialize

    @Expose(serialize = false)
    int age; // equals only deserialize

    @Expose(deserialize = false)
    boolean isDeveloper; // equals only serialize
}