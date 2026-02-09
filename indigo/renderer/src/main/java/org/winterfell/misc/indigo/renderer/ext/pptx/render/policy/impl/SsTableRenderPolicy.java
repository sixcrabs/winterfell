package org.winterfell.misc.indigo.renderer.ext.pptx.render.policy.impl;

import com.google.auto.service.AutoService;
import org.apache.poi.sl.usermodel.VerticalAlignment;
import org.apache.poi.xslf.usermodel.XSLFTable;
import org.apache.poi.xslf.usermodel.XSLFTableCell;
import org.apache.poi.xslf.usermodel.XSLFTableRow;
import org.winterfell.misc.indigo.renderer.ext.pptx.render.DefaultSsRenderDataCompute;
import org.winterfell.misc.indigo.renderer.ext.pptx.render.RenderContext;
import org.winterfell.misc.indigo.renderer.ext.pptx.render.SsRenderDataCompute;
import org.winterfell.misc.indigo.renderer.ext.pptx.render.data.SsTableRenderData;
import org.winterfell.misc.indigo.renderer.ext.pptx.render.policy.AbstractSsRenderPolicy;
import org.winterfell.misc.indigo.renderer.ext.pptx.render.policy.SsRenderPolicy;
import org.winterfell.misc.indigo.renderer.ext.pptx.template.SsElementTemplate;
import org.winterfell.misc.indigo.renderer.ext.pptx.template.impl.SsTableTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * 实现表格渲染
 * TODO
 *
 * @author alex
 * @version v1.0 2020/11/19
 */
@AutoService(SsRenderPolicy.class)
public class SsTableRenderPolicy extends AbstractSsRenderPolicy<SsTableRenderData> {
    /**
     * 实现渲染
     * - 渲染占位符表格
     * - 插入表格
     *
     * @param context
     * @throws Exception
     */
    @Override
    public void doRender(RenderContext<SsTableRenderData> context) throws Exception {
        SsElementTemplate elementTemplate = context.getElementTemplate();
        SsTableRenderData renderData = context.getData();
        if (elementTemplate instanceof SsTableTemplate) {
            logger.info("render table template...");
            SsTableTemplate tableTemplate = (SsTableTemplate) elementTemplate;
            XSLFTable table = tableTemplate.getTable();
            // 如果没有头部 则只渲染 rows
            List<SsTableRenderData.HeaderItem> header = renderData.getHeader();
            List<Object> rows = renderData.getRows();
            // 取最后一行第一个单元格的字体
            double fontSize = table.getCell(table.getNumberOfRows() - 1, 0).getTextParagraphs().get(0).getDefaultFontSize();
            if (header != null && !header.isEmpty()) {
                // // 移除现有的row
                // for (int i = 1; i < table.getNumberOfRows(); i++) {
                //     table.removeRow(i);
                // }
                // 添加header TBD: 默认取第一行作为标题行
                XSLFTableRow headerRow = table.getRows().get(0);
                for (int i = 0; i < header.size(); i++) {
                    XSLFTableCell cell = headerRow.getCells().get(i);
                    if (cell == null) {
                        cell = headerRow.insertCell(i);
                    }
                    // 水平 垂直居中 加粗
                    cell.setHorizontalCentered(true);
                    cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
                    cell.setText(header.get(i).getTitle()).setBold(true);
                    cell.getTextParagraphs().get(0).getTextRuns().get(0).setFontSize(fontSize);
                }
            }
            int colNum = table.getNumberOfColumns();

            SsRenderDataCompute dataCompute;
            for (int j = 0; j < rows.size(); j++) {
            // for (Object rowData : rows) {
                Object rowData = rows.get(j);
                XSLFTableRow row = null;
                if (j + 1 < table.getNumberOfRows()) {
                    row = table.getRows().get(j + 1);
                } else {
                    row = table.addRow();
                }
                XSLFTableCell cell;
                if (rowData instanceof String) {
                    //逗号分隔
                    List<String> strings = Arrays.asList(String.valueOf(rowData).split(",", -1));
                    // 只渲染现有列 数据里超出部分不会添加
                    for (int i = 0; i < colNum; i++) {
                        if (row.getCells().size() == colNum) {
                            cell = row.getCells().get(i);
                        } else {
                            cell = row.insertCell(i);
                        }
                        cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
                        cell.setText(strings.get(i));
                        cell.getTextParagraphs().get(0).getTextRuns().get(0).setFontSize(fontSize);
                    }
                } else {
                    dataCompute = new DefaultSsRenderDataCompute(rowData, false);
                    if (header != null && !header.isEmpty()) {
                        for (int i = 0; i < colNum; i++) {
                            SsTableRenderData.HeaderItem headerItem = header.get(i);
                            if (headerItem.getKey() != null) {
                                Object val = dataCompute.compute(headerItem.getKey());
                                if (row.getCells().size() == colNum) {
                                    cell = row.getCells().get(i);
                                } else {
                                    cell = row.insertCell(i);
                                }
                                cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
                                cell.setText(String.valueOf(val));
                                cell.getTextParagraphs().get(0).getTextRuns().get(0).setFontSize(fontSize);
                            } else {
                                logger.warn("无法渲染cell, header item of [{}] key is null", headerItem);
                            }
                        }
                    } else {
                        // TBD:
                        logger.warn("无法渲染cell, header item 缺少 key");
                    }
                }
            }
        } else {
            // TODO
            logger.warn("[WIP] 渲染插入表格...");
        }
        logger.info("render table template successfully");
    }
}