package org.winterfell.misc.indigo.renderer.ext.docx.policy;


import org.winterfell.misc.indigo.renderer.ext.docx.DocxCustomRenderData;
import org.winterfell.misc.indigo.renderer.support.DateTimeUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * <p>
 * 定制时间字段格式
 * </p>
 *
 * @author alex
 * @version v1.0 2022/4/19
 */
public class DocxFormattedDateRenderData implements DocxCustomRenderData<Long> {

    /**
     * 时间格式
     */
    private String pattern = "yyyy-MM-dd HH:mm:ss";

    private Long value;

    public DocxFormattedDateRenderData() {
    }

    public DocxFormattedDateRenderData(String pattern, Long value) {
        this.pattern = pattern;
        this.value = value;
    }

    public static DocxFormattedDateRenderData of(LocalDate date) {
        return new DocxFormattedDateRenderData()
                .setValue(DateTimeUtil.toTimestamp(LocalDateTime.of(date, LocalTime.of(0, 0, 0))));
    }

    public static DocxFormattedDateRenderData of(LocalDateTime dateTime) {
        return new DocxFormattedDateRenderData().setValue(DateTimeUtil.toTimestamp(dateTime));
    }

    public static DocxFormattedDateRenderData of(long timestamp) {
        return new DocxFormattedDateRenderData().setValue(timestamp);
    }


    public String getPattern() {
        return pattern;
    }

    /**
     * 设置渲染的时间格式
     *
     * @param pattern
     * @return
     */
    public DocxFormattedDateRenderData setPattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    private DocxFormattedDateRenderData setValue(Long value) {
        this.value = value;
        return this;
    }

    @Override
    public Long getValue() {
        return value;
    }
}