package com.samskivert.mustache;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * .
 * </p>
 *
 * @author <a href="mailto:yingxiufeng@mlogcn.com">alex</a>
 * @version v1.0, 2020/4/26
 */
public final class MustacheResolver {

    /**
     * resolve variable name
     * @param compiler
     * @param source
     * @return
     */
    public static List<String> resolveVariableNames(Mustache.Compiler compiler, String source) {
        List<String> varNames = new ArrayList<>(2);
        Mustache.Accumulator accum = new Mustache.Parser(compiler).parse(new StringReader(source));
        for (Template.Segment segment : accum._segs) {
            if (segment instanceof Mustache.VariableSegment) {
                varNames.add(((Mustache.VariableSegment) segment)._name);
            }
        }
        return varNames;
    }
}
