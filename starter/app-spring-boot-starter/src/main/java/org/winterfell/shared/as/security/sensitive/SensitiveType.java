package org.winterfell.shared.as.security.sensitive;

/**
 * <p>
 * 常见的敏感数据类型
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/8/2
 */
public enum SensitiveType {

    /**
     * 空白，由注解的其他属性进行定义
     */
    EMPTY("", new int[]{0}, "*"),

    /**
     * 密码 全文替换
     */
    PASSWORD("(\\w+)", new int[]{0}, "*"),

    /**
     * 地址类型 保留前6位 其他*
     */
    ADDRESS("(.{6})(.+)", new int[]{2}, "*"),

    /**
     * 中文名字, 保留姓氏 其他*
     */
    CHINESE_NAME("(.{1})(.+)", new int[]{2}, "*"),

    /**
     * 身份证号 显示前 1 位和后 1 位，其余*
     */
    ID_CARD("(\\w{1})(\\w+)(\\w{1})", new int[]{2}, "*"),

    /**
     * 车牌号 地区信息+车牌后三位显示明文，其他都用*展示
     */
    CAR_NUMBER("^[\\u4e00-\\u9fa5]\\s*[A-Z]\\s*([A-Z0-9]{2,})(\\w{3})$", new int[]{1}, "*"),

    /**
     * 银行卡号 只显示最后 4位，其余*显示, 适用于信用卡和储蓄卡
     */
    BANK_CARD("^(\\d+)(\\d{4})$", new int[]{1}, "*"),

    /**
     * 手机号 显示前 3 位和后 2 位，其余*显示。适用于大陆手机号
     */
    MOBILE("(\\w{3})(\\w+)(\\w{2})", new int[]{2}, "*"),

    /**
     * 邮箱地址，保留后两位
     */
    EMAIL("(\\w+)\\w{2}@(.+)", new int[]{1}, "*");

    /**
     * 自定义正则
     */
    private String pattern;

    /**
     * 正则表达式的第几个分组;该分组将被替换为掩码mask
     */
    private int[] group;

    /**
     * 替换后的字符
     */
    private String mask;

    SensitiveType(String pattern, int[] group, String mask) {
        this.pattern = pattern;
        this.group = group;
        this.mask = mask;
    }

    public SensitiveType setPattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    public SensitiveType setGroup(int[] group) {
        this.group = group;
        return this;
    }

    public SensitiveType setMask(String mask) {
        this.mask = mask;
        return this;
    }

    public String getPattern() {
        return pattern;
    }

    public String getMask() {
        return mask;
    }

    public int[] getGroup() {
        return group;
    }
}
