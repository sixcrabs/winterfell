package org.winterfell.misc.hutool.mini;

import java.util.regex.Pattern;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/5/20
 */
public class ValidatorUtil {

    /**
     * 英文字母 、数字和下划线
     */
    public final static Pattern GENERAL = PatternPool.GENERAL;
    /**
     * 数字
     */
    public final static Pattern NUMBERS = PatternPool.NUMBERS;
    /**
     * 分组
     */
    public final static Pattern GROUP_VAR = PatternPool.GROUP_VAR;
    /**
     * IP v4
     */
    public final static Pattern IPV4 = PatternPool.IPV4;
    /**
     * IP v6
     */
    public final static Pattern IPV6 = PatternPool.IPV6;
    /**
     * 货币
     */
    public final static Pattern MONEY = PatternPool.MONEY;
    /**
     * 邮件
     */
    public final static Pattern EMAIL = PatternPool.EMAIL;

    /**
     * 邮件（包含中文）
     */
    public final static Pattern EMAIL_WITH_CHINESE = PatternPool.EMAIL_WITH_CHINESE;

    /**
     * 移动电话
     */
    public final static Pattern MOBILE = PatternPool.MOBILE;

    /**
     * 身份证号码
     */
    public final static Pattern CITIZEN_ID = PatternPool.CITIZEN_ID;

    /**
     * 邮编
     */
    public final static Pattern ZIP_CODE = PatternPool.ZIP_CODE;
    /**
     * 生日
     */
    public final static Pattern BIRTHDAY = PatternPool.BIRTHDAY;
    /**
     * URL
     */
    public final static Pattern URL = PatternPool.URL;
    /**
     * Http URL
     */
    public final static Pattern URL_HTTP = PatternPool.URL_HTTP;
    /**
     * 中文字、英文字母、数字和下划线
     */
    public final static Pattern GENERAL_WITH_CHINESE = PatternPool.GENERAL_WITH_CHINESE;
    /**
     * UUID
     */
    public final static Pattern UUID = PatternPool.UUID;
    /**
     * 不带横线的UUID
     */
    public final static Pattern UUID_SIMPLE = PatternPool.UUID_SIMPLE;
    /**
     * 中国车牌号码
     */
    public final static Pattern PLATE_NUMBER = PatternPool.PLATE_NUMBER;
    /**
     * 车架号;别名：车辆识别代号 车辆识别码；十七位码
     */
    public final static Pattern CAR_VIN = PatternPool.CAR_VIN;
    /**
     * 驾驶证  别名：驾驶证档案编号、行驶证编号；12位数字字符串；仅限：中国驾驶证档案编号
     */
    public final static Pattern CAR_DRIVING_LICENCE = PatternPool.CAR_DRIVING_LICENCE;

    /**
     * 验证是否为Hex（16进制）字符串
     *
     * @param value 值
     * @return 是否为Hex（16进制）字符串
     * @since 4.3.3
     */
    public static boolean isHex(CharSequence value) {
        return isMatchRegex(PatternPool.HEX, value);
    }

    /**
     * 验证是否为可用邮箱地址
     *
     * @param value 值
     * @return true为可用邮箱地址
     */
    public static boolean isEmail(CharSequence value) {
        return isMatchRegex(EMAIL, value);
    }

    /**
     * 验证是否为可用邮箱地址（兼容中文邮箱地址）
     *
     * @param value 值
     * @param includChinese 包含中文标识
     * @return true为可用邮箱地址
     */
    public static boolean isEmail(CharSequence value,boolean includChinese) {
        if (includChinese){
            return isMatchRegex(EMAIL_WITH_CHINESE, value);
        }
        return isEmail(value);
    }

    /**
     * 验证是否为可用邮箱地址
     *
     * @param <T>      字符串类型
     * @param value    值
     * @param errorMsg 验证错误的信息
     * @return 验证后的值
     * @throws ValidateException 验证异常
     */
    public static <T extends CharSequence> T validateEmail(T value, String errorMsg) throws ValidateException {
        if (!isEmail(value)) {
            throw new ValidateException(errorMsg);
        }
        return value;
    }

    /**
     * 通过正则表达式验证
     *
     * @param pattern 正则模式
     * @param value   值
     * @return 是否匹配正则
     */
    public static boolean isMatchRegex(Pattern pattern, CharSequence value) {
        return ReUtil.isMatch(pattern, value);
    }


    /**
     * 通过正则表达式验证
     *
     * @param regex 正则
     * @param value 值
     * @return 是否匹配正则
     */
    public static boolean isMatchRegex(String regex, CharSequence value) {
        return ReUtil.isMatch(regex, value);
    }

    /**
     * 验证是否为英文字母 、数字和下划线
     *
     * @param value 值
     * @return 是否为英文字母 、数字和下划线
     */
    public static boolean isGeneral(CharSequence value) {
        return isMatchRegex(GENERAL, value);
    }

}
