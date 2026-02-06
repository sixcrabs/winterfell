package org.winterfell.misc.zinc.action.api.document;

import org.winterfell.misc.zinc.ZincResult;
import com.google.gson.Gson;

/**
 * <p>
 * .TODO
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/31
 */
public class DocumentResult extends ZincResult {

    @Override
    public String getPathToResult() {
        return "_source";
    }

    public DocumentResult(Gson gson) {
        super(gson);
    }
}
