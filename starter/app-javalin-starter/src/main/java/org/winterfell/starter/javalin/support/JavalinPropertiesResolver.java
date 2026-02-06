package org.winterfell.starter.javalin.support;

//import cn.hutool.core.bean.BeanUtil;
import org.winterfell.misc.hutool.mini.BeanUtil;
import org.winterfell.misc.hutool.mini.ClassUtil;
import org.winterfell.misc.hutool.mini.ReflectUtil;
import org.winterfell.misc.hutool.mini.StringUtil;
import org.winterfell.misc.hutool.mini.io.Resource;
import org.winterfell.starter.javalin.JavalinAppConfig;
import org.winterfell.starter.javalin.annotation.JavalinProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * <p>
 * 配置解析器
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/11/26
 */
public enum JavalinPropertiesResolver {

    // 单例
    INSTANCE;

    private static final Logger LOG = LoggerFactory.getLogger(JavalinPropertiesResolver.class);

    private JavalinAppConfig appConfig;

    private final Map<String, Object> beanProperties = new HashMap<>(1);

    private final Map<String, Object> flattenedMap = new HashMap<>(1);

    public JavalinAppConfig getAppConfig() {
        return this.appConfig;
    }

    public Map<String, Object> getBeanProperties() {
        return this.beanProperties;
    }

    public Map<String, Object> getFlattenedMap() {
        return this.flattenedMap;
    }

    public void resolveProperties(String[] scanPackages) {
        LOG.info("-------config init-----");
        Resource resource = ResourceLoaderUtil.loadResource("application.yml");
        Map<String, Class<?>> repo = new HashMap<>(1);
        if (scanPackages != null) {
            for (String pkg : scanPackages) {
                for (Class<?> clazz : ClassUtil.scanPackageByAnnotation(pkg, JavalinProperties.class)) {
                    repo.put(clazz.getAnnotation(JavalinProperties.class).prefix().toLowerCase(), clazz);
                }
            }
        }
        if (resource != null) {
            Yaml yaml = new Yaml();
            try (InputStream stream = resource.getStream()) {
                for (Object data : yaml.loadAll(stream)) {
                    if (data != null) {
                        LOG.info("loading app config...");
                        Map<String, Object> objectMap = asMap(data);
                        // 加载默认配置
                        appConfig = toBean(objectMap.get(JavalinAppConfig.PREFIX), JavalinAppConfig.class);
                        objectMap.remove(JavalinAppConfig.PREFIX);
                        repo.remove(JavalinAppConfig.PREFIX);
                        objectMap.forEach((key, value) -> {
                            if (repo.containsKey(key.toLowerCase())) {
                                Object bean = toBean(objectMap.get(key), repo.get(key.toLowerCase()));
                                beanProperties.put(repo.get(key.toLowerCase()).getName(), bean);
                            }
                        });
                        flattenedMap.putAll(getFlattenedMap(objectMap));
//                        Properties properties = createStringAdaptingProperties();
//                        properties.putAll(flattenedMap);
//                        System.out.println(properties);
                    }
                }

            } catch (Exception ex) {
                LOG.error(ex.getLocalizedMessage());
            }
        }

    }


    private <T> T toBean(Object source, Class<T> clazz) {
        try {
            T target = ReflectUtil.newInstanceIfPossible(clazz);
            BeanUtil.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Properties createStringAdaptingProperties() {
        return new SortedProperties(false) {
            @Override
            @Nullable
            public String getProperty(String key) {
                Object value = this.get(key);
                return value != null ? value.toString() : null;
            }
        };
    }

    protected final Map<String, Object> getFlattenedMap(Map<String, Object> source) {
        Map<String, Object> result = new LinkedHashMap<>();
        buildFlattenedMap(result, source, null);
        return result;
    }

    private void buildFlattenedMap(Map<String, Object> result, Map<String, Object> source, @Nullable String path) {
        source.forEach((key, value) -> {
            if (StringUtil.isNotBlank(path)) {
                if (key.startsWith("[")) {
                    key = path + key;
                } else {
                    key = path + '.' + key;
                }
            }
            if (value instanceof String) {
                result.put(key, value);
            } else if (value instanceof Map) {
                // Need a compound key
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) value;
                buildFlattenedMap(result, map, key);
            } else if (value instanceof Collection) {
                // Need a compound key
                @SuppressWarnings("unchecked")
                Collection<Object> collection = (Collection<Object>) value;
                if (collection.isEmpty()) {
                    result.put(key, "");
                } else {
                    int count = 0;
                    for (Object object : collection) {
                        buildFlattenedMap(result, Collections.singletonMap(
                                "[" + (count++) + "]", object), key);
                    }
                }
            } else {
                result.put(key, (value != null ? value : ""));
            }
        });
    }


    private Map<String, Object> asMap(Object object) {
        // YAML can have numbers as keys
        Map<String, Object> result = new LinkedHashMap<>();
        if (!(object instanceof Map)) {
            // A document can be a text literal
            result.put("document", object);
            return result;
        }

        Map<Object, Object> map = (Map<Object, Object>) object;
        map.forEach((key, value) -> {
            if (value instanceof Map) {
                value = asMap(value);
            }
            if (key instanceof CharSequence) {
                result.put(key.toString(), value);
            } else {
                // It has to be a map key in this case
                result.put("[" + key.toString() + "]", value);
            }
        });
        return result;
    }

    protected static class SortedProperties extends Properties {

        static final String EOL = System.lineSeparator();

        private static final Comparator<Object> keyComparator = Comparator.comparing(String::valueOf);

        private static final Comparator<Map.Entry<Object, Object>> entryComparator = Map.Entry.comparingByKey(keyComparator);


        private final boolean omitComments;


        /**
         * Construct a new {@code SortedProperties} instance that honors the supplied
         * {@code omitComments} flag.
         *
         * @param omitComments {@code true} if comments should be omitted when
         *                     storing properties in a file
         */
        SortedProperties(boolean omitComments) {
            this.omitComments = omitComments;
        }

        /**
         * Construct a new {@code SortedProperties} instance with properties populated
         * from the supplied {@link Properties} object and honoring the supplied
         * {@code omitComments} flag.
         * <p>Default properties from the supplied {@code Properties} object will
         * not be copied.
         *
         * @param properties   the {@code Properties} object from which to copy the
         *                     initial properties
         * @param omitComments {@code true} if comments should be omitted when
         *                     storing properties in a file
         */
        SortedProperties(Properties properties, boolean omitComments) {
            this(omitComments);
            putAll(properties);
        }


        @Override
        public void store(OutputStream out, @Nullable String comments) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            super.store(baos, (this.omitComments ? null : comments));
            String contents = baos.toString(StandardCharsets.ISO_8859_1.name());
            for (String line : contents.split(EOL)) {
                if (!(this.omitComments && line.startsWith("#"))) {
                    out.write((line + EOL).getBytes(StandardCharsets.ISO_8859_1));
                }
            }
        }

        @Override
        public void store(Writer writer, @Nullable String comments) throws IOException {
            StringWriter stringWriter = new StringWriter();
            super.store(stringWriter, (this.omitComments ? null : comments));
            String contents = stringWriter.toString();
            for (String line : contents.split(EOL)) {
                if (!(this.omitComments && line.startsWith("#"))) {
                    writer.write(line + EOL);
                }
            }
        }

        @Override
        public void storeToXML(OutputStream out, @Nullable String comments) throws IOException {
            super.storeToXML(out, (this.omitComments ? null : comments));
        }

        @Override
        public void storeToXML(OutputStream out, @Nullable String comments, String encoding) throws IOException {
            super.storeToXML(out, (this.omitComments ? null : comments), encoding);
        }

        /**
         * Return a sorted enumeration of the keys in this {@link Properties} object.
         *
         * @see #keySet()
         */
        @Override
        public synchronized Enumeration<Object> keys() {
            return Collections.enumeration(keySet());
        }

        /**
         * Return a sorted set of the keys in this {@link Properties} object.
         * <p>The keys will be converted to strings if necessary using
         * {@link String#valueOf(Object)} and sorted alphanumerically according to
         * the natural order of strings.
         */
        @Override
        public Set<Object> keySet() {
            Set<Object> sortedKeys = new TreeSet<>(keyComparator);
            sortedKeys.addAll(super.keySet());
            return Collections.synchronizedSet(sortedKeys);
        }

        /**
         * Return a sorted set of the entries in this {@link Properties} object.
         * <p>The entries will be sorted based on their keys, and the keys will be
         * converted to strings if necessary using {@link String#valueOf(Object)}
         * and compared alphanumerically according to the natural order of strings.
         */
        @Override
        public Set<Map.Entry<Object, Object>> entrySet() {
            Set<Map.Entry<Object, Object>> sortedEntries = new TreeSet<>(entryComparator);
            sortedEntries.addAll(super.entrySet());
            return Collections.synchronizedSet(sortedEntries);
        }

    }
}
