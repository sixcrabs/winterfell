package org.winterfell.misc.em;

import java.io.Serializable;

/**
 * <p>
 * manager 配置
 * </p>
 *
 * @author Alex
 * @version v1.0 2022/12/29
 */
public class VinEmConfig implements Serializable {

    /**
     * ring buffer size 必须是 2的n次方
     */
    private int bufferSize = 128;


    public int getBufferSize() {
        return bufferSize;
    }

    public VinEmConfig setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
        return this;
    }

    public VinEmConfig() {
    }
}
