package org.winterfell.misc.indigo.renderer.ext.docx.policy;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.data.*;
import com.deepoove.poi.data.style.CellStyle;
import com.deepoove.poi.data.style.ParagraphStyle;
import com.deepoove.poi.data.style.Style;
import com.deepoove.poi.policy.TableRenderPolicy;
import com.deepoove.poi.template.ElementTemplate;
import com.deepoove.poi.template.run.RunTemplate;
import com.deepoove.poi.util.TableTools;
import com.deepoove.poi.xwpf.NiceXWPFDocument;
import com.google.auto.service.AutoService;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.xwpf.usermodel.XWPFTableCell.XWPFVertAlign;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.winterfell.misc.hutool.mini.MapUtil;
import org.winterfell.misc.indigo.renderer.ext.docx.DocxCustomRenderPolicy;
import org.winterfell.misc.indigo.renderer.support.IndigoRenderException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 定制的表格渲染策略
 * 模板格式 `{{%rich_table}}`
 * <br/>
 * - 支持行合并
 * - 支持列合并
 *
 * </p>
 *
 * @author alex
 * @version v1.0, 2019/9/16
 */
@AutoService(DocxCustomRenderPolicy.class)
public class DocxRichTableRenderPolicy implements DocxCustomRenderPolicy {

    /**
     * 渲染
     *
     * @param eleTemplate
     * @param data
     * @param template
     */
    @Override
    public void render(ElementTemplate eleTemplate, Object data, XWPFTemplate template) {
        DocxRichTableRenderData tableRenderData = (DocxRichTableRenderData) data;
        NiceXWPFDocument doc = template.getXWPFDocument();
        RunTemplate runTemplate = (RunTemplate) eleTemplate;
        XWPFRun run = runTemplate.getRun();
        run.setText("", 0);
        try {
            // w:tbl-w:tr-w:tc-w:p-w:tr
            XmlCursor newCursor = ((XWPFParagraph) run.getParent()).getCTP().newCursor();
            newCursor.toParent();
            newCursor.toParent();
            newCursor.toParent();
            XmlObject object = newCursor.getObject();
            XWPFTable table = doc.getTable((CTTbl) object);
            doRender(table, tableRenderData);
        } catch (Exception e) {
            throw new IndigoRenderException("dynamic table error:" + e.getMessage());
        }
    }

    /**
     * 执行自定义表格渲染
     *
     * @param table 表格
     * @param data  数据
     */
    private void doRender(XWPFTable table, @NonNull DocxRichTableRenderData data) {

        List<List<String>> rowList = data.getRowData();
        if (rowList.isEmpty()) {
            throw new IndigoRenderException("row data cannot be empty！");
        }
        int cols = 0;
        // 默认表格都具有表头，故数据从1开始
        int startRow = 1;
        table.removeRow(startRow);
        for (int i = 0; i < rowList.size(); i++) {
            XWPFTableRow insertNewTableRow = table.insertNewTableRow(i + 1);
            if (cols == 0) {
                cols = rowList.get(i).size();
            }
            for (int j = 0; j < cols; j++) {
                insertNewTableRow.createCell();
            }
            try {
                TableRenderPolicy.Helper.renderRow(insertNewTableRow, createRowRenderData(rowList.get(i),
                        data.getStyle(), resolveParaStyle(data),
                        data.getProperties().containsKey("rowHeight") ? MapUtil.getDouble(data.getProperties(), "rowHeight") : 0.0D));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 合并行
        if (!data.getFreeRowMerge().isEmpty()) {
            AtomicInteger cursor = new AtomicInteger();
            data.getFreeRowMerge().forEach(nums -> {
                if (nums.get(0) >= cursor.get() && nums.get(1) > nums.get(0)) {
                    // 保证后一个合并的起始行大于前一个的结束行
                    TableTools.mergeCellsVertically(table, data.getRowMergeCol(), startRow + nums.get(0), startRow + nums.get(1));
                    cursor.set(nums.get(1));
                }
            });
        } else {
            if (data.getRowMergeCol() > -1) {
                int rows = table.getRows().size();
                for (int i = startRow + data.getRowMergeStart(); i < rows; i++) {
                    TableTools.mergeCellsVertically(table, data.getRowMergeCol(), i, i + data.getRowMergeStep());
                    i = i + data.getRowMergeStep();
                }
            }
        }

        // 合并列
        if (!data.getFreeColMerge().isEmpty()) {
            AtomicInteger cursor = new AtomicInteger();
            data.getFreeColMerge().forEach((key, nums) -> {
                if (nums.get(0) >= cursor.get() && nums.get(1) > nums.get(0)) {
                    // 保证后一个合并的起始列大于前一个的结束列
                    TableTools.mergeCellsHorizonal(table,
                            key,
                            nums.get(0),
                            nums.get(1));
                    cursor.set(nums.get(1));
                }
            });
        }

    }


    /**
     * 从属性中解析 段落样式
     *
     * @param renderData
     * @return
     */
    private ParagraphStyle resolveParaStyle(DocxRichTableRenderData renderData) {
        Map<String, ?> properties = renderData.getProperties();
        if (properties.isEmpty()) {
            return null;
        }
        ParagraphStyle.Builder builder = ParagraphStyle.builder()
                .withAlign(ParagraphAlignment.CENTER)
                .withSpacing(1.0);
        if (properties.containsKey("spacing")) {
            builder.withSpacing(MapUtil.getDouble(properties, "spacing"));
        }
        if (properties.containsKey("align")) {
            try {
                builder.withAlign(ParagraphAlignment.valueOf(MapUtil.getStr(properties, "align").toUpperCase()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return builder.build();
    }

    /**
     * create render data for row
     *
     * @param data
     * @return
     */
    private RowRenderData createRowRenderData(List<String> data, Style textStyle, ParagraphStyle paragraphStyle, double rowHeight) {
        List<CellRenderData> cellRenderDataList = data.stream().map(val -> {
            TextRenderData textRenderData = new TextRenderData(val);
            if (textStyle != null) {
                textRenderData.setStyle(textStyle);
            }
            CellRenderData cellRenderData = new CellRenderData();
            CellStyle cellStyle = new CellStyle();
            cellStyle.setVertAlign(XWPFVertAlign.CENTER);
            if (paragraphStyle != null) {
                cellStyle.setDefaultParagraphStyle(paragraphStyle);
            } else {
                cellStyle.setDefaultParagraphStyle(ParagraphStyle.builder()
                        .withAlign(ParagraphAlignment.CENTER)
                        .withSpacing(1.0).build());
            }
            cellRenderData.addParagraph(new ParagraphRenderData().addText(textRenderData))
                    .setCellStyle(cellStyle);
            return cellRenderData;
        }).collect(Collectors.toList());
        Rows.RowBuilder rowBuilder = Rows.of();
        cellRenderDataList.forEach(rowBuilder::addCell);
        rowBuilder.center();
        if (rowHeight > 0) {
            rowBuilder.rowExactHeight(rowHeight);
        }
        return rowBuilder.create();

    }

    @Override
    public String renderTag() {
        return null;
    }

    /**
     * 文档中标记 eg `{{%rich_table}}`
     * @return
     */
    @Override
    public Character renderPluginChar() {
        return '%';
    }
}