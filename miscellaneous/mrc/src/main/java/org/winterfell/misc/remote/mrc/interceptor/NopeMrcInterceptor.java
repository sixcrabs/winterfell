package org.winterfell.misc.remote.mrc.interceptor;

import okhttp3.Interceptor;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/9/13
 */
public class NopeMrcInterceptor implements MrcInterceptor {

    @Override
    public boolean shouldSkip() {
        return true;
    }

    @Override
    public Interceptor nativeInterceptor() {
        return null;
    }
}
