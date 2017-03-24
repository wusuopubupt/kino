package com.mathandcs.kino.abacus.streaming.runtime.utils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.ClosureSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import com.twitter.chill.java.ArraysAsListSerializer;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import de.javakaffee.kryoserializers.*;
import de.javakaffee.kryoserializers.guava.*;
import org.objenesis.strategy.StdInstantiatorStrategy;

import java.lang.invoke.SerializedLambda;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * ref: 1. https://www.javatips.net/api/jetstream-master/jetstreamcore/src/main/java/com/ebay/jetstream/util/offheap/serializer/KryoSerializer.java
 * 2. https://www.javatips.net/api/Dempsy-master/lib-dempsyimpl/src/main/java/com/nokia/dempsy/serialization/kryo/KryoSerializer.java
 * 3. https://github.com/EsotericSoftware/kryo#thread-safety
 */
public class Serializer {

    private static final int BUFFER_SIZE = 4096 * 5;
    private static final String BUFFER_OVERFLOW_EXCEPTION_MESSAGE = "Buffer overflow";

    static class KryoThreadLocalContext {

        private Kryo kryo;
        private Input input;
        private Output output;
        private int outputBufferSize = BUFFER_SIZE;

        public KryoThreadLocalContext(Kryo kryo, Input input, Output output) {
            this.kryo = kryo;
            this.input = input;
            this.output = output;
        }

        private Kryo getKryo() {
            return kryo;
        }

        public Input getInput() {
            return input;
        }

        public Output getOutput() {
            return output;
        }

        private Output resizeOutputBuffer() {
            this.outputBufferSize *= 2;
            this.output = new Output(outputBufferSize);
            return this.output;
        }
    }

    // must be thread safely, see: https://github.com/EsotericSoftware/kryo#thread-safety
    private static final ThreadLocal<KryoThreadLocalContext> kryoThreadLocalContext = ThreadLocal
            .withInitial(() -> {
                Kryo kryo = createKryoInstance();
                Input input = new Input();
                Output output = new Output(BUFFER_SIZE);
                return new KryoThreadLocalContext(kryo, input, output);
            });


    static class KryoConfigSerializer extends com.esotericsoftware.kryo.Serializer<Config> {

        @Override
        public void write(Kryo kryo, Output output, Config config) {
            String renderResult = config.root().render(ConfigRenderOptions.concise());
            kryo.writeObjectOrNull(output, renderResult, String.class);
        }

        @Override
        public Config read(Kryo kryo, Input input, Class<Config> type) {
            String readResult = kryo.readObject(input, String.class);
            return ConfigFactory.parseString(readResult);
        }
    }

    static class Tuple2<A, B> {

        private A a;
        private B b;

        public Tuple2() {
        }

        public Tuple2(A a, B b) {
            this.a = a;
            this.b = b;
        }

        public void setA(A a) {
            this.a = a;
        }

        public void setB(B b) {
            this.b = b;
        }

        public A getA() {
            return a;
        }

        public B getB() {
            return b;
        }
    }

    static class KryoArrayBlockingQueueSerializer extends com.esotericsoftware.kryo.Serializer<ArrayBlockingQueue> {

        @Override
        public void write(Kryo kryo, Output output, ArrayBlockingQueue queue) {
            Integer queueCapacity = queue.size() + queue.remainingCapacity();
            LinkedList list = new LinkedList(Arrays.asList(queue.toArray()));
            Tuple2<Integer, LinkedList> tuple2 = new Tuple2<>(queueCapacity, list);
            kryo.writeObjectOrNull(output, tuple2, Tuple2.class);
        }

        @Override
        public ArrayBlockingQueue read(Kryo kryo, Input input, Class<ArrayBlockingQueue> type) {
            Tuple2<Integer, LinkedList> tuple2 = kryo.readObject(input, Tuple2.class);
            ArrayBlockingQueue arrayBlockingQueue = new ArrayBlockingQueue(tuple2.a);
            tuple2.b.stream().forEach(
                    obj -> {
                        arrayBlockingQueue.add(obj);
                    }
            );
            return arrayBlockingQueue;
        }
    }

    static class LinkedBlockingQueueSerializer extends com.esotericsoftware.kryo.Serializer<LinkedBlockingQueue> {

        @Override
        public void write(Kryo kryo, Output output, LinkedBlockingQueue queue) {
            Integer queueCapacity = queue.size() + queue.remainingCapacity();
            LinkedList list = new LinkedList(Arrays.asList(queue.toArray()));
            Tuple2<Integer, LinkedList> tuple2 = new Tuple2<>(queueCapacity, list);
            kryo.writeObjectOrNull(output, tuple2, Tuple2.class);
        }

        @Override
        public LinkedBlockingQueue read(Kryo kryo, Input input, Class<LinkedBlockingQueue> type) {
            Tuple2<Integer, LinkedList> tuple2 = kryo.readObject(input, Tuple2.class);
            LinkedBlockingQueue linkedBlockingQueue = new LinkedBlockingQueue(tuple2.a);
            tuple2.b.stream().forEach(
                    obj -> {
                        linkedBlockingQueue.add(obj);
                    }
            );
            return linkedBlockingQueue;
        }
    }

    private static Kryo createKryoInstance() {
        Kryo kryo = new Kryo();

        kryo.setClassLoader(Thread.currentThread().getContextClassLoader());
        kryo.setReferences(true);
        //kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));

        kryo.register(Arrays.asList("").getClass(), new ArraysAsListSerializer());
        kryo.register(Collections.EMPTY_LIST.getClass(), new DefaultSerializers.CollectionsEmptyListSerializer());
        kryo.register(Collections.EMPTY_MAP.getClass(), new DefaultSerializers.CollectionsEmptyMapSerializer());
        kryo.register(Collections.EMPTY_SET.getClass(), new DefaultSerializers.CollectionsEmptySetSerializer());
        kryo.register(Collections.singletonList("").getClass(),
                new DefaultSerializers.CollectionsSingletonListSerializer());
        kryo.register(Collections.singleton("").getClass(), new DefaultSerializers.CollectionsSingletonSetSerializer());
        kryo.register(Collections.singletonMap("", "").getClass(),
                new DefaultSerializers.CollectionsSingletonMapSerializer());
        kryo.register(Config.class, new KryoConfigSerializer());
        try {
            kryo.register(Class.forName("com.typesafe.config.impl.SimpleConfig"),
                    new KryoConfigSerializer());
        } catch (ClassNotFoundException e) {
            // do nothing.
        }
        kryo.register(Object[].class);
        kryo.register(java.lang.Class.class);
        kryo.register(SerializedLambda.class);
        kryo.register(ClosureSerializer.Closure.class, new ClosureSerializer());
        kryo.register(ArrayBlockingQueue.class, new KryoArrayBlockingQueueSerializer());
        kryo.register(LinkedBlockingQueue.class, new LinkedBlockingQueueSerializer());

        ArrayListMultimapSerializer.registerSerializers(kryo);
        HashMultimapSerializer.registerSerializers(kryo);
        ImmutableListSerializer.registerSerializers(kryo);
        ImmutableMapSerializer.registerSerializers(kryo);
        ImmutableMultimapSerializer.registerSerializers(kryo);
        ImmutableSetSerializer.registerSerializers(kryo);
        ImmutableSortedSetSerializer.registerSerializers(kryo);
        LinkedHashMultimapSerializer.registerSerializers(kryo);
        LinkedListMultimapSerializer.registerSerializers(kryo);
        ReverseListSerializer.registerSerializers(kryo);
        TreeMultimapSerializer.registerSerializers(kryo);
        UnmodifiableNavigableSetSerializer.registerSerializers(kryo);

        UnmodifiableCollectionsSerializer.registerSerializers(kryo);
        SynchronizedCollectionsSerializer.registerSerializers(kryo);

        return kryo;
    }

    /**
     * Get Kryo instance
     *
     * @return Kryo instance for current thread
     */
    public static Kryo getInstance() {
        return kryoThreadLocalContext.get().getKryo();
    }

    /**
     * Serialize obj to byte[]
     *
     * @param obj the input object
     * @param <T> the input object's type
     * @return byte[]
     */
    public static <T> byte[] encode(T obj) {
        Output output = kryoThreadLocalContext.get().getOutput();
        while (true) {
            output.clear();
            try {
                Kryo kryo = getInstance();
                kryo.writeClassAndObject(output, obj);
                output.flush();
                break;
            } catch (KryoException e) {
                // need resize
                if (e.getMessage() != null && e.getMessage()
                        .startsWith(BUFFER_OVERFLOW_EXCEPTION_MESSAGE)) {
                    output = kryoThreadLocalContext.get().resizeOutputBuffer();
                } else {
                    throw e;
                }
            }
        }
        return output.toBytes();
    }

    /**
     * deserialize byte[] to object
     *
     * @param byteArray byte[]
     * @param <T>       object's type
     * @return object
     */
    public static <T> T decode(byte[] byteArray) {
        Input input = kryoThreadLocalContext.get().getInput();
        input.setBuffer(byteArray);
        Kryo kryo = getInstance();
        return (T) kryo.readClassAndObject(input);
    }

    /**
     * clean up thread local
     */
    public static void clean() {
        kryoThreadLocalContext.remove();
    }

}