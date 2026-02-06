package org.winterfell.shared.as.security.sensitive;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * util
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/8/2
 */
public final class SensitiveUtil {

    private SensitiveUtil() {
    }

    /**
     * 脱敏
     *
     * @param rawVal
     * @param pattern
     * @param groupIndices
     * @param mask
     * @return
     */
    public static String desensitize(String rawVal, final String pattern, int[] groupIndices, final String mask) {
        String val = rawVal;
        if (pattern != null) {
            if (!pattern.isEmpty()) {
                Pattern pa = Pattern.compile(pattern);
                Matcher matcher = pa.matcher(rawVal);
                if (matcher.matches()) {
                    for (int i = 0; i < groupIndices.length; i++) {
                        String group = matcher.group(groupIndices[i]);
                        if (!mask.isEmpty() && !group.isEmpty()) {
                            val = val.replace(group, String.join("", Collections.nCopies(group.length(), mask)));
                        }
                    }
                }
            }
        }
        return val;
    }
}
