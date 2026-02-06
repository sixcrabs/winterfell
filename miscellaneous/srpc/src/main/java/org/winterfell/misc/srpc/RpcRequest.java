package org.winterfell.misc.srpc;

import java.io.Serializable;

/**
 * <p>
 * rpc 请求封装类
 * </p>
 *
 * @author alex
 * @version v1.0, 2019/12/19
 */
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = -2222479480119527588L;

    /**
     * 请求id
     */
    private final String id;

    /**
     * 创建时间
     */
    private long createAt;

    /**
     * 目标类名称
     */
    private String targetClassName;

    /**
     * 目标方法名称
     */
    private String targetMethodName;

    /**
     * 参数类型列表
     */
    private Class<?>[] parameterTypes;

    /**
     * 方法参数
     */
    private Object[] parameters;

    /**
     * access-token
     */
    private String accessToken;

    private RpcRequest(String id) {
        this.id = id;
    }


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String accessToken;

        private final String id;

        private String targetClassName;

        private String targetMethodName;

        private Class<?>[] parameterTypes;

        /**
         * 方法参数
         */
        private Object[] parameters;


        private Builder() {
            this.id = SrpcUtil.simpleUUID();
        }

        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder targetClassName(String className) {
            this.targetClassName = className;
            return this;
        }

        public Builder targetMethodName(String methodName) {
            this.targetMethodName = methodName;
            return this;
        }

        public Builder parameterTypes(Class<?>[] parameterTypes) {
            this.parameterTypes = parameterTypes;
            return this;
        }

        public Builder parameters(Object[] args) {
            this.parameters = args;
            return this;
        }

        public RpcRequest build() {
            return new RpcRequest(this.id)
                    .setAccessToken(this.accessToken)
                    .setCreateAt(System.currentTimeMillis())
                    .setParameters(this.parameters)
                    .setParameterTypes(this.parameterTypes)
                    .setTargetMethodName(this.targetMethodName)
                    .setTargetClassName(this.targetClassName);

        }
    }

    public String getId() {
        return id;
    }

    public long getCreateAt() {
        return createAt;
    }

    public String getTargetClassName() {
        return targetClassName;
    }

    public String getTargetMethodName() {
        return targetMethodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public RpcRequest setCreateAt(long createAt) {
        this.createAt = createAt;
        return this;
    }

    public RpcRequest setTargetClassName(String targetClassName) {
        this.targetClassName = targetClassName;
        return this;
    }

    public RpcRequest setTargetMethodName(String targetMethodName) {
        this.targetMethodName = targetMethodName;
        return this;
    }

    public RpcRequest setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
        return this;
    }

    public RpcRequest setParameters(Object[] parameters) {
        this.parameters = parameters;
        return this;
    }

    public RpcRequest setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }
}
