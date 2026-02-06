package org.winterfell.misc.indigo.renderer.support;

import com.deepoove.poi.data.*;
import com.deepoove.poi.util.BufferedImageUtils;
import com.google.common.collect.Lists;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.winterfell.misc.hutool.mini.MapUtil;
import org.winterfell.misc.hutool.mini.ReUtil;
import org.winterfell.misc.indigo.renderer.ext.docx.ObjectsRenderData;
import org.winterfell.misc.indigo.renderer.ext.docx.policy.DocxFormattedDateRenderData;
import org.winterfell.misc.indigo.renderer.ext.docx.policy.DocxRichTableRenderData;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <p>
 * .
 * </p>
 *
 * @author <a href="mailto:yingxiufeng@mlogcn.com">alex</a>
 * @version v1.0, 2020/6/11
 */
public final class DocxRenderUtil {

    private static final Pattern URL_PATTERN = Pattern.compile("^(http|https)://\\S+");

    private static final String DOC_TPL_TAG_DATETIME = "datetime";

    /**
     * 转换成渲染引擎可读的map对象
     * TODO
     * - 表格数据渲染
     * - 列表内渲染对象数据
     *
     * @param map
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Map process(@NonNull Map map) {
        Map<String, Object> ret = new HashMap<>(map.size());
        map.keySet().forEach(k -> {
            String key = String.valueOf(k);
            Object val = map.get(k);
            if (val instanceof TplRenderData) {
                TplRenderData renderObj = (TplRenderData) val;
                RenderData renderData = null;
                switch (renderObj.getType()) {
                    case control:
                        break;
                    case sp_el:
                        break;
                    case list:
                        List<String> list = (List<String>) renderObj.getValue();
                        renderData = new NumberingRenderData(NumberingFormat.BULLET,
                                list.stream().map(TextRenderData::new).collect(Collectors.toList()).toArray(new TextRenderData[]{}));
                        break;
                    case table:
                        // TODO 自动生成(n*n)表格

                        break;
                    case table_custom:
                        List<List<String>> objValue = (List<List<String>>) renderObj.getValue();
                        DocxRichTableRenderData customTableRenderData = new DocxRichTableRenderData()
                                .setStyle(renderObj.getStyle())
                                .fromProperties(renderObj.getProperties());
                        customTableRenderData.setRowData(customTableRenderData.isReverseOrder() ? Lists.reverse(objValue) : objValue);
                        renderData = customTableRenderData;
                        break;
                    case date:
                        Object renderObjValue = renderObj.getValue();
                        renderData = Objects.isNull(renderObj.getPattern()) ? DocxFormattedDateRenderData.of(Long.parseLong(renderObjValue.toString())) :
                                new DocxFormattedDateRenderData(renderObj.getPattern(), Long.valueOf(renderObjValue.toString()));
                        break;
                    case picture:
                        String picUrlOrPath = (String) renderObj.getValue();
                        BufferedImage image = ReUtil.isMatch(URL_PATTERN, picUrlOrPath) ? BufferedImageUtils.getUrlBufferedImage(picUrlOrPath) :
                                BufferedImageUtils.getLocalBufferedImage(new File(picUrlOrPath));
                        Map prop = renderObj.getProperties();
                        int width = image.getWidth();
                        int height = image.getHeight();
                        if (prop != null) {
                            if (prop.containsKey("width")) {
                                width = MapUtil.getInt(prop, "width");
                            }
                            if (prop.containsKey("height")) {
                                height = MapUtil.getInt(prop, "height");
                            }
                        }
                        renderData = new FilePictureRenderData(width, height, picUrlOrPath);
                        break;
                    case text:
                    default:
                        if (Objects.equals(key, DOC_TPL_TAG_DATETIME)) {
                            // 单独处理时间数据
                            Object value = renderObj.getValue();
                            if (value instanceof Map) {
                                Map tmp = (Map) value;
                                renderData = new DocxFormattedDateRenderData(MapUtil.getStr(tmp, "pattern"),
                                        MapUtil.getLong(tmp, "timestamp"));

                            } else {
                                renderData = DocxFormattedDateRenderData.of(Long.valueOf(value.toString()));
                            }

                        } else {
                            renderData = new TextRenderData((String) renderObj.getValue(), renderObj.getStyle());
                        }
                        break;
                }
                if (TplRenderData.Type.sp_el.equals(renderObj.getType()) ||
                        TplRenderData.Type.control.equals(renderObj.getType())) {
                    // 返回实际值
                    ret.put(key, renderObj.getValue());
                } else {
                    ret.put(key, renderData);
                }

            } else if (val instanceof ObjectsRenderData) {
                ret.put(key, ((ObjectsRenderData) val).getValue());

            } else {
                ret.put(key, val);
            }
        });
        return ret;
    }
}