package com.hy.frame.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * title 无
 * author heyan
 * time 19-8-23 下午4:41
 * desc 无
 */
public class JsonUtil {
    public static String getStringFromJson(JsonObject obj, String key) {
        if (obj == null || !obj.has(key) || obj.get(key).isJsonNull()) return null;
        return obj.get(key).getAsString();
    }

    public static int getIntFromJson(JsonObject obj, String key) {
        if (obj == null || !obj.has(key) || obj.get(key).isJsonNull()) return 0;
        return obj.get(key).getAsInt();
    }

    public static boolean getBooleanFromJson(JsonObject obj, String key) {
        if (obj == null || !obj.has(key) || obj.get(key).isJsonNull()) return false;
        return obj.get(key).getAsBoolean();
    }

    public static <T> List<T> getListFromJson(JsonObject obj, String key, Class<T> cls) {
        if (obj != null && key != null) {
            return getListFromJson(obj.get(key), cls);
        }
        return null;
    }

    public static <T> List<T> getListFromJson(JsonElement data, Class<T> cls) {
        if (data == null || !data.isJsonArray() || cls == null) return null;
        try {
            T[] beans = new Gson().fromJson(data, TypeToken.getArray(cls).getType());
            if (beans != null) {
                List<T> rows = new ArrayList<>();
                for (T item : beans) {
                    rows.add(item);
                }
                return rows;
            }
            //return Arrays.asList(beans);
        } catch (Exception e) {
            MyLog.e("JSON", e.toString());
        }
        return null;
    }

    public static <T> T getObjectFromJson(JsonElement data, Class<T> cls) {
        if (data == null || !data.isJsonObject() || cls == null) return null;
        try {
            return new Gson().fromJson(data, cls);

        } catch (Exception e) {
            MyLog.e("JSON", e.toString());
        }
        return null;
    }
}
