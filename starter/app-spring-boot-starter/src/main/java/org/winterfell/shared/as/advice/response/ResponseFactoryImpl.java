package org.winterfell.shared.as.advice.response;

import org.winterfell.shared.as.config.ResponseProperties;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/10/10
 */
public class ResponseFactoryImpl implements ResponseFactory {

    private final ResponseProperties responseProperties;

    public ResponseFactoryImpl(ResponseProperties responseProperties) {
        this.responseProperties = responseProperties;
    }

    @Override
    public <T> Response<T> create(boolean success) {
        return success ? createSuccess(null) : createFail();
    }

    @Override
    public <T> Response<T> createSuccess(T data) {
        return ResponseImpl.of(responseProperties.getSuccessCode(), responseProperties.getSuccessMsg(), data);
    }

    @Override
    public <T> Response<T> createFail() {
        return ResponseImpl.ofNull(responseProperties.getFailCode(), responseProperties.getFailMsg());
    }

    @Override
    public <T> Response<T> createFail(String message) {
        return ResponseImpl.ofNull(responseProperties.getFailCode(), message);
    }

    @Override
    public <T> Response<T> createFail(int code, String message) {
        return ResponseImpl.ofNull(code, message);
    }

}