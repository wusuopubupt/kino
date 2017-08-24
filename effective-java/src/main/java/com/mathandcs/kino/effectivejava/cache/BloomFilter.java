package com.mathandcs.kino.effectivejava.cache;

import lombok.Getter;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;
import java.util.Collection;

/**
 * Created by dashwang on 8/24/17.
 *
 * usage: see com.mathandcs.kino.effectivejava.cache.BloomFilterTest
 */
public class BloomFilter<E> implements Serializable {
    @Getter
    private BitSet bitSet;
    @Getter
    private int bitSetSize;
    @Getter
    private double bitsPerElement;
    @Getter
    private int expectedNumberOfFilterElements;
    @Getter
    private int numOfAddedElements;
    @Getter
    private int k;

    static final Charset charset = Charset.forName("UTF-8");
    static final String hashName = "MD5";
    static final MessageDigest digestFunction;

    static {
        MessageDigest temp;
        try {
            temp = java.security.MessageDigest.getInstance(hashName);
        } catch (NoSuchAlgorithmException e) {
            temp = null;
        }
        digestFunction = temp;
    }

    /**
     * @param n : the expected number of elements for the filter
     * @param k : the number of hash functions used
     * @param c : the number of bits used per element
     **/
    public BloomFilter(int n, int k, double c) {
        this.expectedNumberOfFilterElements = n;
        this.k = k;
        this.bitsPerElement = c;
        this.bitSetSize = (int) Math.ceil(c * n);
        this.numOfAddedElements = 0;
        this.bitSet = new BitSet(bitSetSize);
    }

    public BloomFilter(double falsePositiveProbability, int expectedNumberOfFilterElements) {
        this(expectedNumberOfFilterElements,
                (int) Math.ceil(-(Math.log(falsePositiveProbability) / Math.log(2))), // k = ceil(-log_2(fpp))
                Math.ceil(-(Math.log(falsePositiveProbability) / Math.log(2))) / Math.log(2)); // c = k / ln(2)
    }

    /**
     * ref: https://github.com/MagnusS/Java-BloomFilter/blob/master/src/com/skjegstad/utils/BloomFilter.java
     * <p>
     * Generates digests based on the contents of an array of bytes and splits the result into 4-byte int's and store them in an array. The
     * digest function is called until the required number of int's are produced. For each call to digest a salt
     * is prepended to the data. The salt is increased by 1 for each call.
     *
     * @param data   specifies input data.
     * @param hashes number of hashes/int's to produce.
     * @return array of int-sized hashes
     */
    public static int[] createHashes(byte[] data, int hashes) {
        int[] result = new int[hashes];

        int k = 0;
        byte salt = 0;
        while (k < hashes) {
            byte[] digest;
            synchronized (digestFunction) {
                digestFunction.update(salt);
                salt++;
                digest = digestFunction.digest(data);
            }

            for (int i = 0; i < digest.length / 4 && k < hashes; i++) {
                int h = 0;
                for (int j = (i * 4); j < (i * 4) + 4; j++) {
                    h <<= 8;
                    h |= ((int) digest[j]) & 0xFF;
                }
                result[k] = h;
                k++;
            }
        }
        return result;
    }

    public int getBitIndex(int hash) {
        return Math.abs(hash % bitSetSize);
    }

    public void add(E element) {
        add(element.toString().getBytes(charset));
    }

    public void add(byte[] bytes) {
        int[] hashes = createHashes(bytes, k);
        for (int hash : hashes) {
            int bitIndex = getBitIndex(hash);
            bitSet.set(bitIndex, true);
        }
        numOfAddedElements++;
    }

    public void addAll(Collection<? extends E> c) {
        for (E element : c) {
            add(element);
        }
    }

    public boolean contains(E element) {
        return contains(element.toString().getBytes());
    }

    public boolean contains(byte[] bytes) {
        int[] hashes = createHashes(bytes, k);
        for (int hash : hashes) {
            int bitIndex = getBitIndex(hash);
            if (!bitSet.get(bitIndex)) {
                return false; // absolutely false
            }
        }
        return true;  // might be true(with false positive probability)
    }

    public boolean containsAll(Collection<? extends E> c) {
        for (E element : c) {
            if (!contains(element)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BloomFilter<E> other = (BloomFilter<E>) obj;
        if (this.expectedNumberOfFilterElements != other.expectedNumberOfFilterElements) {
            return false;
        }
        if (this.k != other.k) {
            return false;
        }
        if (this.bitSetSize != other.bitSetSize) {
            return false;
        }
        if (this.bitSet != other.bitSet && (this.bitSet == null || !this.bitSet.equals(other.bitSet))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.bitSet != null ? this.bitSet.hashCode() : 0);
        hash = 61 * hash + this.expectedNumberOfFilterElements;
        hash = 61 * hash + this.bitSetSize;
        hash = 61 * hash + this.k;
        return hash;
    }
}
