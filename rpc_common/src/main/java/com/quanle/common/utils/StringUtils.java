package com.quanle.common.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author quanle
 * @date 2020/5/6 10:41 PM
 */
public class StringUtils {
    public static Map<String, String> string2Map(String str) {
        String[] split = str.split("&");
        Map<String, String> map = new HashMap<>(16);
        for (String s : split) {
            String[] split1 = s.split("=");
            map.put(split1[0], split1[1]);
        }
        return map;
    }

    public static String map2String(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            sb.append(entry.getKey()).append("=").append(entry.getValue());
            if (iterator.hasNext()) {
                sb.append("&");
            }
        }
        return sb.toString();
    }
}
