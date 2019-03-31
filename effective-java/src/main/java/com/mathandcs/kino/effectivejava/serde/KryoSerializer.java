package com.mathandcs.kino.effectivejava.serde;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Kryo.DefaultInstantiatorStrategy;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import de.javakaffee.kryoserializers.ArraysAsListSerializer;
import de.javakaffee.kryoserializers.CollectionsEmptyListSerializer;
import de.javakaffee.kryoserializers.CollectionsEmptyMapSerializer;
import de.javakaffee.kryoserializers.CollectionsEmptySetSerializer;
import de.javakaffee.kryoserializers.CollectionsSingletonListSerializer;
import de.javakaffee.kryoserializers.CollectionsSingletonMapSerializer;
import de.javakaffee.kryoserializers.CollectionsSingletonSetSerializer;
import de.javakaffee.kryoserializers.SynchronizedCollectionsSerializer;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;
import de.javakaffee.kryoserializers.guava.ArrayListMultimapSerializer;
import de.javakaffee.kryoserializers.guava.HashMultimapSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableListSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableMapSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableMultimapSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableSetSerializer;
import de.javakaffee.kryoserializers.guava.ImmutableSortedSetSerializer;
import de.javakaffee.kryoserializers.guava.LinkedHashMultimapSerializer;
import de.javakaffee.kryoserializers.guava.LinkedListMultimapSerializer;
import de.javakaffee.kryoserializers.guava.ReverseListSerializer;
import de.javakaffee.kryoserializers.guava.TreeMultimapSerializer;
import de.javakaffee.kryoserializers.guava.UnmodifiableNavigableSetSerializer;
import java.util.Arrays;
import java.util.Collections;
import org.objenesis.strategy.StdInstantiatorStrategy;

/**
 * ref:
 * 1. https://www.javatips.net/api/jetstream-master/jetstreamcore/src/main/java/com/ebay/jetstream/util/offheap/serializer/KryoSerializer.java
 * 2. https://www.javatips.net/api/Dempsy-master/lib-dempsyimpl/src/main/java/com/nokia/dempsy/serialization/kryo/KryoSerializer.java
 * 3. https://github.com/EsotericSoftware/kryo#thread-safety
 */
public class KryoSerializer {

    private static final int    BUFFER_SIZE                       = 4096 * 5;
    private static final String BUFFER_OVERFLOW_EXCEPTION_MESSAGE = "Buffer overflow";

    static class KryoThreadLocalContext {
        private Kryo   kryo;
        private Input  input;
        private Output output;
        private int    outputBufferSize = BUFFER_SIZE;

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
    private static final ThreadLocal<KryoThreadLocalContext> kryoThreadLocalContext = ThreadLocal.withInitial(() -> {
        Kryo kryo = createKryoInstance();
        Input input = new Input();
        Output output = new Output(BUFFER_SIZE);
        return new KryoThreadLocalContext(kryo, input, output);
    });

    private static Kryo createKryoInstance() {
        Kryo kryo = new Kryo();

        kryo.setClassLoader(Thread.currentThread().getContextClassLoader());
        kryo.setReferences(true);
        kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));

        kryo.register(Arrays.asList("").getClass(), new ArraysAsListSerializer());
        kryo.register(Collections.EMPTY_LIST.getClass(), new CollectionsEmptyListSerializer());
        kryo.register(Collections.EMPTY_MAP.getClass(), new CollectionsEmptyMapSerializer());
        kryo.register(Collections.EMPTY_SET.getClass(), new CollectionsEmptySetSerializer());
        kryo.register(Collections.singletonList("").getClass(), new CollectionsSingletonListSerializer());
        kryo.register(Collections.singleton("").getClass(), new CollectionsSingletonSetSerializer());
        kryo.register(Collections.singletonMap("", "").getClass(), new CollectionsSingletonMapSerializer());

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
    public static <T> byte[] writeToByteArray(T obj) {
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
                if (e.getMessage() != null && e.getMessage().startsWith(BUFFER_OVERFLOW_EXCEPTION_MESSAGE)) {
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
    public static <T> T readFromByteArray(byte[] byteArray) {
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