package org.winterfell.misc.indigo.renderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0 2022/4/19
 */
public class RenderEngineFactory {

    private static final Logger LOG = LoggerFactory.getLogger(RenderEngineFactory.class);

    private List<RenderEngine> renderEngines = new ArrayList<>(2);


    /**
     * 返回恰当的渲染引擎 如果没有相应引擎 则返回第一个
     * 支持传入渲染引擎的配置信息，从而可以定制引擎的表现形式
     *
     * @param fileExt      文件扩展名 eg `.docx`
     * @param engineConfig 引擎个性化配置
     * @return
     */
    @SuppressWarnings("unchecked")
    public RenderEngine get(String fileExt, Map engineConfig) {
        RenderEngine engine = renderEngines.stream().filter(renderEngine -> renderEngine.supportsFileType(fileExt)).findFirst().orElse(renderEngines.get(0));
        engine.init(engineConfig);
        return engine;
    }

    /**
     * 加载所有渲染引擎实现
     *
     * @throws Exception
     */
    public void load() throws Exception {
        // 加载所有引擎实现
        ServiceLoader<RenderEngine> engines = ServiceLoader.load(RenderEngine.class);
        for (RenderEngine engine : engines) {
            renderEngines.add(engine);
        }
        LOG.info("[doc] loaded [{}] render engines", renderEngines.size());
    }
}