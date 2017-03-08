package com.mathandcs.kino.agile.yaml;

import com.esotericsoftware.yamlbeans.YamlWriter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileWriter;

/**
 * MockUser Tester.
 *
 * @author wangdongxu
 * @version 1.0
 * @since <pre>Mar 8, 2017</pre>
 */
public class MockUserTest {

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    @Test
    public void testYamlWriter() throws Exception{
        MockUser user = new MockUser();
        user.name = "";
        user.sex = "man";

        YamlWriter writer = new YamlWriter(new FileWriter("/tmp/output.yml"));
        writer.getConfig().setClassTag("str", String.class);
        writer.write(user);
        writer.close();

    }

} 
