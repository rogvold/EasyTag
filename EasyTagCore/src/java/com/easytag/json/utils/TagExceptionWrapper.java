package com.easytag.json.utils;

import com.easytag.exceptions.TagException;
import com.easytag.json.utils.JsonError;
import com.easytag.json.utils.JsonResponse;
import com.easytag.json.utils.ResponseConstants;
import com.google.gson.Gson;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
public class TagExceptionWrapper {

    public static String wrapException(TagException exc) {
        return (new Gson()).toJson(new JsonResponse(ResponseConstants.ERROR, new JsonError(exc.getMessage(), exc.getErrorCode()), null));
    }
}