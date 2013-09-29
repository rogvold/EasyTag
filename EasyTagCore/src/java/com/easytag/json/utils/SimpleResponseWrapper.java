package com.easytag.json.utils;

import com.google.gson.Gson;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
public class SimpleResponseWrapper {

    public static String getJsonResponse(JsonResponse resp) {
        return (new Gson()).toJson(resp);
    }
}