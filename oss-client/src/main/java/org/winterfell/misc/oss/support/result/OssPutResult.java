package org.winterfell.misc.oss.support.result;

import java.io.Serializable;

/**
 * <p>
 * obs put object result
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/9/10
 */
public class OssPutResult extends AbstractOssResult implements Serializable {

    private String contentType;

    public String getContentType() {
        return contentType;
    }

    public OssPutResult setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    @Override
    public String toString() {
        return "OssPutResult{" +
                "contentType='" + contentType + '\'' +
                "} " + super.toString();
    }
}