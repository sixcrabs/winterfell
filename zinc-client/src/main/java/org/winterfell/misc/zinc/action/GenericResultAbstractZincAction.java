package org.winterfell.misc.zinc.action;

import org.winterfell.misc.zinc.ZincResult;
import com.google.gson.Gson;

/**
 * <p>
 * 通用的请求 返回结果用 ZincResult 接收
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/31
 */
public abstract class GenericResultAbstractZincAction extends AbstractZincAction<ZincResult>{

    public GenericResultAbstractZincAction() {
    }

    public GenericResultAbstractZincAction(Builder builder) {
        super(builder);
    }

    @Override
    public ZincResult createNewResult(String responseBody, int statusCode, String reasonPhrase, Gson gson) {
        return createNewResult(new ZincResult(gson), responseBody, statusCode, reasonPhrase, gson);
    }
}
