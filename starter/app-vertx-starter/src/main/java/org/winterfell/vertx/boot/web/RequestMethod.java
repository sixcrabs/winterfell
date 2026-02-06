package org.winterfell.vertx.boot.web;

import io.vertx.core.http.HttpMethod;

/**
 * <p>
 * method enum
 * </p>
 *
 * @author alex
 * @version v1.0
 */
public enum RequestMethod {

    //
    GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;

    /**
     * to {@see HttpMethod}
     *
     * @return {@link HttpMethod}
     */
    public HttpMethod toHttpMethod() {
        switch (this) {
            case HEAD:
                return HttpMethod.HEAD;
            case POST:
                return HttpMethod.POST;
            case PUT:
                return HttpMethod.PUT;
            case PATCH:
                return HttpMethod.PATCH;
            case TRACE:
                return HttpMethod.TRACE;
            case DELETE:
                return HttpMethod.DELETE;
            case OPTIONS:
                return HttpMethod.OPTIONS;
            default:
            case GET:
                return HttpMethod.GET;
        }
    }

}
