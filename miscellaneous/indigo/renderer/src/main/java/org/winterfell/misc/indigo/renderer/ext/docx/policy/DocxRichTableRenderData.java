package org.winterfell.misc.indigo.renderer.ext.docx.policy;

import com.deepoove.poi.data.RenderData;
import com.deepoove.poi.data.style.Style;
import org.winterfell.misc.hutool.mini.MapUtil;

import java.util.*;

/**
 * <p>
 * 定制表格渲染数据
 * {@link DocxRichTableRenderPolicy}
 * </p>
 *
 * @author alex
 * @version v1.0, 2019/9/16
 */
public class DocxRichTableRenderData implements RenderData {

    /**
     * 行数据
     * eg [["alex","30"],["kobe", "41"]]
     */
    private List<List<String>> rowData = new ArrayList<>(0);

    /**
     * 合并哪一列的行数据
     */
    private int rowMergeCol = -1;

    /**
     * 合并行开始行
     * 默认从第一行开始应用合并
     */
    private int rowMergeStart = 0;

    /**
     * 合并行结束行
     * 默认-1 表示应用整个表格
     */
    private int rowMergeEnd = -1;

    /**
     * 合并行步长
     * 1- 合并上下两行
     * 2- 合并三行
     * n- 合并 n+1 行
     */
    private int rowMergeStep = 1;

    /**
     * 是否逆序插入行数据
     */
    private boolean reverseOrder = true;

    /**
     * 字体样式
     */
    private Style style;

    /**
     * 自由定义行合并
     * <br/>
     * 当启用此属性时，忽略上述行合并属性
     * <p>
     *     eg:
     *     [[0,2],[3,6],...]
     *     [0,2]--- 0 表示开始行, 2 表示结束行
     *     后面数据的前一值需大于前面数据的后一值
     * </p>
     */
    private List<List<Integer>> freeRowMerge = new ArrayList<>(1);


    /**
     * 定义列合并
     * <p>
     *     eg:
     *     {0:[2,3]} 表示合第一行的 2 和 3 列
     * </p>
     * <b>注：[x,y] 中 x > y </b>
     */
    private Map<Integer, List<Integer>> freeColMerge = new LinkedHashMap<>(1);

    /**
     * 属性
     */
    private Map<String, ?> properties = Collections.emptyMap();

    /**
     * 解析属性
     *
     * @param properties
     */
    public DocxRichTableRenderData fromProperties(Map<String, ?> properties) {
        if (properties != null) {
            if (properties.containsKey("rowMergeCol")) {
                this.setRowMergeCol(MapUtil.getInt(properties, "rowMergeCol"));
            }

            if (properties.containsKey("rowMergeStart")) {
                this.setRowMergeStart(MapUtil.getInt(properties, "rowMergeStart"));
            }

            if (properties.containsKey("rowMergeEnd")) {
                this.setRowMergeStart(MapUtil.getInt(properties, "rowMergeEnd"));
            }

            if (properties.containsKey("rowMergeStep")) {
                this.setRowMergeStart(MapUtil.getInt(properties, "rowMergeStep"));
            }

            if (properties.containsKey("reverseOrder")) {
                this.setReverseOrder(MapUtil.getBool(properties, "reverseOrder"));
            }

            if (properties.containsKey("freeRowMerge")) {
                this.setFreeRowMerge(MapUtil.get(properties, "freeRowMerge", List.class));
            }

            this.properties = properties;
        }
        return this;
    }

    public List<List<String>> getRowData() {
        return rowData;
    }

    public DocxRichTableRenderData setRowData(List<List<String>> rowData) {
        this.rowData = rowData;
        return this;
    }

    public int getRowMergeCol() {
        return rowMergeCol;
    }

    public DocxRichTableRenderData setRowMergeCol(int rowMergeCol) {
        this.rowMergeCol = rowMergeCol;
        return this;
    }

    public int getRowMergeStart() {
        return rowMergeStart;
    }

    public DocxRichTableRenderData setRowMergeStart(int rowMergeStart) {
        this.rowMergeStart = rowMergeStart;
        return this;
    }

    public int getRowMergeEnd() {
        return rowMergeEnd;
    }

    public DocxRichTableRenderData setRowMergeEnd(int rowMergeEnd) {
        this.rowMergeEnd = rowMergeEnd;
        return this;
    }

    public int getRowMergeStep() {
        return rowMergeStep;
    }

    public DocxRichTableRenderData setRowMergeStep(int rowMergeStep) {
        this.rowMergeStep = rowMergeStep;
        return this;
    }

    public boolean isReverseOrder() {
        return reverseOrder;
    }

    public DocxRichTableRenderData setReverseOrder(boolean reverseOrder) {
        this.reverseOrder = reverseOrder;
        return this;
    }

    public Style getStyle() {
        return style;
    }

    public DocxRichTableRenderData setStyle(Style style) {
        this.style = style;
        return this;
    }

    public List<List<Integer>> getFreeRowMerge() {
        return freeRowMerge;
    }

    public DocxRichTableRenderData setFreeRowMerge(List<List<Integer>> freeRowMerge) {
        this.freeRowMerge = freeRowMerge;
        return this;
    }

    public Map<Integer, List<Integer>> getFreeColMerge() {
        return freeColMerge;
    }

    public DocxRichTableRenderData setFreeColMerge(Map<Integer, List<Integer>> freeColMerge) {
        this.freeColMerge = freeColMerge;
        return this;
    }

    public Map<String, ?> getProperties() {
        return properties;
    }

    public DocxRichTableRenderData setProperties(Map<String, ?> properties) {
        this.properties = properties;
        return this;
    }

    //    /**
//     * 列合并开始列序号
//     * 默认-1 表示不进行列合并
//     */
//    private int colMergeStart = -1;
//
//    /**
//     * 列合并结束列序号
//     * 默认-1 表示不进行列合并
//     */
//    private int colMergeEnd = -1;

}