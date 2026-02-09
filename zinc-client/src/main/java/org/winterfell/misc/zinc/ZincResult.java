package org.winterfell.misc.zinc;

import org.winterfell.misc.zinc.annotation.ZincId;
import org.winterfell.misc.zinc.support.CloneUtil;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/30
 */
public class ZincResult {

    protected static final Logger log = LoggerFactory.getLogger(ZincResult.class);

    protected JsonObject jsonObject;
    protected String jsonString;
    protected String pathToResult;
    protected int responseCode;
    protected boolean isSucceeded;
    protected String errorMessage;
    protected Gson gson;

    protected static class MetaField {
        public final String internalFieldName;
        public final String esFieldName;
        public final Class<? extends Annotation>[] annotationClasses;

        MetaField(String internalFieldName, String esFieldName, Class<? extends Annotation>... annotationClasses) {
            this.internalFieldName = internalFieldName;
            this.esFieldName = esFieldName;
            this.annotationClasses = annotationClasses;
        }
    }

    protected static final ImmutableList<MetaField> META_FIELDS = ImmutableList.of(
            new MetaField("id", "_id", ZincId.class)
    );

    public ZincResult(Gson gson) {
        this.gson = gson;
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    public String getStrValue(String key) {
        return jsonObject.get(key).getAsString();
    }

    public ZincResult setJsonObject(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
        if (jsonObject.get("error") != null) {
            errorMessage = jsonObject.get("error").toString();
        }
        return this;
    }

    public String getJsonString() {
        return jsonString;
    }

    public ZincResult setJsonString(String jsonString) {
        this.jsonString = jsonString;
        return this;
    }

    public String getPathToResult() {
        return pathToResult;
    }

    public ZincResult setPathToResult(String pathToResult) {
        this.pathToResult = pathToResult;
        return this;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public ZincResult setResponseCode(int responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    public boolean isSucceeded() {
        return isSucceeded;
    }

    public ZincResult setSucceeded(boolean succeeded) {
        isSucceeded = succeeded;
        return this;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public ZincResult setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public Gson getGson() {
        return gson;
    }

    public ZincResult setGson(Gson gson) {
        this.gson = gson;
        return this;
    }

    public <T> T getSourceAsObject(Class<T> clazz) {
        T sourceAsObject = null;
        List<T> sources = getSourceAsObjectList(clazz);
        if (sources.size() > 0) {
            sourceAsObject = sources.get(0);
        }
        return sourceAsObject;
    }

    public <T> List<T> getSourceAsObjectList(Class<T> type) {
        List<T> objectList = new ArrayList<T>();
        if (isSucceeded) {
            for (JsonElement source : extractSource()) {
                T obj = createSourceObject(source, type);
                if (obj != null) {
                    objectList.add(obj);
                }
            }
        }

        return objectList;
    }

    protected List<JsonElement> extractSource() {
        List<JsonElement> sourceList = new ArrayList<JsonElement>();

        if (jsonObject != null) {
            String[] keys = getKeys();
            if (keys == null) {
                sourceList.add(jsonObject);
            } else {
                String sourceKey = keys[keys.length - 1];
                JsonElement obj = jsonObject.get(keys[0]);
                if (keys.length > 1) {
                    for (int i = 1; i < keys.length - 1; i++) {
                        obj = ((JsonObject) obj).get(keys[i]);
                    }

                    if (obj.isJsonObject()) {
                        JsonElement source = obj.getAsJsonObject().get(sourceKey);
                        if (source != null) {
                            sourceList.add(source);
                        }
                    } else if (obj.isJsonArray()) {
                        for (JsonElement element : obj.getAsJsonArray()) {
                            if (element instanceof JsonObject) {
                                JsonObject currentObj = element.getAsJsonObject();
                                JsonObject source = currentObj.getAsJsonObject(sourceKey);
                                if (source != null) {

                                    JsonObject copy = (JsonObject) CloneUtil.deepClone(source);
                                    sourceList.add(copy);
                                }
                            }
                        }
                    }
                } else if (obj != null) {
                    JsonElement copy = CloneUtil.deepClone(obj);
                    sourceList.add(copy);
                }
            }
        }

        return sourceList;
    }

    protected <T> T createSourceObject(JsonElement source, Class<T> type) {
        T obj = null;
        try {
            String json = source.toString();
            obj = gson.fromJson(json, type);

            // Check if JestId is visible
            Class clazz = type;
            int knownMetadataFieldsCount = META_FIELDS.size();
            int foundFieldsCount = 0;
            boolean allFieldsFound = false;
            while (clazz != null && !allFieldsFound) {
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    if (foundFieldsCount == knownMetadataFieldsCount) {
                        allFieldsFound = true;
                        break;
                    }
                    for (MetaField metaField : META_FIELDS) {
                        if (isAnnotationPresent(field, metaField) && setAnnotatedField(obj, source, field, metaField.internalFieldName)) {
                            foundFieldsCount++;
                        }
                    }
                }
                clazz = clazz.getSuperclass();
            }

        } catch (Exception e) {
            log.error("Unhandled exception occurred while converting source to the object ." + type.getCanonicalName(), e);
        }
        return obj;
    }

    private boolean isAnnotationPresent(Field field, MetaField metaField) {
        return Arrays.stream(metaField.annotationClasses).anyMatch(field::isAnnotationPresent);
    }

    private <T> boolean setAnnotatedField(T obj, JsonElement source, Field field, String fieldName) {
        try {
            field.setAccessible(true);
            Object value = field.get(obj);
            if (value == null) {
                Class<?> fieldType = field.getType();
                JsonElement element = ((JsonObject) source).get(fieldName);
                field.set(obj, getAs(element, fieldType));
                return true;
            }
        } catch (IllegalAccessException e) {
            log.error("Unhandled exception occurred while setting annotated field from source");
        }
        return false;
    }

    /**
     * 结果对象在 json 中的路径key
     *
     * @return
     */
    protected String[] getKeys() {
        return getPathToResult() == null ? null : getPathToResult().split("/");
    }

    protected String getAsString(String jsonKey) {
        try {
            return getAs(jsonObject.get(jsonKey), String.class);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected Long getAsLong(String jsonKey) {
        try {
            return getAs(jsonObject.get(jsonKey), Long.class);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected Boolean getAsBool(String jsonKey) {
        try {
            return getAs(jsonObject.get(jsonKey), Boolean.class);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    protected  <T> T getAs(JsonElement id, Class<T> fieldType) throws IllegalAccessException {
        if (id == null) {
            return null;
        }
        if (id.isJsonNull()) {
            return null;
        }
        if (fieldType.isAssignableFrom(String.class)) {
            return (T) id.getAsString();
        }
        if (fieldType.isAssignableFrom(Number.class)) {
            return (T) id.getAsNumber();
        }
        if (fieldType.isAssignableFrom(BigDecimal.class)) {
            return (T) id.getAsBigDecimal();
        }
        if (fieldType.isAssignableFrom(Double.class)) {
            Object o = id.getAsDouble();
            return (T) o;
        }
        if (fieldType.isAssignableFrom(Float.class)) {
            Object o = id.getAsFloat();
            return (T) o;
        }
        if (fieldType.isAssignableFrom(BigInteger.class)) {
            return (T) id.getAsBigInteger();
        }
        if (fieldType.isAssignableFrom(Long.class)) {
            Object o = id.getAsLong();
            return (T) o;
        }
        if (fieldType.isAssignableFrom(Integer.class)) {
            Object o = id.getAsInt();
            return (T) o;
        }
        if (fieldType.isAssignableFrom(Short.class)) {
            Object o = id.getAsShort();
            return (T) o;
        }
        if (fieldType.isAssignableFrom(Character.class)) {
            return (T) (Character) id.getAsCharacter();
        }
        if (fieldType.isAssignableFrom(Byte.class)) {
            return (T) (Byte) id.getAsByte();
        }
        if (fieldType.isAssignableFrom(Boolean.class)) {
            return (T) (Boolean) id.getAsBoolean();
        }

        throw new RuntimeException("cannot assign " + id + " to " + fieldType);
    }
}
