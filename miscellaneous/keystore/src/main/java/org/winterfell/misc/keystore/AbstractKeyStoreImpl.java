package org.winterfell.misc.keystore;

import org.winterfell.misc.timer.TimerManager;
import org.winterfell.misc.timer.TimerTask;
import org.winterfell.misc.timer.job.TimerJobs;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/10/20
 */
public abstract class AbstractKeyStoreImpl implements SimpleKeyStore {

    protected static final Map<String, List<KeyEventListener>> EVENT_LISTENERS = new ConcurrentHashMap<>(1);

    /**
     * 管理具有时效性的数据
     * 以 1s 为最小单位
     */
    protected static final TimerManager TIMER_MANAGER = new TimerManager(new TimerManager.TimerConfig().setTickDuration(1).setTicksPerWheel(64).setTimeUnit(TimeUnit.SECONDS));

    /**
     * 用于定期commit (mapdb & mvstore)
     */
    protected static final ScheduledExecutorService SCHEDULED_THREAD_POOL = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());


    @Override
    public void addListener(String key, KeyEventListener listener) throws KeyStoreException {
        if (has(key)) {
            // key 存在时添加监听器
            EVENT_LISTENERS.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>()).add(listener);
        }
    }

    /**
     * do destroy
     */
    @Override
    public void destroy() {
        SimpleKeyStore.super.destroy();
        SCHEDULED_THREAD_POOL.shutdown();
    }

    /**
     *  initialize
     * @param properties
     */
    protected abstract void initialize(KeyStoreProperties properties);

    /**
     * add ttl timer
     *
     * @param key 设置 ttl 的key
     * @param ttl ttl 时间 单位秒
     * @return
     */
    protected boolean addTTLTimer(String key, long ttl) {
        if (ttl > 0) {
            // 清空已存在的 ttl timer
            TIMER_MANAGER.removeTimerJob(key);
            return TIMER_MANAGER.addTimerJob(TimerJobs.newDelayJob(key, Duration.ofSeconds(ttl), timeout -> {
                notifyListeners(EXPIRED, key, get(key));
                remove(key);
            }));
        }
        return false;
    }

    /**
     * add ttl timer with used-defined task
     * @param key
     * @param ttl
     * @param task 到期后执行的自定义任务
     * @return
     */
    protected boolean addTTLTimer(String key, long ttl, TimerTask task) {
        if (ttl > 0) {
            // 清空已存在的 ttl timer
            TIMER_MANAGER.removeTimerJob(key);
            return TIMER_MANAGER.addTimerJob(TimerJobs.newDelayJob(key, Duration.ofSeconds(ttl), task));
        }
        return false;
    }

    /**
     * 通知监听器
     *
     * @param eventType 事件类型 expired/removed/... 使用string 方便实现类扩展
     * @param key       事件关联的key
     * @param val       值，可选
     */
    protected void notifyListeners(String eventType, String key, Object val) {
        List<KeyEventListener> listeners = getListeners(key);
        if (listeners != null) {
            for (KeyEventListener listener : listeners) {
                listener.onEvent(eventType, key, val);
            }
        }
        if (REMOVED.equalsIgnoreCase(eventType)) {
            // key 被移除后 同时移除监听器和定时任务
            removeListeners(key);
            TIMER_MANAGER.removeTimerJob(key);
        }
    }

    /**
     * get event listeners
     *
     * @param key key
     * @return
     */
    private List<KeyEventListener> getListeners(String key) {
        return EVENT_LISTENERS.get(key);
    }

    /**
     * remove all listeners fo key
     *
     * @param key
     */
    private void removeListeners(String key) {
        List<KeyEventListener> listeners = getListeners(key);
        if (listeners != null) {
            listeners.clear();
        }
    }
}