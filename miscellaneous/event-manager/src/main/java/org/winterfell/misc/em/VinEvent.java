package org.winterfell.misc.em;

import java.io.Serializable;

/**
 * <p>
 * 事件接口
 * </p>
 *
 * @author Alex
 * @version v1.0 2022/12/29
 */
public interface VinEvent<T extends Serializable> extends Serializable, Cloneable {

    /**
     * 设置事件载荷
     * @param payload
     */
    void setPayload(T payload);
}
