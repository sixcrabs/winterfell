package org.winterfell.misc.zinc.action.es;

import org.winterfell.misc.zinc.ZincResult;
import com.google.gson.Gson;

/**
 * <p>
 * 兼容 es 的版本信息等
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/31
 */
public class EsInfoResult extends ZincResult {

    public EsInfoResult(Gson gson) {
        super(gson);
    }


}
