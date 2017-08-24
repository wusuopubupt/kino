package com.mathandcs.kino.effectivejava.cache;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by dashwang on 8/24/17.
 */
public class BloomFilterTest {
    static Random r = new Random();

    @Test
    public void testConstructorCNK() throws Exception {
        System.out.println("BloomFilter(n, k, c)");

        for (int i = 0; i < 10000; i++) {
            int n = r.nextInt(10000) + 1;
            int k = r.nextInt(20) + 1;
            double c = r.nextInt(20) + 1;
            BloomFilter bf = new BloomFilter(n, k, c);
            assertEquals(bf.getK(), k);
            assertEquals(bf.getBitsPerElement(), c, 0);
            assertEquals(bf.getExpectedNumberOfFilterElements(), n);
            assertEquals(bf.getBitSetSize(), c * n, 0);
        }
    }

    /**
     * Test of add method, of class BloomFilter.
     *
     * @throws Exception
     */
    @Test
    public void testAdd() throws Exception {
        System.out.println("add");
        BloomFilter instance = new BloomFilter(0.1, 100);

        for (int i = 0; i < 100; i++) {
            String val = UUID.randomUUID().toString();
            instance.add(val);
            assert (instance.contains(val));
        }
    }

    /**
     * Test of addAll method, of class BloomFilter.
     *
     * @throws Exception
     */
    @Test
    public void testAddAll() throws Exception {
        System.out.println("addAll");
        List<String> v = new ArrayList<String>();
        BloomFilter instance = new BloomFilter(0.1, 100);

        for (int i = 0; i < 100; i++)
            v.add(UUID.randomUUID().toString());

        instance.addAll(v);

        for (int i = 0; i < 100; i++)
            assert (instance.contains(v.get(i)));
    }

    /**
     * Test of contains method, of class BloomFilter.
     *
     * @throws Exception
     */
    @Test
    public void testContains() throws Exception {
        System.out.println("contains");
        BloomFilter instance = new BloomFilter(0.1, 10);

        for (int i = 0; i < 10; i++) {
            instance.add(Integer.toBinaryString(i));
            assert (instance.contains(Integer.toBinaryString(i)));
        }

        assertFalse(instance.contains(UUID.randomUUID().toString()));
    }

    /**
     * Test of containsAll method, of class BloomFilter.
     *
     * @throws Exception
     */
    @Test
    public void testContainsAll() throws Exception {
        System.out.println("containsAll");
        List<String> v = new ArrayList<String>();
        BloomFilter instance = new BloomFilter(0.1, 100);

        for (int i = 0; i < 100; i++) {
            v.add(UUID.randomUUID().toString());
            instance.add(v.get(i));
        }

        assert (instance.containsAll(v));
    }

    /**
     * Test for correct k
     **/
    @Test
    public void testGetK() {
        // Numbers are from http://pages.cs.wisc.edu/~cao/papers/summary-cache/node8.html
        System.out.println("testGetK");
        BloomFilter instance = null;

        // k = ceil(-log_2(fpp))
        double fpp = 0.1;

        instance = new BloomFilter(fpp, 100);
        assertEquals((int) Math.ceil(-Math.log(fpp) / Math.log(2)), instance.getK());

        instance = new BloomFilter(fpp, 100000);
        assertEquals((int) Math.ceil(-Math.log(fpp) / Math.log(2)), instance.getK());


        fpp = 0.5;

        instance = new BloomFilter(fpp, 100);
        assertEquals((int) Math.ceil(-Math.log(fpp) / Math.log(2)), instance.getK());

        instance = new BloomFilter(fpp, 100000);
        assertEquals((int) Math.ceil(-Math.log(fpp) / Math.log(2)), instance.getK());
    }

    /**
     * Test of contains method, of class BloomFilter.
     */
    @Test
    public void testContainsGenericType() {
        System.out.println("contains");
        int items = 100;
        BloomFilter<String> instance = new BloomFilter(0.01, items);

        for (int i = 0; i < items; i++) {
            String s = UUID.randomUUID().toString();
            instance.add(s);
            assertTrue(instance.contains(s));
        }
    }

    /**
     * Test of contains method, of class BloomFilter.
     */
    @Test
    public void testContainsByteArr() {
        System.out.println("contains");

        int items = 100;
        BloomFilter instance = new BloomFilter(0.01, items);

        for (int i = 0; i < items; i++) {
            byte[] bytes = new byte[500];
            r.nextBytes(bytes);
            instance.add(bytes);
            assertTrue(instance.contains(bytes));
        }
    }

}