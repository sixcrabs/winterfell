package org.winterfell.misc.hutool.mini.convert.impl;

import org.winterfell.misc.hutool.mini.convert.AbstractConverter;

import java.io.File;
import java.net.URI;
import java.net.URL;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/3/5
 */
public class URLConverter extends AbstractConverter<URL> {

    @Override
    protected URL convertInternal(Object value) {
        try {
            if(value instanceof File){
                return ((File)value).toURI().toURL();
            }

            if(value instanceof URI){
                return ((URI)value).toURL();
            }
            return new URL(convertToStr(value));
        } catch (Exception e) {
            // Ignore Exception
        }
        return null;
    }

}
