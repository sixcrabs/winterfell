package org.winterfell.misc.zinc.model;

import com.google.gson.JsonObject;

/**
 * <p>
 * 时间格式 字段
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/31
 */
public class DocumentDateTimeField extends DocumentField {

    private static final long serialVersionUID = -452243854862430476L;

    private String format = "2006-01-02 15:04:05";

    private String timeZone = "+08:00";

    public static DocumentDateTimeField of(String name) {
        return (DocumentDateTimeField) new DocumentDateTimeField().setName(name).setType("date");
    }

    public static DocumentDateTimeField ofDefault() {
        return (DocumentDateTimeField) new DocumentDateTimeField().setName("@timestamp").setIndex(true).setAggregatable(true).setStore(true).setType("date");
    }

    @Override
    public boolean isDateField() {
        return true;
    }

    /**
     * 转为 json object
     *
     * @return
     */
    @Override
    public JsonObject toJsonObject() {
        JsonObject jsonObject = super.toJsonObject();
        jsonObject.addProperty("format", this.getFormat());
        jsonObject.addProperty("time_zone", this.getTimeZone());
        return jsonObject;
    }

    @Override
    public String getType() {
        return "date";
    }

    public String getFormat() {
        return format;
    }

    public DocumentDateTimeField setFormat(String format) {
        this.format = format;
        return this;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public DocumentDateTimeField setTimeZone(String timeZone) {
        this.timeZone = timeZone;
        return this;
    }
}
