package com.jhalkjar.caoscomp.web;

/**
 * The http Response class
 *
 * @author Chen Fishbein
 */
public class Response<T> {
    private int responseCode;
    private T responseData;
    private String responseMessage;

    Response(int responseCode, T responseData, String responseMessage) {
        this.responseCode = responseCode;
        this.responseData = responseData;
        this.responseMessage = responseMessage;
    }

    /**
     * The http response data
     *
     * @return the data
     */
    public T getResponseData() {
        return responseData;
    }

    /**
     * The http response code
     *
     * @return the code
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * Returns the error message that accompanies the response
     * @return the response
     */
    public String getResponseErrorMessage() {
        return responseMessage;
    }
}
