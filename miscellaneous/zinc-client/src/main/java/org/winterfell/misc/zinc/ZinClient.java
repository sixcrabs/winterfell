package org.winterfell.misc.zinc;

import org.winterfell.misc.zinc.action.ZincAction;

import java.io.Closeable;
import java.io.IOException;

/**
 * <p>
 * .TODO
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/30
 */
public interface ZinClient extends Closeable {


    /**
     * 同步执行
     * @param clientRequestAction
     * @param <T>
     * @return
     * @throws IOException
     */
    <T extends ZincResult> T execute(ZincAction<T> clientRequestAction) throws IOException;

    /**
     * 异步回调
     * @param clientRequestAction
     * @param zincResultHandler
     * @param <T>
     */
    <T extends ZincResult> void executeAsync(ZincAction<T> clientRequestAction, ZincResultHandler<? super T> zincResultHandler);

}
