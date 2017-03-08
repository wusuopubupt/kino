package com.mathandcs.kino.agile.gson;

import com.google.gson.Gson;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * UserSimple Tester.
 *
 * @author wangdongxu
 * @version 1.0
 * @since <pre>Mar 7, 2017</pre>
 */
public class UserSimpleTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void testGsonExpose() {
        UserSimple us = new UserSimple();
        us.setName("Tom");
		us.setEmail("Tom@gmail.com");
		us.setAge(21);
		us.setDeveloper(true);
        // 参考: http://stackoverflow.com/questions/4802887/gson-how-to-exclude-specific-fields-from-serialization-without-annotations
//        GsonBuilder builder = new GsonBuilder();
//        builder.excludeFieldsWithoutExposeAnnotation();
//        Gson gson = builder.create();

        Gson gson = new Gson();
        System.out.println(gson.toJson(us));
        Assert.assertEquals("{\"email\":\"Tom@gmail.com\",\"age\":21,\"isDeveloper\":true}", gson.toJson(us));
    }


} 
