package org.winterfell.misc.zinc.action;

import org.winterfell.misc.zinc.ZincResult;
import org.winterfell.misc.zinc.exception.ZincException;
import org.winterfell.misc.zinc.http.HttpRequestMethod;
import org.winterfell.misc.zinc.support.ZincUtil;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/30
 */
public abstract class AbstractZincAction<T extends ZincResult> implements ZincAction<T> {


    public static final Logger log = LoggerFactory.getLogger(AbstractZincAction.class);

    public final static String CHARSET = "utf-8";

    /**
     * es 索引名
     */
    protected String indexName;

    /**
     * 请求的数据
     */
    protected Object payload;

    private final ConcurrentMap<String, Object> headerMap = new ConcurrentHashMap<String, Object>();

    /**
     * 请求url参数
     */
    private final Multimap<String, Object> parameterMap = LinkedHashMultimap.create();

    public AbstractZincAction() {
    }

    @SuppressWarnings("unchecked")
    public AbstractZincAction(Builder builder) {
        parameterMap.putAll(builder.parameters);
        headerMap.putAll(builder.headers);
        indexName = builder.indexName;
    }

    @Override
    public Map<String, Object> getHeaders() {
        return this.headerMap;
    }


    /**
     * request method
     *
     * @return
     */
    @Override
    public abstract HttpRequestMethod getRequestMethod();

    /**
     *
     * request uri
     *
     * @return
     */
    @Override
    public String getRequestURI() {
        String finalUri = buildURI();
        if (!parameterMap.isEmpty()) {
            try {
                finalUri += buildQueryString();
            } catch (UnsupportedEncodingException e) {
                // unless CHARSET is overridden with a wrong value in a subclass,
                // this exception won't be thrown.
                log.error("Error occurred while adding parameters to uri.", e);
            }
        }
        return finalUri;
    }

    @Override
    public String getData(Gson gson) {
        if (payload == null) {
            return null;
        } else if (payload instanceof String) {
            return (String) payload;
        } else {
            return gson.toJson(payload);
        }
    }

    @Override
    public String getPathToResult() {
        return null;
    }

    @Override
    public T createNewResult(String responseBody, int statusCode, String reasonPhrase, Gson gson) {
        try {
            return createNewResult(ZincUtil.newInstance(resultClass(), gson), responseBody, statusCode, reasonPhrase, gson);
        } catch (ZincException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 子类调用去生成指定的 result 对象
     * @param result
     * @param responseBody
     * @param statusCode
     * @param reasonPhrase
     * @return
     */
    public T createNewResult(T result, String responseBody, int statusCode, String reasonPhrase, Gson gson) {
        JsonObject jsonMap = parseResponseBody(responseBody);
        result.setResponseCode(statusCode);
        result.setJsonString(responseBody);
        result.setJsonObject(jsonMap);
        result.setPathToResult(getPathToResult());

        if (isHttpSuccessful(statusCode)) {
            result.setSucceeded(true);
            log.debug("Request and operation succeeded");
        } else {
            result.setSucceeded(false);
            // provide the generic HTTP status code error, if one hasn't already come in via the JSON response...
            // eg.
            //  IndicesExist will return 404 (with no content at all) for a missing index, but:
            //  Update will return 404 (with an error message for DocumentMissingException)
            if (result.getErrorMessage() == null) {
                result.setErrorMessage(statusCode + " " + (reasonPhrase == null ? "null" : reasonPhrase));
            }
            log.debug("Response is failed. errorMessage is " + result.getErrorMessage());
        }
        return result;
    }

    protected JsonObject parseResponseBody(String responseBody) {
        if (responseBody == null || responseBody.trim().isEmpty()) {
            return new JsonObject();
        }

        JsonElement parsed = new JsonParser().parse(responseBody);
        if (parsed.isJsonObject()) {
            return parsed.getAsJsonObject();
        } else {
            throw new JsonSyntaxException("Response did not contain a JSON Object");
        }
    }

    /**
     * 子类可以实现该方法修改请求url
     *
     * @return
     */
    protected String buildURI() {
        StringBuilder sb = new StringBuilder();
        try {
            if (ZincUtil.isNotBlank(indexName)) {
                sb.append(URLEncoder.encode(indexName, CHARSET));
            }
        } catch (UnsupportedEncodingException e) {
            // unless CHARSET is overridden with a wrong value in a subclass,
            // this exception won't be thrown.
            log.error("Error occurred while adding index/type to uri", e);
        }
        return sb.toString();
    }

    /**
     * query params
     *
     * @return
     * @throws UnsupportedEncodingException
     */
    protected String buildQueryString() throws UnsupportedEncodingException {
        StringBuilder queryString = new StringBuilder();

        queryString.append("?");
        for (Map.Entry<String, Object> entry : parameterMap.entries()) {
            queryString.append(URLEncoder.encode(entry.getKey(), CHARSET))
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue().toString(), CHARSET))
                    .append("&");
        }
        // if there are any params  ->  deletes the final ampersand
        // if no params             ->  deletes the question mark
        queryString.deleteCharAt(queryString.length() - 1);

        return queryString.toString();
    }

    protected boolean isHttpSuccessful(int httpCode) {
        return (httpCode / 100) == 2;
    }

    public Object getHeader(String header) {
        return headerMap.get(header);
    }

    public Collection<Object> getParameter(String parameter) {
        return parameterMap.get(parameter);
    }


    /**
     * 从泛型参数里获取class
     * @return
     */
    @SuppressWarnings("unchecked")
    public Class<T> resultClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected static abstract class Builder<T extends ZincAction, K> {

        protected Multimap<String, Object> parameters = LinkedHashMultimap.<String, Object>create();
        protected Map<String, Object> headers = new LinkedHashMap<String, Object>();

        /**
         * 可以为空
         */
        protected String indexName;

        public K setParameter(String key, Object value) {
            parameters.put(key, value);
            return (K) this;
        }

        public K setHeader(String key, Object value) {
            headers.put(key, value);
            return (K) this;
        }

        public K setHeader(Map<String, Object> headers) {
            this.headers.putAll(headers);
            return (K) this;
        }

        public K setIndexName(String indexName) {
            this.indexName = indexName;
            return (K) this;
        }

        public String getIndexName() {
            return indexName;
        }

        abstract public T build();
    }


}
