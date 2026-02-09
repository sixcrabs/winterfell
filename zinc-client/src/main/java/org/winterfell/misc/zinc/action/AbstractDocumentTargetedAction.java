package org.winterfell.misc.zinc.action;


import org.winterfell.misc.zinc.ZincResult;
import org.winterfell.misc.zinc.support.ZincUtil;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/31
 */
public abstract class AbstractDocumentTargetedAction<T extends ZincResult> extends AbstractZincAction<T> implements DocumentTargetedAction<T> {

    protected String id;

    public AbstractDocumentTargetedAction(Builder builder) {
        super(builder);
        this.id = builder.id;
    }

    /**
     * 索引名称
     *
     * @return
     */
    @Override
    public String getIndexName() {
        return indexName;
    }

    /**
     * 文档 id
     *
     * @return
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * TODO
     *
     * @return
     */
    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder("/api/" + indexName + "/_doc");
        if (ZincUtil.isNotBlank(id)) {
            sb.append("/").append(id);
        }
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    protected abstract static class Builder<T extends AbstractDocumentTargetedAction, K> extends AbstractZincAction.Builder<T, K> {

        private String id;

        public K id(String id) {
            this.id = id;
            return (K) this;
        }

    }
}
