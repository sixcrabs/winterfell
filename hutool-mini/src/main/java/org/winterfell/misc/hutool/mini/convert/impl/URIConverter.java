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
public class URIConverter extends AbstractConverter<URI> {

    @Override
    protected URI convertInternal(Object value) {
        try {
            if(value instanceof File){
                return ((File)value).toURI();
            }

            if(value instanceof URL){
                return ((URL)value).toURI();
            }
            return new URI(convertToStr(value));
        } catch (Exception e) {
            // Ignore Exception
        }
        return null;
    }

}
