package com.abook23.tv.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author abook23@163.com
 *  2019/12/06
 */
public class SeaDataUtils {
    public static Map<String, List<String[]>> formatBody(String data) {
        Map<String, List<String[]>> map = new HashMap<>();
        String dataUrls = data.substring(data.indexOf("m3u8$$")+6);
        String[] typeUrls = dataUrls.split("\\$\\$\\$\\$\\$");
        for (String typeUrl : typeUrls) {
            String[] tods = typeUrl.split("#");
            for (String tod : tods) {
                String[] urls = tod.split("\\$");
                String type = urls[2];
                if (!map.containsKey(type)) {
                    map.put(type, new ArrayList<>());
                }
                map.get(type).add(urls);
            }
        }
        return map;
    }

    public static Map<String, List<String[]>> formatBody2(String data) {
        Map<String, List<String[]>> map = new HashMap<>();
        String[] dataArray = data.split("\\$\\$");
        String[] tods = dataArray[1].split("#");
        for (String tod : tods) {
            String[] urls = tod.split("\\$");
            String type = urls[2];
            if (!map.containsKey(type)) {
                map.put(type, new ArrayList<>());
            }
            map.get(type).add(urls);
        }
        return map;
    }
}
