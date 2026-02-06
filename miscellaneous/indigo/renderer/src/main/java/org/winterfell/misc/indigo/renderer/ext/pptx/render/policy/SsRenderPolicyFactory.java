package org.winterfell.misc.indigo.renderer.ext.pptx.render.policy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * 创建各类 渲染策略
 * spi 方式
 *
 * @author alex
 * @version v1.0 2020/11/18
 */
public class SsRenderPolicyFactory {

    private static final Logger logger = LoggerFactory.getLogger(SsRenderPolicyFactory.class);

    public static final SsRenderPolicyFactory INSTANCE = new SsRenderPolicyFactory();

    private static Map<Class<? extends SsRenderPolicy>, SsRenderPolicy> repository = new HashMap<>(1);

    private SsRenderPolicyFactory() {
    }

    static {
        ServiceLoader<SsRenderPolicy> policies = ServiceLoader.load(SsRenderPolicy.class);
        for (SsRenderPolicy policy : policies) {
            repository.put(policy.getClass(), policy);
        }
        logger.info("load [{}] policies of `SlideShow`", repository.size());
    }

    /**
     * 获取某一个渲染策略
     *
     * @param clazz class
     * @return render policy
     */
    public SsRenderPolicy get(Class<? extends SsRenderPolicy> clazz) {
        return repository.get(clazz);
    }
}