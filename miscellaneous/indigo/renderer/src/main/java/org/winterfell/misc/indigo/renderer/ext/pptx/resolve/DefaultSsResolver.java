package org.winterfell.misc.indigo.renderer.ext.pptx.resolve;

import org.apache.poi.xslf.usermodel.*;
import org.apache.xmlbeans.XmlObject;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.openxmlformats.schemas.drawingml.x2006.main.CTNonVisualDrawingProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.winterfell.misc.indigo.renderer.ext.pptx.support.Constants;
import org.winterfell.misc.indigo.renderer.ext.pptx.support.RegexUtils;
import org.winterfell.misc.indigo.renderer.ext.pptx.support.TemplateGrammarSymbol;
import org.winterfell.misc.indigo.renderer.ext.pptx.template.SsMetaTemplate;
import org.winterfell.misc.indigo.renderer.ext.pptx.template.impl.SsPictureTemplate;
import org.winterfell.misc.indigo.renderer.ext.pptx.template.impl.SsRunTemplate;
import org.winterfell.misc.indigo.renderer.ext.pptx.template.impl.SsTableTemplate;

import javax.xml.namespace.QName;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.winterfell.misc.indigo.renderer.ext.pptx.support.Constants.*;

/**
 * @author alex
 * @version v1.0 2020/11/17
 */
public class DefaultSsResolver implements SsResolver {


    private static final Logger logger = LoggerFactory.getLogger(DefaultSsResolver.class);

    private Pattern templatePattern;

    private Pattern grammarPattern;

    private static final String FORMAT_TEMPLATE = "{0}{1}{2}{3}";

    private static final String FORMAT_GRAMMAR = "({0})|({1})";


    public DefaultSsResolver() {
        String prefixRegex = RegexUtils.escapeExprSpecialWord(GRAMMAR_PREFIX);
        String suffixRegex = RegexUtils.escapeExprSpecialWord(GRAMMAR_SUFFIX);

        templatePattern = Pattern
                .compile(MessageFormat.format(FORMAT_TEMPLATE,
                        prefixRegex, Constants.SIGN_REGEX,
                        Constants.DEFAULT_GRAMMAR_REGEX, suffixRegex));
        grammarPattern = Pattern.compile(MessageFormat.format(FORMAT_GRAMMAR, prefixRegex, suffixRegex));

    }

    /**
     * 解析模板变量等
     *
     * @param slideShow slideshow
     * @return
     */
    @Override
    public List<SsMetaTemplate> resolve(XMLSlideShow slideShow) {
        List<SsMetaTemplate> metaTemplates = new ArrayList<>();
        if (null == slideShow) {
            return metaTemplates;
        }
        logger.info("Resolve the document start...");
        List<XSLFShape> shapes = slideShow.getSlides().stream().map(XSLFSheet::getShapes)
                .flatMap((Function<List<XSLFShape>, Stream<XSLFShape>>) Collection::stream).collect(Collectors.toList());
        if (!shapes.isEmpty()) {
            // 解析
            metaTemplates.addAll(resolveShapes(shapes));
        } else {
            logger.error("contains none shapes");
        }
        return metaTemplates;
    }

    public Pattern getTemplatePattern() {
        return templatePattern;
    }

    public Pattern getGrammarPattern() {
        return grammarPattern;
    }


    /**
     * 解析 XSLFShape
     *
     * @param shapes XSLFShape
     * @return
     */
    public List<SsMetaTemplate> resolveShapes(List<XSLFShape> shapes) {
        List<SsMetaTemplate> metaTemplates = new ArrayList<>();
        shapes.forEach(xslfShape -> {
            if (xslfShape instanceof XSLFTextBox) {
                metaTemplates.addAll(resolveTextBox((XSLFTextBox) xslfShape));
            } else if (xslfShape instanceof XSLFAutoShape) {
                metaTemplates.addAll(resolveAutoShape((XSLFAutoShape) xslfShape));
            } else if (xslfShape instanceof XSLFPictureShape) {
                XSLFPictureShape pictureShape = (XSLFPictureShape) xslfShape;
                String title = getTitle(xslfShape);
                if (title != null && templatePattern.matcher(title).matches()) {
                    metaTemplates.add(createPictureTemplate(parseTag(title), pictureShape.getPictureData()));
                }
            } else if (xslfShape instanceof XSLFTable) {
                String tt = getTitle(xslfShape);
                if (tt != null && templatePattern.matcher(tt).matches()) {
                    XSLFTable xslfTable = (XSLFTable) xslfShape;
                    metaTemplates.add(createTableTemplate(parseTag(tt), xslfTable));
                }
            } else if (xslfShape instanceof XSLFGroupShape) {
                XSLFGroupShape groupShape = (XSLFGroupShape) xslfShape;
                metaTemplates.addAll(resolveShapes(groupShape.getShapes()));
            }
        });
        return metaTemplates;
    }

    /**
     * 获取可选文字的标题
     *
     * @param shape 图片或表格
     * @return
     */
    private String getTitle(XSLFShape shape) {
        String xquery = "declare namespace p='http://schemas.openxmlformats.org/presentationml/2006/main' .//*/p:cNvPr";
        XmlObject[] rs = shape.getXmlObject().selectPath(xquery);
        if (rs.length == 0) {
            return null;
        }
        CTNonVisualDrawingProps props = (CTNonVisualDrawingProps.class.isInstance(rs[0])) ? (CTNonVisualDrawingProps) rs[0] : null;
        if (Objects.isNull(props)) {
            return null;
        }
        try {
            return props.selectAttribute(new QName("title")).getDomNode().getNodeValue();
        } catch (Exception e) {
            logger.warn("get `title` of `XSLFShape` error: {}", e.getLocalizedMessage());
        }
        return null;
    }

    /**
     * 解析ppt中自动生成的 区域 XSLFAutoShape
     * 忽略占位符
     *
     * @param autoShape
     * @return
     */
    public List<SsMetaTemplate> resolveAutoShape(XSLFAutoShape autoShape) {
        if (!autoShape.isPlaceholder()) {
            List<XSLFTextParagraph> textParagraphs = autoShape.getTextParagraphs();
            return resolveTextParagraph(textParagraphs);
        }
        return new ArrayList<>(1);
    }

    /**
     * 解析 textbox (文本框)
     *
     * @param textBox 文本框 XSLFTextBox
     * @return
     */
    public List<SsMetaTemplate> resolveTextBox(XSLFTextBox textBox) {
        List<XSLFTextParagraph> textParagraphs = textBox.getTextParagraphs();
        return resolveTextParagraph(textParagraphs);
    }

    /**
     * 解析段落 XSLFTextParagraph
     *
     * @param paragraphs 段落
     * @return
     */
    private List<SsMetaTemplate> resolveTextParagraph(List<XSLFTextParagraph> paragraphs) {
        List<SsMetaTemplate> tpls = new ArrayList<>(1);
        paragraphs.forEach(paragraph -> {
            List<XSLFTextRun> textRuns = paragraph.getTextRuns();
            textRuns.forEach(textRun -> {
                String rawText = textRun.getRawText();
                Matcher matcher = templatePattern.matcher(rawText);
                while (matcher.find()) {
///                    int startPos = matcher.start();
///                    int endPos = matcher.end();
                    String tag = parseTag(matcher.group());
                    tpls.add(createRunTemplate(tag, textRun));
                }
            });
        });
        return tpls;
    }

    /**
     * parse `{{#var}}` to `#var`
     *
     * @param source
     * @return
     */
    private String parseTag(String source) {
        return grammarPattern.matcher(source).replaceAll("").trim();
    }

    /**
     * 创建基本的要素模板
     *
     * @param tag     标记 eg `@pic`
     * @param textRun 对应的 run
     * @return
     */
    private SsRunTemplate createRunTemplate(String tag, XSLFTextRun textRun) {
        SsRunTemplate ssRunTemplate = new SsRunTemplate().setTextRun(textRun);
        Character symbol = EMPTY_CHAR;
        if (!"".equals(tag)) {
            char firstChar = tag.charAt(0);
            for (TemplateGrammarSymbol value : TemplateGrammarSymbol.values()) {
                if (value.getSymbol() == firstChar) {
                    symbol = value.getSymbol();
                    break;
                }
            }
        }
        ssRunTemplate
                .setShape(textRun.getParagraph().getParentShape())
                .setSource(GRAMMAR_PREFIX + tag + GRAMMAR_SUFFIX)
                .setSign(symbol)
                .setTagName(symbol.equals(EMPTY_CHAR) ? tag : tag.substring(1));
        return ssRunTemplate;
    }


    /**
     * 创建图片要素模板
     *
     * @return
     */
    private SsPictureTemplate createPictureTemplate(String tag, @NonNull XSLFPictureData pictureData) {
        SsPictureTemplate pictureTemplate = new SsPictureTemplate().setPictureData(pictureData);
        pictureTemplate.setTagName(tag).setSign(EMPTY_CHAR).setSource(GRAMMAR_PREFIX + tag + GRAMMAR_SUFFIX);
        return pictureTemplate;
    }

    /**
     * 创建表格要素模板
     *
     * @param tag
     * @param xslfTable
     * @return
     */
    private SsTableTemplate createTableTemplate(String tag, XSLFTable xslfTable) {
        SsTableTemplate ssTableTemplate = new SsTableTemplate();
        ssTableTemplate.setTable(xslfTable);
        ssTableTemplate.setTagName(tag).setSign(EMPTY_CHAR).setSource(GRAMMAR_PREFIX + tag + GRAMMAR_SUFFIX);
        return ssTableTemplate;
    }


}