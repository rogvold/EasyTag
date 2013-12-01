package com.easytag.web.utils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
public class OpenIdUtils {

    public Map<String, String> extractIdFromJson(String json) {
        System.out.println("extractIdFromJson(): json = " + json);
        String s = "";
        int i;
        Map<String, String> map = new HashMap();
        if (json.indexOf("facebook.com") > 0) {
            i = json.indexOf("uid\":\"") + 6;
            while (json.charAt(i) != '"') {
                s += json.charAt(i);
                i++;
            }
            map.put("facebook", s);
            return map;
        }

        if (json.indexOf("vk.com\\/id") > 0) {
            s = "";
            i = json.indexOf("uid\":") + 5;
            while (json.charAt(i) != ',') {
                s += json.charAt(i);
                i++;
            }
            map.put("vkontakte", s);
            return map;
        }

        if (json.indexOf("google.com\\/acc") > 0) {
            i = json.indexOf("email\":\"") + 8;
            while (json.charAt(i) != '@') {
                s += json.charAt(i);
                i++;
            }
            map.put("gmail", s);
            return map;
        }

        if (json.indexOf("openid.yandex.ru") > 0) {
            i = json.indexOf(".ru\\/") + 5;
            while (json.charAt(i) != '\\') {
                s += json.charAt(i);
                i++;
            }
            map.put("yandex", s);
            return map;
        }

        if (json.indexOf("openid.mail.ru") > 0) {
            i = json.indexOf("\\/mail\\/") + 8;
            while (json.charAt(i) != '"') {
                s += json.charAt(i);
                i++;
            }
            map.put("mailru", s);

            System.out.println("parsed JSON: map = " + map);

            return map;
        }

        return null;
    }

    public String extractOpenId(String json) {
        Map<String, String> map = extractIdFromJson(json);
        if (map == null) {
            return null;
        }
        String s = "";
        for (String key : map.keySet()) {
            s += key;
            s += map.get(key);
        }
        return s;
    }
}
