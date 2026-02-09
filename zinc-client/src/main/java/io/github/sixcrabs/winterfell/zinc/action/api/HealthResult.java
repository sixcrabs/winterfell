package io.github.sixcrabs.winterfell.zinc.action.api;

import io.github.sixcrabs.winterfell.zinc.ZincResult;
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