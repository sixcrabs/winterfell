package org.winterfell.misc.em;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * vin event manager 主要类
 * </p>
 *
 * @author Alex
 * @version v1.0 2022/12/29
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class VinEventManager {

    private static final Logger logger = LoggerFactory.getLogger(VinEventManager.class);

    private final VinEmConfig config;

    private Disruptor<VinEvent> disruptor;

    private static final int UNINITIALIZED = 0;
    private static final int FAILED_INITIALIZATION = 1;
    private static final int SUCCESSFUL_INITIALIZATION = 2;
    private static int INITIALIZATION_STATE;

    public VinEventManager(VinEmConfig config, EventFactory<VinEvent> eventFactory, VinEventHandlerRegistry registry) {
        this.config = config;
        this.init(eventFactory, registry);
        INITIALIZATION_STATE = UNINITIALIZED;
    }

    public VinEventManager(EventFactory<VinEvent> eventFactory, VinEventHandlerRegistry registry) {
        this.config = new VinEmConfig();
        this.init(eventFactory, registry);
        INITIALIZATION_STATE = UNINITIALIZED;
    }


    /**
     * 初始化
     */
    private void init(EventFactory<VinEvent> eventFactory, VinEventHandlerRegistry registry) {
        if (INITIALIZATION_STATE == UNINITIALIZED) {
            synchronized (this) {
                if (INITIALIZATION_STATE == UNINITIALIZED) {
                    logger.info("[vem] initializing...");
                    // FIXME: 这里的等待策略 需要放开给调用方确定
                    disruptor = new Disruptor<>(eventFactory, config.getBufferSize(), new ThreadFactoryBuilder().setNameFormat("em-thread-%d").build(),
                            ProducerType.MULTI, new BlockingWaitStrategy());
                    // 设置事件处理器--消费者
                    List<VinEventHandler> handlers = registry.eventHandlers();
                    // 排序
                    handlers.sort((o1, o2) -> {
                        if (o1.ordinal() - o2.ordinal() == 0) {
                            return 0;
                        } else {
                            return (o1.ordinal() - o2.ordinal() > 0) ? 1 : -1;
                        }
                    });
                    EventHandlerGroup eventHandlerGroup = null;
                    for (VinEventHandler handler : handlers) {
                        if (Objects.isNull(eventHandlerGroup)) {
                            eventHandlerGroup = disruptor.handleEventsWith(handler);
                        } else {
                            eventHandlerGroup.then(handler);
                        }
                    }
                    disruptor.start();
                    INITIALIZATION_STATE = SUCCESSFUL_INITIALIZATION;
                    logger.info("[vem] initialized successfully");
                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        try {
                            this.destroy();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }));
                } else {
                    logger.info("[vem] has been initialized already");
                }
                INITIALIZATION_STATE = FAILED_INITIALIZATION;
            }
        }
    }

    /**
     * 发布事件
     *
     * @param eventData  事件携带的数据对象
     * @param <T>
     */
    public <T extends Serializable> void dispatch(T eventData) throws VinEmException {
        Preconditions.checkArgument(INITIALIZATION_STATE != SUCCESSFUL_INITIALIZATION, "尚未初始化!");
        disruptor.publishEvent((evt, sequence) -> evt.setPayload(eventData));
    }

    /**
     * 销毁
     * @throws Exception
     */
    public void destroy() throws Exception {
        synchronized (VinEventManager.class) {
            if (disruptor != null) {
                disruptor.shutdown();
            }
        }
    }

}
