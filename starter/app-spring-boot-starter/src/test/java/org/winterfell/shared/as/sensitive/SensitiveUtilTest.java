package org.winterfell.shared.as.sensitive;

import org.winterfell.shared.as.security.sensitive.SensitiveType;
import org.winterfell.shared.as.security.sensitive.SensitiveUtil;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/8/2
 */
class SensitiveUtilTest {

    @Test
    void desensitize() {

        System.out.println(SensitiveUtil.desensitize("yxfacw@163.com",
                SensitiveType.EMAIL.getPattern(), new int[]{1}, "*"));

        System.out.println(SensitiveUtil.desensitize("南京市雨花区赛虹桥街道12号",
                SensitiveType.ADDRESS.getPattern(), SensitiveType.ADDRESS.getGroup(), "*"));

        System.out.println(SensitiveUtil.desensitize("麦小迪",
                SensitiveType.CHINESE_NAME.getPattern(), SensitiveType.CHINESE_NAME.getGroup(), "*"));

        System.out.println(SensitiveUtil.desensitize("341191199304136213",
                SensitiveType.ID_CARD.getPattern(), SensitiveType.ID_CARD.getGroup(), "*"));

        System.out.println(SensitiveUtil.desensitize("15151890742",
                SensitiveType.MOBILE.getPattern(), SensitiveType.MOBILE.getGroup(), "*"));

        System.out.println(SensitiveUtil.desensitize("123456",
                SensitiveType.PASSWORD.getPattern(), SensitiveType.PASSWORD.getGroup(), "*"));

        System.out.println(SensitiveUtil.desensitize("苏AT8J00",
                SensitiveType.CAR_NUMBER.getPattern(), SensitiveType.CAR_NUMBER.getGroup(), "*"));

        System.out.println(SensitiveUtil.desensitize("3423567467873312",
                SensitiveType.BANK_CARD.getPattern(), SensitiveType.BANK_CARD.getGroup(), "*"));
    }

    @Test
    void testCarNum() {

        String pattern = "^[\\u4e00-\\u9fa5]\\s*[A-Z]\\s*([A-Z0-9]{2,})(\\w{3})$"; //SensitiveType.CAR_NUMBER.getPattern();
        String rawVal = "苏A DX2123";
        Pattern pa = Pattern.compile(pattern);
        Matcher matcher = pa.matcher(rawVal);
        if (matcher.matches()) {
            int groupCount = matcher.groupCount();
            System.out.println(groupCount);
        }
    }
}