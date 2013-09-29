package com.easytag.exceptions;

import com.easytag.json.utils.ResponseConstants;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
public class TagException extends Exception {

    private Integer errorCode;

    /**
     * Creates a new instance of
     * <code>PatientException</code> without detail message.
     */
    public TagException() {
    }

    public TagException(String message, Integer errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public TagException(String msg) {
        super(msg);
        this.errorCode = ResponseConstants.NORMAL_ERROR_CODE;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }
}
