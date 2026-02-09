package org.winterfell.misc.hutool.mini.convert.impl;

import org.winterfell.misc.hutool.mini.convert.AbstractConverter;

import java.util.Currency;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/3/5
 */
public class CurrencyConverter extends AbstractConverter<Currency> {

    @Override
    protected Currency convertInternal(Object value) {
        return Currency.getInstance(value.toString());
    }

}
