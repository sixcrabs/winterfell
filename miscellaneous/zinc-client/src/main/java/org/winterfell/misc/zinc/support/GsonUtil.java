package org.winterfell.misc.zinc.support;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.gson.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.reflect.Type;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

/**
 * <p>
 * Gson Util
 * </p>
 *
 * @author alex
 * @version v1.0, 2019/12/10
 */
public final class GsonUtil {

    private static final Class<?>[] PRIMITIVE_TYPES = {int.class, long.class, short.class,
            float.class, double.class, byte.class, boolean.class, char.class, Integer.class, Long.class,
            Short.class, Float.class, Double.class, Byte.class, Boolean.class, Character.class};

    public static final Class<?>[] NUMBER_TYPES = {int.class, long.class, short.class,
            float.class, double.class, Integer.class, Long.class,
            Short.class, Float.class, Double.class};

    public static final Gson gson = new Gson();

    public static final Logger logger = LoggerFactory.getLogger(GsonUtil.class);


    public static Object getJson(Gson gson, Object source) {
        if (source instanceof String) {
            return source;
        } else {
            return gson.toJson(source);
        }
    }

    public static JsonObject make(@Nonnull String property, @Nonnull Object value) {
        if (value instanceof String) {
            return make(property, String.valueOf(value));
        }
        JsonObject jsonObject = new JsonObject();
        if (!isPrimitiveOrString(value)) {
            jsonObject.add(property, gson.toJsonTree(value));
        } else {
            jsonObject.add(property, new JsonPrimitive(gson.toJson(value)));
        }
        return jsonObject;
    }

    public static JsonObject make(@Nonnull String property, JsonElement jsonElement) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(property, jsonElement);
        return jsonObject;
    }

    public static JsonObject make(String property, String value) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(property, new JsonPrimitive(value));
        return jsonObject;
    }

    public static JsonObject make(String property, Number value) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(property, new JsonPrimitive(value));
        return jsonObject;
    }

    public static JsonObject make(String property, Boolean value) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(property, new JsonPrimitive(value));
        return jsonObject;
    }

    public static JsonObject make(String p1, @Nonnull Object v1, String p2, Object v2) {
        JsonObject jsonObject = new JsonObject();
        addProperty(jsonObject, p1, v1);
        addProperty(jsonObject, p2, v2);
        return jsonObject;
    }

    /**
     * merge two json objects
     * the second will overwrite the first when meeting same properties
     *
     * @param first
     * @param second
     * @return
     */
    public static JsonObject merge(JsonObject first, JsonObject second) {
        Preconditions.checkState(!first.isJsonPrimitive() && !first.isJsonNull(), "cannot be primitive or null");
        Preconditions.checkState(!second.isJsonPrimitive() && !second.isJsonNull(), "cannot be primitive or null");
        JsonObject result = new JsonObject();
        Set<String> keys = Sets.newHashSet(first.keySet());
        keys.addAll(second.keySet());
        keys.forEach(key -> {
            JsonElement a = first.has(key) ? first.get(key) : JsonNull.INSTANCE;
            JsonElement b = second.has(key) ? second.get(key) : JsonNull.INSTANCE;
            if (a.isJsonObject() && b.isJsonObject()) {
                result.add(key, merge(a.getAsJsonObject(), b.getAsJsonObject()));
            }
            if (!a.isJsonNull()) {
                result.add(key, a);
            }
            if (!b.isJsonNull()) {
                result.add(key, b);
            }
        });
        return result;
    }

    /**
     * to json string
     *
     * @param object
     * @return
     */
    public static String toJsonString(Object object) {
        return isProxy(object.getClass()) ? gson.toJson(object, object.getClass().getSuperclass()) : gson.toJson(object);
    }

    /**
     * from json
     *
     * @param json
     * @param classOfT
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        try {
            return gson.fromJson(json, classOfT);
        } catch (JsonSyntaxException e) {
            logger.error(e.getLocalizedMessage());
        }
        return null;
    }

    /**
     * return internal gson object
     *
     * @return
     */
    public static Gson gson() {
        return gson;
    }

    /**
     * form json
     *
     * @param json
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T fromJson(String json, Type type) {
        try {
            return gson.fromJson(json, type);
        } catch (JsonSyntaxException e) {
            logger.error(e.getLocalizedMessage());
        }
        return null;
    }

    private static boolean isProxy(Class<?> clazz) {
        for (Class<?> item : clazz.getInterfaces()) {
            String interfaceName = item.getName();
            if (interfaceName.equals("net.sf.cglib.proxy.Factory") //
                    || interfaceName.equals("org.springframework.cglib.proxy.Factory")) {
                return true;
            }

            if (interfaceName.equals("javassist.util.proxy.ProxyObject") //
                    || interfaceName.equals("org.apache.ibatis.javassist.util.proxy.ProxyObject")
            ) {
                return true;
            }
        }

        return false;
    }

    /**
     * add property
     *
     * @param jsonObject
     * @param property
     * @param value
     */
    private static void addProperty(@Nonnull JsonObject jsonObject, String property, Object value) {
        if (!isPrimitiveOrString(value)) {
            jsonObject.add(property, gson.toJsonTree(value));
        } else {
            Class<?> clazz = value.getClass();
            if (value instanceof String) {
                jsonObject.addProperty(property, String.valueOf(value));
            } else if (value instanceof Boolean || boolean.class.isAssignableFrom(clazz)) {
                jsonObject.addProperty(property, (Boolean) value);
            } else if (value instanceof Number || double.class.isAssignableFrom(clazz) ||
                    int.class.isAssignableFrom(clazz) || float.class.isAssignableFrom(clazz)) {
                jsonObject.addProperty(property, (Number) value);
            }
        }
    }


    private static boolean isPrimitiveOrString(Object val) {
        if (val instanceof String) {
            return true;
        }
        Class<?> classOfPrimitive = val.getClass();
        for (Class<?> standardPrimitive : PRIMITIVE_TYPES) {
            if (standardPrimitive.isAssignableFrom(classOfPrimitive)) {
                return true;
            }
        }
        return false;
    }

    /**
     * is primitive or not
     *
     * @param type
     * @return
     */
    public static boolean isPrimitiveOrString(Class<?> type) {
        if (String.class.equals(type)) {
            return true;
        }
        for (Class<?> standardPrimitive : PRIMITIVE_TYPES) {
            if (standardPrimitive.isAssignableFrom(type)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isString(Object value) {
        return value instanceof String;
    }

    public static boolean isNumber(Object value) {
        return isNumber(value.getClass());
    }

    public static boolean isNumber(Class<?> clazz) {
        for (Class<?> standardPrimitive : NUMBER_TYPES) {
            if (standardPrimitive.isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }

    public static Number toNumber(Object value) {
        boolean b = isPrimitiveOrString(value);
        if (b) {
            Class<?> valueClass = value.getClass();
            if (double.class.isAssignableFrom(valueClass) || Double.class.isAssignableFrom(valueClass)) {
                return Double.parseDouble(value.toString());
            } else if (float.class.isAssignableFrom(valueClass) || Float.class.isAssignableFrom(valueClass)) {
                return Float.parseFloat(value.toString());
            } else if (int.class.isAssignableFrom(valueClass) || Integer.class.isAssignableFrom(valueClass)) {
                return Integer.parseInt(value.toString());
            } else if (long.class.isAssignableFrom(valueClass) || Long.class.isAssignableFrom(valueClass)) {
                return Long.parseLong(value.toString());
            } else if (short.class.isAssignableFrom(valueClass) || Short.class.isAssignableFrom(valueClass)) {
                return Short.parseShort(value.toString());
            }
        }
        return null;
    }


    /**
     * object[] to json array
     *
     * @param array
     * @return
     */
    public static JsonElement toJsonArray(Object[] array, boolean returnNullIsEmpty) {
        if (array.length == 0) {
            return returnNullIsEmpty ? JsonNull.INSTANCE: new JsonArray();
        }
        JsonArray jsonArray = new JsonArray(array.length);
        Arrays.stream(array).forEach(obj -> jsonArray.add(toJsonElement(obj)));
        return jsonArray;
    }

    public static JsonElement toJsonArray(Collection collection, boolean returnNullIsEmpty) {
        if (collection.size() == 0) {
            return returnNullIsEmpty ? JsonNull.INSTANCE : new JsonArray();
        }
        return toJsonArray(collection.toArray(new Object[]{}), returnNullIsEmpty);
    }

    /**
     * obj to json element
     *
     * @param obj
     * @return
     */
    public static JsonElement toJsonElement(Object obj) {
        if (isPrimitiveOrString(obj)) {
            Class<?> clazz = obj.getClass();
            if (obj instanceof String) {
                return new JsonPrimitive(String.valueOf(obj));
            } else if (obj instanceof Boolean || boolean.class.isAssignableFrom(clazz)) {
                return new JsonPrimitive((Boolean) obj);
            } else if (obj instanceof Number || double.class.isAssignableFrom(clazz) ||
                    int.class.isAssignableFrom(clazz) || float.class.isAssignableFrom(clazz)) {
                return new JsonPrimitive((Number) obj);
            }
        } else {
            // json 对象
            return gson.toJsonTree(obj);
///                return gson.fromJson(gson.toJson(obj), JsonObject.class);
        }
        return JsonNull.INSTANCE;
    }

    public static boolean isDateLike(Class<?> clazz) {
        return Date.class.isAssignableFrom(clazz) || Temporal.class.isAssignableFrom(clazz);
    }
}
