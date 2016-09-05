package net.roocky.mojian.Util;

import java.util.Map;

/**
 * Created by roock on 09/05.
 */
public class MapUtil {
    //已知Value，获取Key
    public static <K, V> K getKeyByValue(Map<K, V> map, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
