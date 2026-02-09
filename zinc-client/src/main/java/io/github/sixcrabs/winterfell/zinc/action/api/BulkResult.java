package io.github.sixcrabs.winterfell.zinc.action.api;

import io.github.sixcrabs.winterfell.zinc.action.ZincMessagedResult;
import com.google.gson.Gson;

/**
 * <p>
 * .bulk result
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/31
 */
public class BulkResult extends ZincMessagedResult {
    public BulkResult(Gson gson) {
        super(gson);
    }

    /**
     * bulk 影像的记录数
     * @return
     */
    public Long getBulkRecordCount() {
        return isSucceeded() ? getAsLong("record_count") : 0L;
    }

}