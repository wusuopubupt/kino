package com.mathandcs.kino.effectivejava.serde;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Assert;
import org.junit.Test;

public class KryoSerializerTest {

    public static final int LOOP_COUNT = 10000;

    private Tuple t = createTuple();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Tuple {
        Long             a;
        List<TimeRecord> b;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Record {
        public int v;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class TimeRecord implements Serializable {
        public Record record;
        public Long   triggerTime;
    }

    @Test
    public void testSerde() {
        Tuple t = createTuple();
        byte[] bytes = KryoSerializer.writeToByteArray(t);
        Assert.assertEquals(KryoSerializer.readFromByteArray(bytes), t);
    }

    private Tuple createTuple() {
        // use ArrayList will raise java.util.ConcurrentModificationException
        Tuple t = new Tuple(1L, new CopyOnWriteArrayList<>());
        t.b.add(new TimeRecord(new Record(1), System.currentTimeMillis()));
        t.b.add(new TimeRecord(new Record(2), System.currentTimeMillis()));
        t.b.add(new TimeRecord(new Record(2), null));
        t.b.add(new TimeRecord(null, null));
        return t;
    }

    private void modifyTuple() {
        if (t.b.size() > 2) {
            t.b.remove(0);
            return;
        }
        t.b.add(new TimeRecord(null, System.currentTimeMillis()));
    }

    private void runSer() {
        Tuple copied = new Tuple(t.a, t.b);
        Assert.assertTrue(copied.b.size() > 0);
        byte[] bytes = KryoSerializer.writeToByteArray(copied);
        Assert.assertTrue(bytes.length > 0);
    }

    private Thread newSerThread() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (i < LOOP_COUNT) {
                    runSer();
                    i++;

                    if (i % 100 == 0) {
                        modifyTuple();
                    }
                }
            }
        });
    }

    private Thread newModifyThread() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (i < LOOP_COUNT) {
                    modifyTuple();
                    i++;
                }
            }
        });
    }

    @Test
    public void testMultiThreadsSerde() throws Exception {

        Thread t1 = newSerThread();
        t1.start();

        Thread t3 = newModifyThread();
        t3.start();

        Thread t2 = newSerThread();
        t2.start();

        t1.join();
        t2.join();
        t3.join();

        // No exception occurred.
        Assert.assertEquals(1, 1);
    }

}