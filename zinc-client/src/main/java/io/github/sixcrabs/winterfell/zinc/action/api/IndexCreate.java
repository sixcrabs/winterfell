package io.github.sixcrabs.winterfell.zinc.action.api;

import io.github.sixcrabs.winterfell.zinc.action.AbstractZincAction;
import io.github.sixcrabs.winterfell.zinc.http.HttpRequestMethod;
import io.github.sixcrabs.winterfell.zinc.model.DocumentDateTimeField;
import io.github.sixcrabs.winterfell.zinc.model.DocumentField;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static io.github.sixcrabs.winterfell.zinc.support.GsonUtil.make;

/**
 * <p>
 * 创建 es 索引
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/31
 */
public class IndexCreate extends AbstractZincAction<IndexResult> {

    protected IndexCreate(Builder builder) {
        super(builder);
        this.payload = makePayload(builder);
    }

    private JsonObject makePayload(Builder builder) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", builder.getIndexName());
        jsonObject.addProperty("storage_type", builder.storageType);
        jsonObject.addProperty("shard_num", builder.shardNum);
        JsonObject properties = new JsonObject();
        builder.fields.forEach(iField -> properties.add(iField.getName(), iField.toJsonObject()));
        jsonObject.add("mappings", make("properties", properties));
        return jsonObject;
    }

    /**
     * 子类可以实现该方法修改请求url
     *
     * @return
     */
    @Override
    protected String buildURI() {
        return "/api/index";
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

    public static class Builder extends AbstractZincAction.Builder<IndexCreate, Builder> {

        /**
         * disk(default) s3 minio
         */
        private String storageType = "disk";

        private int shardNum = 3;

        private List<DocumentField> fields = new ArrayList(1);

        public Builder addField(DocumentField field) {
            this.fields.add(field);
            return this;
        }

        public Builder addField(DocumentDateTimeField documentDateTimeField) {
            this.fields.add(documentDateTimeField);
            return this;
        }

        public Builder setShardNum(int shardNum) {
            this.shardNum = shardNum;
            return this;
        }

        public Builder setStorageType(String storageType) {
            this.storageType = storageType;
            return this;
        }

        @Override
        public IndexCreate build() {
            return new IndexCreate(this);
        }
    }
}