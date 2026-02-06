package org.winterfell.misc.indigo.renderer.ext.pptx.template.impl;

import org.apache.poi.xslf.usermodel.XSLFTable;
import org.winterfell.misc.indigo.renderer.ext.pptx.render.policy.SsRenderPolicy;
import org.winterfell.misc.indigo.renderer.ext.pptx.render.policy.SsRenderPolicyFactory;
import org.winterfell.misc.indigo.renderer.ext.pptx.render.policy.impl.SsTableRenderPolicy;
import org.winterfell.misc.indigo.renderer.ext.pptx.template.SsElementTemplate;

/**
 * 表格模板
 * 用于现有表格的行列生成!
 *
 * @author alex
 * @version v1.0 2020/11/18
 */
public class SsTableTemplate extends SsElementTemplate {

    private XSLFTable table;

    public XSLFTable getTable() {
        return table;
    }

    public SsTableTemplate setTable(XSLFTable table) {
        this.table = table;
        return this;
    }

    @Override
    public SsRenderPolicy findPolicy() {
        return SsRenderPolicyFactory.INSTANCE.get(SsTableRenderPolicy.class);
    }
}