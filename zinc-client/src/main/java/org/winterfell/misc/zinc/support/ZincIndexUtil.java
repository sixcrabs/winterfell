package org.winterfell.misc.zinc.support;

import org.winterfell.misc.zinc.action.api.IndexCreate;
import org.winterfell.misc.zinc.annotation.ZincField;
import org.winterfell.misc.zinc.annotation.ZincId;
import org.winterfell.misc.zinc.annotation.ZincIgnore;
import org.winterfell.misc.zinc.annotation.ZincIndex;
import org.winterfell.misc.zinc.exception.ZincException;
import org.winterfell.misc.zinc.model.DocumentDateTimeField;
import org.winterfell.misc.zinc.model.DocumentField;
import com.google.common.collect.Lists;

import javax.annotation.Nonnull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

import static org.winterfell.misc.zinc.support.ZincConstants.DATETIME_DEFAULT_FORMAT;
import static org.winterfell.misc.zinc.support.ZincConstants.DATETIME_DEFAULT_TZ;
import static org.winterfell.misc.zinc.support.ZincUtil.*;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/4/21
 */
public final class ZincIndexUtil {

    private ZincIndexUtil() {
    }

    /**
     * to schema
     *
     * @param idxName
     * @param indexClazz
     * @return
     * @throws ZincException
     */
    public static IndexCreate toIndexCreateSchema(String idxName, @Nonnull Class<?> indexClazz) throws ZincException {
        IndexCreate.Builder builder = toIndexCreateBuilder(indexClazz);
        return builder.setIndexName(idxName).build();
    }

    /**
     * 转换 @ZincIndex 注解的类为索引schema
     *
     * @param indexClazz
     * @return
     */
    public static IndexCreate toIndexCreateSchema(@Nonnull Class<?> indexClazz) throws ZincException {
        IndexCreate.Builder builder = toIndexCreateBuilder(indexClazz);
        return builder.build();
    }

    private static IndexCreate.Builder toIndexCreateBuilder(@Nonnull Class<?> indexClazz) throws ZincException {
        boolean annotationPresent = indexClazz.isAnnotationPresent(ZincIndex.class);
        if (!annotationPresent) {
            throw new ZincException("必须使用 @ZincIndex 注解类");
        }
        ZincIndex annotation = indexClazz.getAnnotation(ZincIndex.class);
        String idxName = isNotBlank(annotation.name()) ? annotation.name() :
                annotation.prefix().concat(toUnderlineCase(indexClazz.getSimpleName()));
        int shardNum = annotation.numberOfShards();
        IndexCreate.Builder builder = new IndexCreate.Builder().setIndexName(idxName);
        builder.setShardNum(Math.min(shardNum, Runtime.getRuntime().availableProcessors()));
        // add fields
        List<Field> fields = Lists.newArrayList(indexClazz.getDeclaredFields());
        // 先过滤 ignore/join 字段以及静态字段
        fields.stream()
                .filter(f -> !f.isAnnotationPresent(ZincIgnore.class) &&
                        !f.isAnnotationPresent(ZincId.class) &&
                        !Modifier.isStatic(f.getModifiers()) &&
                        !"serialVersionUID".equalsIgnoreCase(f.getName()))
                .forEach(classField -> {
                    Class<?> fieldType = classField.getType();
                    String fieldName = classField.getName();
                    ZincField fieldAnnotation = classField.isAnnotationPresent(ZincField.class) ? classField.getAnnotation(ZincField.class) : null;
                    ZincFieldTypes type = getZincFieldType(fieldType, fieldAnnotation);
                    DocumentField docField = type.toField();
                    String name = (fieldAnnotation != null && isNotBlank(fieldAnnotation.name())) ? fieldAnnotation.name() : fieldName;
                    docField.setName(name)
                            .setIndex(Objects.isNull(fieldAnnotation) || fieldAnnotation.index())
                            .setSortable(Objects.isNull(fieldAnnotation) || fieldAnnotation.sortable())
                            .setAggregatable(Objects.isNull(fieldAnnotation) || fieldAnnotation.aggregatable())
                            .setHighlightable(Objects.isNull(fieldAnnotation) || fieldAnnotation.highlightable());

                    if (docField.isDateField()) {
                        // 设置时间字段的时间格式
                        DocumentDateTimeField timeField = (DocumentDateTimeField) docField;
                        timeField.setFormat(Objects.isNull(fieldAnnotation) ? DATETIME_DEFAULT_FORMAT : fieldAnnotation.dateFormat())
                                .setTimeZone(Objects.isNull(fieldAnnotation) ? DATETIME_DEFAULT_TZ : fieldAnnotation.dateTimeZone());
                    }

                    if (docField.isDateOrNumeric()) {
                        docField.setAggregatable(true).setSortable(true).setHighlightable(false);
                    }
                    if (docField.isHighlightable()) {
                        docField.setStore(true);
                    }
                    builder.addField(docField);
                });

        return builder;
    }

    private static ZincFieldTypes getZincFieldType(Class<?> fieldType, ZincField fieldAnnotation) {
        if (fieldAnnotation != null) {
            return fieldAnnotation.type();
        }
        if (fieldType.equals(LocalTime.class) || fieldType.equals(LocalDateTime.class) || fieldType.equals(LocalDate.class)) {
            return ZincFieldTypes.date;
        } else if (fieldType.equals(Float.class) || fieldType.equals(float.class) ||
                fieldType.equals(Integer.class) || fieldType.equals(int.class) ||
                fieldType.equals(Double.class) || fieldType.equals(double.class) ||
                fieldType.equals(Long.class) || fieldType.equals(long.class)
        ) {
            return ZincFieldTypes.numeric;
        } else if (fieldType.equals(boolean.class) || fieldType.equals(Boolean.class)) {
            return ZincFieldTypes.bool;
        } else {
            return ZincFieldTypes.text;
        }
    }

    public static boolean isEnum(Class<?> clazz) {
        return null != clazz && clazz.isEnum();
    }

    public static String getIdxName(Class<?> clazz) {
        ZincIndex zincIndex = clazz.isAnnotationPresent(ZincIndex.class) ? clazz.getAnnotation(ZincIndex.class) : null;
        String name = zincIndex != null ? zincIndex.name() : "";
        return isBlank(name) ? (zincIndex == null ? "" : zincIndex.prefix()).concat(toUnderlineCase(clazz.getSimpleName())) : name;
    }
}
