package org.winterfell.misc.zinc.action.api;

import org.winterfell.misc.zinc.action.AbstractZincAction;
import org.winterfell.misc.zinc.action.GenericResultAbstractZincAction;
import org.winterfell.misc.zinc.http.HttpRequestMethod;
import org.winterfell.misc.zinc.support.GsonUtil;
import org.winterfell.misc.zinc.support.ZincTokenizers;
import com.google.gson.JsonObject;

import static org.winterfell.misc.zinc.support.GsonUtil.toJsonArray;

/**
 * <p>
 * 参考: https://docs.zincsearch.com/api/index/analyze/#use-a-special-analyzer
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/31
 */
public class Analyze extends GenericResultAbstractZincAction {


    public Analyze(Builder builder) {
        super(builder);
        JsonObject object = GsonUtil.make("tokenizer", builder.tokenizer, "text", builder.text);
        if (builder.charFilter.length > 0) {
            object.add("char_filter", toJsonArray(builder.charFilter, true));
        }
        if (builder.tokenFilter.length > 0) {
            object.add("token_filter", toJsonArray(builder.tokenFilter, true));
        }
        this.payload = object;
    }

    /**
     * 子类可以实现该方法修改请求url
     *
     * @return
     */
    @Override
    protected String buildURI() {
        return "/api/_analyze";
    }

    /**
     * request method
     *
     * @return
     */
    @Override
    public HttpRequestMethod getRequestMethod() {
        return HttpRequestMethod.POST;
    }

    public static class Builder extends AbstractZincAction.Builder<Analyze, Builder> {

        /**
         * 分词器
         */
        private String tokenizer;

        /**
         * 进行分析的文本
         */
        private String text;

        private String[] charFilter = new String[]{};

        private String[] tokenFilter = new String[]{};


        public Builder setTokenizer(ZincTokenizers tokenizer) {
            this.tokenizer = tokenizer.name();
            return this;
        }

        public Builder setText(String text) {
            this.text = text;
            return this;
        }

        public Builder setCharFilter(String... charFilter) {
            this.charFilter = charFilter;
            return this;
        }

        public Builder setTokenFilter(String... tokenFilter) {
            this.tokenFilter = tokenFilter;
            return this;
        }

        @Override
        public Analyze build() {
            return new Analyze(this);
        }
    }
}
