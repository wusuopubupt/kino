package com.mathandcs.kino.effectivejava.map;

import java.util.*;

/**
 * Created by dashwang on 8/14/17.
 * <p>
 * benchmark for HashMap, TreeMap and LinkedHashMap
 */
public class MapBenchmark {

    public HashMap<String, String> hashMap = new HashMap<>();
    public TreeMap<String, String> treeMap = new TreeMap<>();
    public LinkedHashMap<String, String> linkedMap = new LinkedHashMap<>();

    public static ArrayList<String> LIST = new ArrayList<>();
    public static int REPEATS = 1000;

    static {
        for (int i = 0; i < 10000; i++) {
            LIST.add(Integer.toString(i, 16));
        }
    }

    private static void get(Map<String, String> map) {
        for (String s : LIST) {
            map.get(s);
        }
    }

    private static void put(Map<String, String> map) {
        map.clear();
        for (String s : LIST) {
            map.put(s, s);
        }
    }

    private static void measureTimeToPut(Map<String, String> map, String setName, int repeats) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < repeats; i++) {
            put(map);
        }
        long finish = System.currentTimeMillis();
        System.out.println("Time to put() " + (repeats * map.size()) + " entries in a "
                + setName + ": " + (finish - start));
    }

    private static void measureTimeToGet(Map<String, String> map, String setName, int repeats) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < repeats; i++) {
            get(map);
        }
        long finish = System.currentTimeMillis();
        System.out.println("Time to get() " + (repeats * map.size()) + " entries in a "
                + setName + ": " + (finish - start));
    }

    public static void main(String[] args) {
        MapBenchmark mapBenchmark = new MapBenchmark();
        System.out.println("Start to put elements");

        measureTimeToPut(mapBenchmark.hashMap, "HashMap", REPEATS);
        measureTimeToPut(mapBenchmark.treeMap, "TreeMap", REPEATS);
        measureTimeToPut(mapBenchmark.linkedMap, "LinkedMap", REPEATS);

        System.out.println("\n\nStar to get elements");

        measureTimeToGet(mapBenchmark.hashMap, "HashMap", REPEATS);
        measureTimeToGet(mapBenchmark.treeMap, "TreeMap", REPEATS);
        measureTimeToGet(mapBenchmark.linkedMap, "LinkedMap", REPEATS);
    }
}
