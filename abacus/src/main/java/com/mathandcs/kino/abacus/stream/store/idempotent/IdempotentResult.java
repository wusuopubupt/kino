/**
 * Alipay.com Inc. Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.mathandcs.kino.abacus.stream.store.idempotent;

import lombok.Data;

/**
 *
 * @author dash wang
 */
@Data
public class IdempotentResult {
    private IdempotentResultEnum resultEnum;
    private Object data;
}