package org.winterfell.misc.zinc.action.api;

import org.winterfell.misc.zinc.ZincResult;
import com.google.gson.Gson;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/31
 */
public class HealthResult extends ZincResult {
    public HealthResult(Gson gson) {
        super(gson);
    }

    public boolean isOk() {
        return "ok".equalsIgnoreCase(getAsString("status"));
    }
}
