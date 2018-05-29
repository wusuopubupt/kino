package com.mathandcs.kino.effectivejava.concurrent.threadLocal;

import java.util.HashMap;
import java.util.Map;

public class ThreadLocalContext {

    protected final static ThreadLocal<Map<Object, Object>> THREAD_CONTEXT = new MapThreadLocal();

    private ThreadLocalContext() {}

    public static void put(Object key, Object value) {
        getContextMap().put(key, value);
    }

    public static Object remove(Object key) {
        return getContextMap().remove(key);
    }

    public static void remove() {
        THREAD_CONTEXT.remove();
    }

    public static Object get(Object key) {
        return getContextMap().get(key);
    }

    public static boolean containsKey(Object key) {
        return getContextMap().containsKey(key);
    }

    private static class MapThreadLocal extends ThreadLocal<Map<Object, Object>> {
        protected Map<Object, Object> initialValue() {
            return new HashMap<Object, Object>() {

                private static final long serialVersionUID = 1111111959138292222L;

                public Object put(Object key, Object value) {
                    return super.put(key, value);
                }
            };
        }
    }

    protected static Map<Object, Object> getContextMap() {
        return THREAD_CONTEXT.get();
    }


    public static void clear() {
        getContextMap().clear();
    }
}
