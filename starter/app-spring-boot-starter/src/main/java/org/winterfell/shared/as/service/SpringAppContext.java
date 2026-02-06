package org.winterfell.shared.as.service;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/12/26
 */
@Component
public class SpringAppContext implements ApplicationContextAware {

    private static ApplicationContext context;

    /**
     * 获取bean
     * @param clazz
     * @return
     * @param <T>
     */
    public static <T> T getBean(Class<T> clazz) {
        if (context == null) {
            throw new RuntimeException("SpringAppContext is not initialized.");
        }
        return context.getBean(clazz);
    }

    /**
     * get bean with implementation name
     * @param name
     * @param clazz
     * @return
     * @param <T>
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        if (context == null) {
            throw new RuntimeException("SpringAppContext is not initialized.");
        }
        return context.getBean(name, clazz);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringAppContext.context = applicationContext;
    }
}