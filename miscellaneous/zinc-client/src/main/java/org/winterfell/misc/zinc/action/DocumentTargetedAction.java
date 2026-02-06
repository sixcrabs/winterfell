package org.winterfell.misc.zinc.action;


import org.winterfell.misc.zinc.ZincResult;

/**
 * <p>
 * . Represents an Action that <b>can <i>(but NOT necessarily does)</i></b> operate on a targeted single document.
 *
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/31
 */
public interface DocumentTargetedAction<T extends ZincResult> extends ZincAction<T> {

    /**
     * 索引名称
     * @return
     */
    String getIndexName();

    /**
     * 文档 id
     * @return
     */
    String getId();
}
