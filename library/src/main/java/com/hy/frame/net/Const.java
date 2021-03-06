package com.hy.frame.net;

public class Const {

    public static final String HEAD_KEY_RESPONSE_CODE = "ResponseCode";

    public static final String HEAD_KEY_RESPONSE_MESSAGE = "ResponseMessage";

    public static final String HEAD_KEY_ACCEPT = "Accept";

    public static final String HEAD_KEY_ACCEPT_ENCODING = "Accept-Encoding";

    public static final String HEAD_VALUE_ACCEPT_ENCODING = "gzip, deflate";// no sdch

    public static final String HEAD_KEY_ACCEPT_LANGUAGE = "Accept-Language";

    public static final String HEAD_KEY_CONTENT_TYPE = "Content-Type";

    public static final String HEAD_KEY_CONTENT_LENGTH = "Content-Length";

    public static final String HEAD_KEY_CONTENT_ENCODING = "Content-Encoding";

    public static final String HEAD_KEY_CONTENT_RANGE = "Content-Range";

    public static final String HEAD_KEY_CACHE_CONTROL = "Cache-Control";

    public static final String HEAD_KEY_CONNECTION = "Connection";

    public static final String HEAD_VALUE_CONNECTION_KEEP_ALIVE = "keep-alive";

    public static final String HEAD_VALUE_CONNECTION_CLOSE = "close";

    public static final String HEAD_KEY_DATE = "Date";

    public static final String HEAD_KEY_EXPIRES = "Expires";

    public static final String HEAD_KEY_E_TAG = "ETag";

    public static final String HEAD_KEY_PRAGMA = "Pragma";

    public static final String HEAD_KEY_IF_MODIFIED_SINCE = "If-Modified-Since";

    public static final String HEAD_KEY_IF_NONE_MATCH = "If-None-Match";

    public static final String HEAD_KEY_LAST_MODIFIED = "Last-Modified";

    public static final String HEAD_KEY_LOCATION = "Location";

    public static final String HEAD_KEY_USER_AGENT = "User-Agent";

    public static final String HEAD_KEY_COOKIE = "Cookie";

    public static final String HEAD_KEY_COOKIE2 = "Cookie2";

    public static final String HEAD_KEY_SET_COOKIE = "Set-Cookie";

    public static final String HEAD_KEY_SET_COOKIE2 = "Set-Cookie2";

    public static final String HEAD_ACCEPT_STRING = "text/html,application/xhtml+xml,application/xml;*/*;q=0.9";

    public static final String HEAD_ACCEPT_IMAGE = "image/*,*/*;q=1";

    public static final String HEAD_ACCEPT_JSON = "application/json;q=1";

    public static final String HEAD_ACCEPT_FILE = "*/*";

    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_STRING = "application/x-www-form-urlencoded";

    public static final int ACCEPT_TYPE_JSON = 0;
    public static final int ACCEPT_TYPE_JSONARRAY = 1;
    public static final int ACCEPT_TYPE_STRING = 2;
    public static final int ACCEPT_TYPE_FILE = 3;

    public static final int TIMEOUT_CONNECT = 15;
    public static final int TIMEOUT_READ = 30;
    public static final int TIMEOUT_WRITE = 30;
}