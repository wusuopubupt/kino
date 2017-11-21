package com.mathandcs.kino.agile;

import junit.framework.Assert;
import org.junit.Test;

import java.lang.reflect.Constructor;

/**
 * Created by dashwang on 10/15/16.
 */
public class SingletonTest {

    @Test
    public void testCreateSingleton() {
        Singleton instance1 = Singleton.Instance();
        Singleton instance2 = Singleton.Instance();
        Assert.assertSame(instance1, instance2);
    }

    @Test
    public void testNoPublicConstructor() throws ClassNotFoundException {
        Class singleton = Class.forName("com.mathandcs.kino.agile.Singleton");
        Constructor[] constructors = singleton.getConstructors();
        Assert.assertEquals("singleton has public constructors", 0, constructors.length);
    }
}