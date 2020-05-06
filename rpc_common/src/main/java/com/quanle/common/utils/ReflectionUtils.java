package com.quanle.common.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author quanle
 * @date 2020/5/6 10:39 PM
 */
public class ReflectionUtils {
    public static String buildKeyWithClassAndMethod(Class<?> clazz, Method method) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("interface", clazz.getName());
        map.put("method", method.getName());
        Parameter[] parameters = method.getParameters();
        if (parameters.length > 0) {
            StringBuilder param = new StringBuilder();
            for (int i = 0; i < parameters.length; i++) {
                Parameter p = parameters[i];
                param.append(p.getType().getName());
                if (i < parameters.length - 1) {
                    param.append(",");
                }
            }
            map.put("parameter", param.toString());
        }
        return StringUtils.map2String(map);
    }
}
