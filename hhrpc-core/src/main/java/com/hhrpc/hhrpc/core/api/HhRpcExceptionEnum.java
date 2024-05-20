package com.hhrpc.hhrpc.core.api;

import java.util.Objects;

public enum HhRpcExceptionEnum {

    X001("X001", "method_not_found"),
    X002("X002", "http_invoker_timeout"),
    Z001("Z001", "unknow");

    private final String errorCode;
    private final String errorMessage;

    HhRpcExceptionEnum(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static HhRpcExceptionEnum findHhRpcExceptionEnum(String errorCode) {
        if (Objects.isNull(errorCode)) {
            return Z001;
        }
        for (HhRpcExceptionEnum enumItem : values()) {
            if (enumItem.errorCode.equals(errorCode)) {
                return enumItem;
            }
        }
        return Z001;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
