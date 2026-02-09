package io.github.sixcrabs.winterfell.zinc.action.es;


import io.github.sixcrabs.winterfell.zinc.ZincResult;
import io.github.sixcrabs.winterfell.zinc.action.AbstractZincAction;

/**
 * <p>
 * /es/**
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/31
 */
public abstract class AbstractEsZincAction<T extends ZincResult> extends AbstractZincAction<T> {

    public AbstractEsZincAction() {
    }

    public AbstractEsZincAction(Builder builder) {
        super(builder);
    }

    /**
     * 子类可以实现该方法修改请求url
     *
     * @return
     */
    @Override
    protected String buildURI() {
        return "/es/" + super.buildURI();
    }
}