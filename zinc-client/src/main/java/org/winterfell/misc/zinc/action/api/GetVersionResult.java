package org.winterfell.misc.zinc.action.api;

import org.winterfell.misc.zinc.ZincResult;
import com.google.gson.Gson;

/**
 * <p>
 * zinc 版本信息
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/31
 */
public class GetVersionResult extends ZincResult {
    public GetVersionResult(Gson gson) {
        super(gson);
    }

    public String getBuildVersion(){
        return getAsString("version");
    }

    public String getBuildDate() {
        return getAsString("build_date");
    }
}
