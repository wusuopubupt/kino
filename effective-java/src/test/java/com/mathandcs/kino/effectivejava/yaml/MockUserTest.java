package com.mathandcs.kino.effectivejava.yaml;

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
    public void testYamlWriter() throws Exception {

        MockUser user = new MockUser(2);

        MockUser.Student stu1 = new MockUser.Student();
        stu1.name = "Tom";
        stu1.asset = -1.0;
        user.students.add(stu1);

        MockUser.Student stu2 = new MockUser.Student();
        stu2.name = "Jack";
        stu2.asset = -3.4e+10;
        user.students.add(stu2);

        YamlWriter writer = new YamlWriter(new FileWriter("/tmp/output.yml"));
        writer.getConfig().setClassTag("str", String.class);
        writer.write(user);
        writer.close();

    }

} 
