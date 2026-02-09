package org.winterfell.misc.zinc.config;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/30
 */
public class ZinClientConfig {

    protected int connTimeout;
    protected int readTimeout;
    protected long maxConnectionIdleTime;
    protected TimeUnit maxConnectionIdleTimeDurationTimeUnit;
    protected Gson gson;
    protected boolean isMultiThreaded;

    protected boolean debugEnabled;

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public int getConnTimeout() {
        return connTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public long getMaxConnectionIdleTime() {
        return maxConnectionIdleTime;
    }

    public Gson getGson() {
        return gson;
    }

    public TimeUnit getMaxConnectionIdleTimeDurationTimeUnit() {
        return maxConnectionIdleTimeDurationTimeUnit;
    }

    public boolean isMultiThreaded() {
        return isMultiThreaded;
    }
}
