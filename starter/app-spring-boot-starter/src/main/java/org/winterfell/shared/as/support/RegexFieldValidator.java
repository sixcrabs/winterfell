package org.winterfell.shared.as.support;

import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * <p>
 * validator for {@linkplain RegexField}
 * </p>
 *
 * @author alex
 * @version v1.0, 2020/9/22
 */
public class RegexFieldValidator implements ConstraintValidator<RegexField, String> {

    private Pattern pattern;

    @Override
    public void initialize(RegexField annotation) {
        pattern = Pattern.compile(annotation.regex());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(value)) {
            return true;
        }
        return pattern.asPredicate().test(value);
    }
}
