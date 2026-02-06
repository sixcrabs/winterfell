package org.winterfell.misc.zinc.http;


import org.winterfell.misc.zinc.http.apache.HttpDeleteWithEntity;
import org.winterfell.misc.zinc.http.apache.HttpGetWithEntity;
import org.apache.http.client.methods.*;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/30
 */
public enum HttpRequestMethod {

    //
    POST {
        @Override
        HttpUriRequest toRequest(String url) {
            return new HttpPost(url);
        }
    },
    GET {
        @Override
        HttpUriRequest toRequest(String url) {
            return new HttpGetWithEntity(url);
        }
    },
    PUT {
        @Override
        HttpUriRequest toRequest(String url) {
            return new HttpPut(url);
        }
    },
    PATCH {
        @Override
        HttpUriRequest toRequest(String url) {
            return new HttpPatch(url);
        }
    },
    DELETE {
        @Override
        HttpUriRequest toRequest(String url) {
            return new HttpDeleteWithEntity(url);
        }
    },
    HEAD {
        @Override
        HttpUriRequest toRequest(String url) {
            return new HttpHead(url);
        }
    },
    OPTIONS {
        @Override
        HttpUriRequest toRequest(String url) {
            return new HttpOptions(url);

        }
    };

    private HttpRequestMethod() {
    }

    abstract HttpUriRequest toRequest(String url);
}
