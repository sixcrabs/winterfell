package org.winterfell.misc.zinc.model;

import org.winterfell.misc.zinc.support.ZincFieldTypes;
import com.google.gson.JsonObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

import static org.winterfell.misc.zinc.support.ZincUtil.isNotBlank;

/**
 * <p>
 * 索引的文档的 字段
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/31
 */
@ApiModel("全文索引字段")
public class DocumentField implements Serializable {

    private static final long serialVersionUID = -317115339878615842L;

    /**
     * 字段名称
     */
    @ApiModelProperty("字段名称")
    private String name;

    /**
     * 字段类型 text keyword bool ...
     * 参考: https://zincsearch-docs.zinc.dev/api/index/update-mapping/#field-types
     */
    @ApiModelProperty("类型")
    private String type;

    /**
     * Enable index for the field, default is true, it will can't be query if it disabled.
     */
    private boolean index = true;

    /**
     * Store value for the field, default is false, it can return the origin value when query, it used for like highlight.
     */
    private boolean store;

    /**
     * Enable sort support for the field, default is false, but it enabled for numeric and date type by default.
     */
    private boolean sortable;

    /**
     * Enable aggregation for the field, default is false, but it enabled for numeric and date type by default.
     */
    private boolean aggregatable;

    /**
     * Enable highlight for the field, default is false, if you want to use highlight for the field, you should enable it.
     */
    private boolean highlightable;

    /**
     * 只在 text 字段有效
     * https://zincsearch-docs.zinc.dev/api/analyze/
     */
    private String analyzer;

    /**
     * 只在 text 字段有效
     * https://zincsearch-docs.zinc.dev/api/index/analyze/
     */
    private String searchAnalyzer;


    public static DocumentField of(String name, String type) {
        return new DocumentField().setName(name).setType(type);
    }

    public static DocumentField of(String name) {
        return new DocumentField().setName(name).setType("text");
    }

    public static DocumentField ofNumeric(String name) {
        return new DocumentField().setName(name).setType("numeric").setSortable(true);
    }

    public static DocumentField ofKeyword(String name) {
        return new DocumentField().setName(name).setType("keyword");
    }


    public String getAnalyzer() {
        return analyzer;
    }

    public DocumentField setAnalyzer(String analyzer) {
        this.analyzer = analyzer;
        return this;
    }

    public String getSearchAnalyzer() {
        return searchAnalyzer;
    }

    public DocumentField setSearchAnalyzer(String searchAnalyzer) {
        this.searchAnalyzer = searchAnalyzer;
        return this;
    }

    /**
     * 转为 json object
     *
     * @return
     */
    public JsonObject toJsonObject() {
        JsonObject inner = new JsonObject();
        inner.addProperty("type", this.getType());
        inner.addProperty("index", this.isIndex());
        inner.addProperty("store", this.isStore());
        inner.addProperty("sortable", this.isSortable());
        inner.addProperty("aggregatable", this.isAggregatable());
        inner.addProperty("highlightable", this.isHighlightable());
        if (isNotBlank(this.getAnalyzer())) {
            inner.addProperty("analyzer", this.getAnalyzer());
        }
        if (isNotBlank(this.getSearchAnalyzer())) {
            inner.addProperty("search_analyzer", this.getSearchAnalyzer());
        }
        return inner;
    }


    public String getName() {
        return name;
    }

    public DocumentField setName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public DocumentField setType(String type) {
        this.type = type;
        return this;
    }

    public boolean isIndex() {
        return index;
    }

    public DocumentField setIndex(boolean index) {
        this.index = index;
        return this;
    }

    public boolean isStore() {
        return store;
    }

    public DocumentField setStore(boolean store) {
        this.store = store;
        return this;
    }

    public boolean isSortable() {
        return sortable;
    }

    public DocumentField setSortable(boolean sortable) {
        this.sortable = sortable;
        return this;
    }

    public boolean isAggregatable() {
        return aggregatable;
    }

    public DocumentField setAggregatable(boolean aggregatable) {
        this.aggregatable = aggregatable;
        return this;
    }

    public boolean isHighlightable() {
        return highlightable;
    }

    public DocumentField setHighlightable(boolean highlightable) {
        this.highlightable = highlightable;
        return this;
    }

    public boolean isDateField() {
        return false;
    }

    public boolean isDateOrNumeric() {
        return ZincFieldTypes.numeric.name().equals(this.type) ||
                ZincFieldTypes.date.name().equals(this.type);
    }
}
