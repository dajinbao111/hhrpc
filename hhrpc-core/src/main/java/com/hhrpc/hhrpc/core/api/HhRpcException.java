package com.hhrpc.hhrpc.core.api;

public class HhRpcException extends RuntimeException {

    private String errorCode;

    // X 技术类异常; Y 业务类型异常; Z 未知异常

    public static final String METHOD_NOT_FOUND = "X001_method_not_found";
    public static final String HTTP_INVOKER_TIMEOUT = "X002_http_invoker_timeout";
    public static final String UNKNOWN = "Z001_unknow";

    public HhRpcException() {
    }

    public HhRpcException(String message) {
        super(message);
    }

    public HhRpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public HhRpcException(Throwable cause, String errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public HhRpcException(Throwable cause) {
        super(cause);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
