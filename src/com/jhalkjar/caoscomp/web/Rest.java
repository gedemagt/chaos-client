package com.jhalkjar.caoscomp.web;



public class Rest {

    /**
     * Creates a GET request builder
     * @param url The request URL
     * @return RequestBuilder instance
     */
    public static RequestBuilder get(String url) {
        return new RequestBuilder("GET", url);
    }

    /**
     * Creates a HEAD request builder
     * @param url The request URL
     * @return RequestBuilder instance
     */
    public static RequestBuilder head(String url) {
        return new RequestBuilder("HEAD", url);
    }

    /**
     * Creates a OPTIONS request builder
     * @param url The request URL
     * @return RequestBuilder instance
     */
    public static RequestBuilder options(String url) {
        return new RequestBuilder("OPTIONS", url);
    }

    /**
     * Creates a POST request builder
     * @param url The request URL
     * @return RequestBuilder instance
     */
    public static RequestBuilder post(String url) {
        return new RequestBuilder("POST", url);
    }

    /**
     * Creates a DELETE request builder
     * @param url The request URL
     * @return RequestBuilder instance
     */
    public static RequestBuilder delete(String url) {
        return new RequestBuilder("DELETE", url);
    }

    /**
     * Creates a PUT request builder
     * @param url The request URL
     * @return RequestBuilder instance
     */
    public static RequestBuilder put(String url) {
        return new RequestBuilder("PUT", url);
    }

}

