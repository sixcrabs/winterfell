package org.winterfell.misc.indigo.renderer.ext.docx.policy;

import com.deepoove.poi.policy.AbstractRenderPolicy;
import com.deepoove.poi.render.RenderContext;
import com.deepoove.poi.template.run.RunTemplate;
import com.google.auto.service.AutoService;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.winterfell.misc.indigo.renderer.ext.docx.DocxCustomRenderPolicy;
import org.winterfell.misc.indigo.renderer.support.DateTimeUtil;

import java.time.LocalDateTime;

/**
 * <p>
 * 支持格式化的时间字段渲染策略
 * 格式 {{datetime}}
 * 根据传入的 {@link DocxFormattedDateRenderData} 指定的pattern 格式化时间字段然后渲染
 * </p>
 *
 * @author alex
 * @version v1.0, 2019/9/6
 */
@AutoService(DocxCustomRenderPolicy.class)
public class DocxFormattedDateRenderPolicy extends AbstractRenderPolicy<DocxFormattedDateRenderData> implements DocxCustomRenderPolicy {

    /**
     * 文档中标记 eg `{{my}}`
     *
     * @return
     */
    @Override
    public String renderTag() {
        return "datetime";
    }

    @Override
    public Character renderPluginChar() {
        return null;
    }

    /**
     * 执行模板渲染
     *
     * @param context
     * @throws Exception
     */
    @Override
    public void doRender(RenderContext<DocxFormattedDateRenderData> context) throws Exception {
        XWPFRun run = ((RunTemplate) context.getEleTemplate()).getRun();
        DocxFormattedDateRenderData data = context.getData();
        LocalDateTime dateTime = DateTimeUtil.fromTimestamp(data.getValue() > 0 ? data.getValue() : System.currentTimeMillis());
        run.setText(DateTimeUtil.format(dateTime, data.getPattern()));
    }

    /**
     * 校验data model
     *
     * @param data
     * @return
     */
    @Override
    protected boolean validate(DocxFormattedDateRenderData data) {
        return super.validate(data);
    }

    @Override
    protected void beforeRender(RenderContext context) {
        super.beforeRender(context);
    }

    @Override
    protected void afterRender(RenderContext context) {
        // 清空模板标签所在段落
        clearPlaceholder(context, false);
    }
}