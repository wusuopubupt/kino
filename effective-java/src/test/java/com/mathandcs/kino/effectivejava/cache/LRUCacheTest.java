package com.mathandcs.kino.effectivejava.cache;

import junit.framework.Assert;
import org.junit.Test;

/**
 * Created by dashwang on 5/12/17.
 */
public class LRUCacheTest {

    @Test
    public void testGet() {
        LRUCache cache = new LRUCache(5);
        cache.set(1, 2);
        cache.set(2, 4);
        cache.set(3, 6);
        cache.set(4, 8);
        cache.set(5, 10);
        cache.set(1, -1);

        Assert.assertEquals(-1, cache.get(1));

        Assert.assertEquals(-1, cache.head.val);
        Assert.assertEquals(10, cache.head.next.val);
        Assert.assertEquals(8, cache.head.next.next.val);
        Assert.assertEquals(6, cache.head.next.next.next.val);
        Assert.assertEquals(4, cache.tail.val);

    }

}