package com.easytag.json.utils;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
public class JsonResponse<T> {
    private Integer responseCode;
    private JsonError error;
    private T data;

    public JsonResponse(Integer responseCode, JsonError error, T object) {
        this.responseCode = responseCode;
        this.error = error;
        this.data = object;
    }

    public JsonError getError() {
        return error;
    }

    public void setError(JsonError error) {
        this.error = error;
    }
    

    public T getObject() {
        return data;
    }

    public void setObject(T object) {
        this.data = object;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

}
