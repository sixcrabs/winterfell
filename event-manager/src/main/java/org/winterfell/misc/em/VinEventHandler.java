package org.winterfell.misc.em;

import com.lmax.disruptor.EventHandler;

/**
 * <p>
 * 事件处理的接口 {@link VinEvent}
 * </p>
 *
 * @author Alex
 * @version v1.0 2022/12/29
 */
public interface VinEventHandler<T extends VinEvent> extends EventHandler<T> {

    /**
     * 是否支持该事件类型
     * @param eventClass
     * @return
     */
    boolean support(Class<T> eventClass);


    /**
     * 控制消费的次序 值越小越在前
     * @return
     */
     default int ordinal() {
         return 0;
     }

}
