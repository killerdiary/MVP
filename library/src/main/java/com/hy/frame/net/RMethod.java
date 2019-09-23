package com.hy.frame.net;

/**
 * title RequestMethod
 * author heyan
 * time 19-8-23 上午10:22
 * desc 无
 */
public enum RMethod {

    GET("GET"),

    POST("POST"),

    PUT("PUT"),

    DELETE("DELETE"),

    HEAD("HEAD"),

    OPTIONS("OPTIONS"),

    TRACE("TRACE"),

    PATCH("PATCH");

    private final String value;

    RMethod(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

}
