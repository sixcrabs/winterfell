package org.winterfell.misc.indigo.renderer.ext.pptx.template;


import org.winterfell.misc.indigo.renderer.ext.pptx.render.policy.SsRenderPolicy;

/**
 * 元素模板
 * prefix + sign + tagName + suffix == source (eg: `{{var}}`)
 * @author alex
 * @version v1.0 2020/11/18
 */
public abstract class SsElementTemplate implements SsMetaTemplate {

    /**
     * 标识字符
     * - @ 表示插入图片
     * - # 表示插入表格
     * - * 表示插入列表
     * - 空 表示渲染文本 或替换已有图片、表格
     */
    protected Character sign;

    /**
     * 标记名称(数据的key)
     */
    protected String tagName;

    /**
     * `{{` sign + tagName + `}}`
     */
    protected String source;

    /**
     * find policy
     * @return
     */
    public abstract SsRenderPolicy findPolicy();


    /**
     * 变量名称
     *
     * @return
     */
    @Override
    public String variable() {
        return source;
    }

    @Override
    public String toString() {
        return "SsElementTemplate{" +
                "source='" + source + '\'' +
                '}';
    }

    public Character getSign() {
        return sign;
    }

    public SsElementTemplate setSign(Character sign) {
        this.sign = sign;
        return this;
    }

    /**
     * key 值
     * @return
     */
    public String getTagName() {
        return tagName;
    }

    public SsElementTemplate setTagName(String tagName) {
        this.tagName = tagName;
        return this;
    }

    public String getSource() {
        return source;
    }

    public SsElementTemplate setSource(String source) {
        this.source = source;
        return this;
    }


}