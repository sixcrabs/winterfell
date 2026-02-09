package io.github.sixcrabs.winterfell.zinc.action;


import io.github.sixcrabs.winterfell.zinc.ZincResult;

/**
 * <p>
 * 可进行bulk的action
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/31
 */
public interface BulkableZincAction<T extends ZincResult> extends DocumentTargetedAction<T> {

    /**
     * eg 'index' 'update' 'delete'
     * @return
     */
    String getBulkMethodName();
}