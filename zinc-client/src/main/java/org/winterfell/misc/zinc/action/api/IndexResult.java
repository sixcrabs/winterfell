package org.winterfell.misc.zinc.action.api;

import org.winterfell.misc.zinc.ZincResult;
import com.google.gson.Gson;

/**
 * <p>
 * 创建 update delete 索引 结果
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/31
 */
public class IndexResult extends ZincResult {
    public IndexResult(Gson gson) {
        super(gson);
    }

    public String getIndexName() {
        return getAsString("index");
    }

    public String getStorageType() {
        return getAsString("storage_type");
    }


    @Override
    public String getErrorMessage() {
        return getAsString("error");
    }
}
