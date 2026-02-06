package org.winterfell.misc.keystore;

import lombok.Data;

import java.util.Collections;
import java.util.Map;

/**
 * kv properties
 *
 * @author alex
 * @version v1.0 2019/9/1
 */
@Data
public class KeyStoreProperties {

    /**
     * memory
     * mapdb
     * mvstore
     * redis
     */
    private String type = "memory";

    /**
     * 设置 redis 连接uri / mapdb 指定db文件路径
     */
    private String uri;

    /**
     * 自动提交数据间隔 单位s
     * mapdb/mvstore 生效
     */
    private long autoCommitInterval = 5;

    /**
     * 其他一些自定义设置
     */
    private Map<String, Object> setting = Collections.emptyMap();
}
