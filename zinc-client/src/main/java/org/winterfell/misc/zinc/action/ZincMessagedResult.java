package org.winterfell.misc.zinc.action;

import org.winterfell.misc.zinc.ZincResult;
import com.google.gson.Gson;

/**
 * <p>
 * TBD
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/31
 */
public abstract class ZincMessagedResult extends ZincResult {
    public ZincMessagedResult(Gson gson) {
        super(gson);
    }

    public String getMessage() {
        return getAsString("message");
    }

}
