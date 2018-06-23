/**
 * Alipay.com Inc. Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.mathandcs.kino.abacus.stream.store.idempotent;

import lombok.Data;

/**
 * @author dash wang
 */
@Data
public enum IdempotentResultEnum {

    NULL_IDEMPOTENT_KEY(1001, "idempotent key is null"),

    IDEMPOTENT_SUCCESS(1002,  "idempotent operation succeeded"),

    ROLLBACK_IDEMPOTENT_SUCCESS(1003, "delete idempotent key succeeded"),

    IDEMPOTENT_FAILED(1004, "idempotent operation failed"),

    ROLLBACK_IDEMPOTENT_ERROR(1005, "rollback idempotent error"),

    UNKNOWN_IDEMPOTENT_RESULT(1006, "unknown error");

    private int    code;
    private String desc;

    private IdempotentResultEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}