package org.winterfell.misc.indigo.renderer.support;

import com.deepoove.poi.data.RenderData;
import com.deepoove.poi.data.style.Style;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * for old methods ,will be deprecated in next big version
 * @author zyz
 */
public class TplRenderData<T> implements RenderData {

    /**
     * 渲染的数据类型 默认是 text
     *
     * @see Type
     */
    private Type type = Type.text;

    /**
     * 样式 可为null
     */
    private Style style;

    /**
     * 值 string/list/long/...
     */
    private T value;

    /**
     * 属性，如表格合并参数等
     */
    private Map<String, ?> properties;

    /**
     * 格式化，可对时间等数据进行格式化展示
     */
    private String pattern;

    public TplRenderData(Type type) {
        this.type = type;
    }

    public TplRenderData(T value) {
        this.value = value;
    }

    public TplRenderData(Type type, T value) {
        this.type = type;
        this.value = value;
    }

    public TplRenderData(Type type, T value, Map<String, ?> properties) {
        this.type = type;
        this.value = value;
        this.properties = properties;
    }

    public Type getType() {
        return type;
    }

    public TplRenderData<T> setType(Type type) {
        this.type = type;
        return this;
    }

    public Style getStyle() {
        return style;
    }

    public TplRenderData<T> setStyle(Style style) {
        this.style = style;
        return this;
    }

    public T getValue() {
        return value;
    }

    public TplRenderData<T> setValue(T value) {
        this.value = value;
        return this;
    }

    public Map<String, ?> getProperties() {
        return properties;
    }

    public TplRenderData<T> setProperties(Map<String, ?> properties) {
        this.properties = properties;
        return this;
    }

    public String getPattern() {
        return pattern;
    }

    public TplRenderData<T> setPattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    /**
     * set string value
     *
     * @param value
     * @return
     */
    public static TplRenderData of(String value) {
        return new TplRenderData(Type.text).setValue(value);
    }


    /**
     * set number data
     *
     * @param value
     * @return
     */
    public static TplRenderData ofNumber(Number value) {
        return of(String.valueOf(value));
    }

    public static TplRenderData ofControl(Boolean value) {
        return new TplRenderData(Type.control).setValue(value);
    }

    /**
     * set spring El value
     * @param value
     * @param <T>
     * @return
     */
    public static <T> TplRenderData ofSpringEL(T value) {
        return new TplRenderData(Type.sp_el).setValue(value);
    }

    /**
     * set array data
     *
     * @param value
     * @return
     */
    public static TplRenderData ofArray(String... value) {
        return new TplRenderData(Type.list).setValue(Arrays.asList(value));
    }

    /**
     * set list data
     *
     * @param value
     * @return
     */
    public static TplRenderData ofArray(List<String> value) {
        return new TplRenderData(Type.list).setValue(value);
    }

    /**
     * set date value
     *
     * @param timestamp
     * @param pattern   时间显示格式 eg `yyyy-MM-dd`
     * @return
     */
    public static TplRenderData ofDate(Long timestamp, String pattern) {
        return new TplRenderData(Type.date)
                .setValue(timestamp)
                .setPattern(pattern);
    }

    /**
     * set date value
     *
     * @param timestamp
     * @return
     */
    public static TplRenderData ofDate(Long timestamp) {
        return ofDate(timestamp, null);
    }

    /**
     * set date
     *
     * @param date
     * @return
     */
    public static TplRenderData ofDate(Date date) {
        return ofDate(date.getTime());
    }


    public enum Type {
        /**
         * 文本
         */
        text,
        /**
         * 时间
         */
        date,
        /**
         * 列表
         */
        list,
        /**
         * 表格数据
         */
        table,
        /**
         * 自定义表格类型
         */
        table_custom,
        /**
         * 图片类型
         */
        picture,

        /**
         * 控制变量 用于控制区块对的显示(true)或隐藏(false)
         */
        control,

        /**
         * spring el 数据
         */
        sp_el;
    }
}