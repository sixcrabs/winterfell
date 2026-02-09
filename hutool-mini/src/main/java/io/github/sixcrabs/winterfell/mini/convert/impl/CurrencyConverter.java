package io.github.sixcrabs.winterfell.mini.convert.impl;

import io.github.sixcrabs.winterfell.mini.convert.AbstractConverter;

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