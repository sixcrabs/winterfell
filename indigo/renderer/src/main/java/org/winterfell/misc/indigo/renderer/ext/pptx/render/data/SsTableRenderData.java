package org.winterfell.misc.indigo.renderer.ext.pptx.render.data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 表格渲染数据
 *
 * @author alex
 * @version v1.0 2020/11/19
 */
public class SsTableRenderData implements SsRenderData {

    /**
     * 用来表示表格头
     */
    private List<HeaderItem> header;

    /**
     * 行数据
     * 对象可以是 string（逗号隔开） or map or pojo
     */
    private List<Object> rows;

    public SsTableRenderData() {
    }

    public SsTableRenderData(List<HeaderItem> header, List<Object> rows) {
        this.header = header;
        this.rows = rows;
    }

    public SsTableRenderData(List<Object> rows) {
        this.rows = rows;
    }

    public List<HeaderItem> getHeader() {
        return header;
    }

    public SsTableRenderData setHeader(List<HeaderItem> header) {
        this.header = header;
        return this;
    }

    public List<Object> getRows() {
        return rows;
    }

    public SsTableRenderData setRows(List<Object> rows) {
        this.rows = rows;
        return this;
    }

    @Override
    public String toString() {
        return "SsTableRenderData{" +
                header.stream()
                        .map(item -> (item.getKey() != null ? (item.getKey() + "-") : "").concat(item.getTitle()))
                        .collect(Collectors.joining(",")) +
                '}';
    }

    public static class HeaderItem {

        /**
         * 对应在数据对象里取值的 key
         * 如果为空 则表示直接通过 数据的 toString获取
         */
        private String key;

        /**
         * 表格列标题
         */
        private String title;

        public HeaderItem(String key, String title) {
            this.key = key;
            this.title = title;
        }

        public HeaderItem(String title) {
            this.title = title;
        }

        public String getKey() {
            return key;
        }

        public HeaderItem setKey(String key) {
            this.key = key;
            return this;
        }

        public String getTitle() {
            return title;
        }

        public HeaderItem setTitle(String title) {
            this.title = title;
            return this;
        }

        @Override
        public String toString() {
            return "HeaderItem{" +
                    "key='" + key + '\'' +
                    ", title='" + title + '\'' +
                    '}';
        }
    }


}