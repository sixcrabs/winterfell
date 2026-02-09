package org.winterfell.misc.indigo.renderer.ext.pptx.support;

/**
 * 模板语法符号
 *
 * @author alex
 * @version v1.0 2020/11/18
 */
public enum TemplateGrammarSymbol {

    /**
     * Picture in the template
     * 图片
     */
    IMAGE('@'),

    /**
     * Text in the template
     * 文本
     */
    TEXT('\0'),

    /**
     * Table in the template
     * 表格
     */
    TABLE('#'),

    /**
     * Numbering in the template
     * 列表
     */
    NUMBERING('*');

    private char symbol;

    TemplateGrammarSymbol(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return String.valueOf(this.symbol);
    }
}