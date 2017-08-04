package com.commons.util;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * @author clx at 2017年8月4日 下午2:56:25
 */
public class ParamUtils {

    public static String getStr(Map<String, ? extends Object> param, String key,
            String def) {
        Object o = param.get(key);
        if (o != null) {
            return StringUtils.trimToNull(String.valueOf(o));
        }
        return def;
    }

    public static String getStr(Map<String, ? extends Object> param,
            String key) {
        return getStr(param, key, null);
    }

    public static String getNotBlankStr(Map<String, ? extends Object> param,
            String key) {
        String v = getStr(param, key);
        if (StringUtils.isBlank(v)) {
            throw new IllegalArgumentException("the val is blank. key:" + key);
        }
        return v;
    }

    public static Integer getInt(Map<String, ? extends Object> param,
            String key, Integer def) {
        return parseInt(getStr(param, key), def);
    }

    public static Integer getInt(Map<String, ? extends Object> param,
            String key) {
        return parseInt(getStr(param, key), null);
    }

    public static Integer parseInt(Object v, Integer def) {
        if (v != null) {
            try {
                if (v instanceof Number) {
                    return ((Number) v).intValue();
                }
                if (v instanceof CharSequence) {
                    return Integer.valueOf(String.valueOf(v));
                }
            } catch (NumberFormatException ex) {

            }
            return null;
        }
        return def;
    }
}
