package org.winterfell.misc.em;

import java.util.List;

/**
 * <p>
 * 事件消费handler注册接口，由应用实现该接口
 * </p>
 *
 * @author Alex
 * @version v1.0 2022/12/29
 */
@SuppressWarnings("rawtypes")
public interface VinEventHandlerRegistry<T extends VinEvent> {

    /**
     * 对应事件处理器集合
     * @return 该事件的处理器(消费者)
     */
    List<VinEventHandler<T>> eventHandlers();
}
