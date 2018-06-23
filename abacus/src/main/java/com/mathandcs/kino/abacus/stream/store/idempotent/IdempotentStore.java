/**
 * Alipay.com Inc. Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.mathandcs.kino.abacus.stream.store.idempotent;

/**
 *
 * @author dash wang
 * @date 2018-06-22
 */
public interface IdempotentStore {

    /**
     *
     * @param uniqueKey
     * @param data
     * @param version
     * @param expire
     * @return
     */
    IdempotentResult put(Object uniqueKey, Object data, int version, int expire);

    /**
     *
     * @param uniqueKey
     * @return
     */
    IdempotentResult delete(Object uniqueKey);

    /**
     *
     * @param uniqueKey
     * @return
     */
    IdempotentResult get(Object uniqueKey);
}