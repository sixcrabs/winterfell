package org.winterfell.misc.zinc.http;

import org.winterfell.misc.zinc.ZinClient;
import org.winterfell.misc.zinc.ZincResult;
import org.winterfell.misc.zinc.ZincResultHandler;
import org.winterfell.misc.zinc.action.ZincAction;
import org.winterfell.misc.zinc.config.idle.IdleConnectionReaper;
import org.winterfell.misc.zinc.exception.CouldNotConnectException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Future;

import static org.winterfell.misc.zinc.support.ZincUtil.isNotBlank;

/**
 * <p>
 * http client for ZincSearch
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/30
 */
public class ZincHttpClient implements ZinClient {

    protected ContentType requestContentType = ContentType.APPLICATION_JSON.withCharset("utf-8");

    public static final Logger log = LoggerFactory.getLogger(ZincHttpClient.class);

    private CloseableHttpClient httpClient;

    private CloseableHttpAsyncClient asyncClient;

    private IdleConnectionReaper idleConnectionReaper;

    private final String serverUri;

    /**
     * debug 模式，会输出详细信息
     */
    private boolean debug = false;

    /**
     * basic auth 认证字符串 base64 编码
     */
    private String basicCredentials;

    private Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    public ZincHttpClient(String serverUri) {
        this.serverUri = serverUri;
    }

    /**
     * 开启 debug
     *
     * @return
     */
    public ZincHttpClient enableDebug() {
        this.debug = true;
        return this;
    }

    /**
     * 同步执行
     *
     * @param clientRequestAction
     * @return
     * @throws IOException
     */
    @Override
    public <T extends ZincResult> T execute(ZincAction<T> clientRequestAction) throws IOException {
        return execute(clientRequestAction, null);
    }

    public <T extends ZincResult> T execute(ZincAction<T> clientRequest, RequestConfig requestConfig) throws IOException {
        HttpUriRequest request = prepareRequest(clientRequest, requestConfig);
        CloseableHttpResponse response = null;
        try {
            response = executeRequest(request);
            return deserializeResponse(response, request, clientRequest);
        } catch (HttpHostConnectException ex) {
            throw new CouldNotConnectException(ex.getHost().toURI(), ex);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException ex) {
                    log.error("Exception occurred while closing response stream.", ex);
                }
            }
        }
    }

    /**
     * 异步回调
     *
     * @param clientRequestAction
     * @param zincResultHandler
     */
    @Override
    public <T extends ZincResult> void executeAsync(ZincAction<T> clientRequestAction, ZincResultHandler<? super T> zincResultHandler) {
        synchronized (this) {
            if (!asyncClient.isRunning()) {
                asyncClient.start();
            }
        }
        HttpUriRequest request = prepareRequest(clientRequestAction, null);
        executeAsyncRequest(clientRequestAction, zincResultHandler, request);
    }

    private <T extends ZincResult> T deserializeResponse(HttpResponse response, final HttpRequest httpRequest, ZincAction<T> clientRequest) throws IOException {
        StatusLine statusLine = response.getStatusLine();
        try {
            return clientRequest.createNewResult(
                    response.getEntity() == null ? null : EntityUtils.toString(response.getEntity()),
                    statusLine.getStatusCode(),
                    statusLine.getReasonPhrase(),
                    gson
            );
        } catch (com.google.gson.JsonSyntaxException e) {
            for (Header header : response.getHeaders("Content-Type")) {
                final String mimeType = header.getValue();
                if (!mimeType.startsWith("application/json")) {
                    // probably a proxy that responded in text/html
                    final String message = "Request " + httpRequest.toString() + " yielded " + mimeType
                            + ", should be json: " + statusLine.toString();
                    throw new IOException(message, e);
                }
            }
            throw e;
        }
    }

    /**
     * 准备发送请求
     *
     * @param clientRequest
     * @param requestConfig
     * @param <T>
     * @return
     */
    protected <T extends ZincResult> HttpUriRequest prepareRequest(final ZincAction<T> clientRequest, final RequestConfig requestConfig) {
        String requestURL = getRequestURL(clientRequest.getRequestURI());
        HttpUriRequest request = constructHttpMethod(clientRequest.getRequestMethod(), requestURL, clientRequest.getData(gson), requestConfig);
        log.info("Request method={} url={}", clientRequest.getRequestMethod().name(), requestURL);
        // add headers added to action
        for (Map.Entry<String, Object> header : clientRequest.getHeaders().entrySet()) {
            request.addHeader(header.getKey(), header.getValue().toString());
        }
        if (isNotBlank(this.basicCredentials)) {
            // add basic auth
            request.addHeader("Authorization", this.basicCredentials);
        }
        return request;
    }

    protected CloseableHttpResponse executeRequest(HttpUriRequest request) throws IOException {
        return httpClient.execute(request);
    }

    protected <T extends ZincResult> Future<HttpResponse> executeAsyncRequest(ZincAction<T> clientRequest, ZincResultHandler<? super T> resultHandler, HttpUriRequest request) {
        return asyncClient.execute(request, new DefaultCallback<T>(clientRequest, request, resultHandler));
    }


    /**
     * 组装请求的 url
     * serverUri + 由 action 提供的 uri
     *
     * @param uri
     * @return
     */
    protected String getRequestURL(String uri) {
        StringBuilder sb = new StringBuilder(serverUri);
        if (uri.length() > 0 && uri.charAt(0) == '/') {
            sb.append(uri);
        } else {
            sb.append('/').append(uri);
        }
        return sb.toString();
    }

    protected HttpUriRequest constructHttpMethod(HttpRequestMethod requestMethod, String url, String payload, RequestConfig requestConfig) {
        HttpUriRequest httpUriRequest = requestMethod.toRequest(url);
        if (debug && log.isInfoEnabled()) {
            log.info("[zinc] {} method created based on client request", requestMethod.name());
            log.info("[zinc] request data : \n\r ==============: \n\r {} \n\r==============================",
                    payload);
        }
        if (httpUriRequest instanceof HttpRequestBase && requestConfig != null) {
            ((HttpRequestBase) httpUriRequest).setConfig(requestConfig);
        }

        if (httpUriRequest instanceof HttpEntityEnclosingRequest && payload != null) {
            EntityBuilder entityBuilder = EntityBuilder.create()
                    .setText(payload)
                    .setContentType(requestContentType);
//            if (isRequestCompressionEnabled()) {
//                entityBuilder.gzipCompress();
//            }
            ((HttpEntityEnclosingRequest) httpUriRequest).setEntity(entityBuilder.build());
        }
        // TODO

        return httpUriRequest;
    }

    //region---------------------设置属性-------------------------------------

    public void setIdleConnectionReaper(IdleConnectionReaper idleConnectionReaper) {
        this.idleConnectionReaper = idleConnectionReaper;
    }

    public ZincHttpClient setHttpClient(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
        return this;
    }

    public ZincHttpClient setAsyncClient(CloseableHttpAsyncClient asyncClient) {
        this.asyncClient = asyncClient;
        return this;
    }

    public ZincHttpClient setBasicCredentials(String basicCredentials) {
        this.basicCredentials = basicCredentials;
        return this;
    }

    /**
     * 外部设置属性
     *
     * @param gson
     * @return
     */
    public ZincHttpClient setGson(Gson gson) {
        this.gson = gson;
        return this;
    }

    //endregion---------------------设置属性-------------------------------------

    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     *
     * <p> As noted in {@link AutoCloseable#close()}, cases where the
     * close may fail require careful attention. It is strongly advised
     * to relinquish the underlying resources and to internally
     * <em>mark</em> the {@code Closeable} as closed, prior to throwing
     * the {@code IOException}.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        if (null != idleConnectionReaper) {
            idleConnectionReaper.stopAsync();
            idleConnectionReaper.awaitTerminated();
        }
    }

    protected class DefaultCallback<T extends ZincResult> implements FutureCallback<HttpResponse> {
        private final ZincAction<T> clientRequest;
        private final HttpRequest request;
        private final ZincResultHandler<? super T> resultHandler;

        public DefaultCallback(ZincAction<T> clientRequest, final HttpRequest request, ZincResultHandler<? super T> resultHandler) {
            this.clientRequest = clientRequest;
            this.request = request;
            this.resultHandler = resultHandler;
        }

        @Override
        public void completed(final HttpResponse response) {
            T result = null;
            try {
                result = deserializeResponse(response, request, clientRequest);
            } catch (Exception e) {
                failed(e);
            } catch (Throwable t) {
                failed(new Exception("Problem during request processing", t));
            }
            if (result != null) {
                resultHandler.completed(result);
            }
        }

        @Override
        public void failed(final Exception ex) {
            log.error("Exception occurred during async execution.", ex);
            if (ex instanceof HttpHostConnectException) {
                String host = ((HttpHostConnectException) ex).getHost().toURI();
                resultHandler.failed(new CouldNotConnectException(host, ex));
                return;
            }
            resultHandler.failed(ex);
        }

        @Override
        public void cancelled() {
            log.warn("Async execution was cancelled; this is not expected to occur under normal operation.");
        }
    }

}
