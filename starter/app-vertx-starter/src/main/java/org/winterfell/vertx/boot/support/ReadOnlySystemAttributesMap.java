package org.winterfell.vertx.boot.support;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0
 */
public abstract class ReadOnlySystemAttributesMap implements Map<String, String> {

  @Override
  public boolean containsKey(Object key) {
    return (get(key) != null);
  }

  /**
   * Returns the value to which the specified key is mapped, or {@code null} if this map
   * contains no mapping for the key.
   * @param key the name of the system attribute to retrieve
   * @throws IllegalArgumentException if given key is non-String
   */
  @Override
  @Nullable
  public String get(Object key) {
    if (!(key instanceof String)) {
      throw new IllegalArgumentException(
        "Type of key [" + key.getClass().getName() + "] must be java.lang.String");
    }
    return getSystemAttribute((String) key);
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  /**
   * Template method that returns the underlying system attribute.
   * <p>Implementations typically call {@link System#getProperty(String)} or {@link System#getenv(String)} here.
   */
  @Nullable
  protected abstract String getSystemAttribute(String attributeName);


  // Unsupported

  @Override
  public int size() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String put(String key, String value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean containsValue(Object value) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String remove(Object key) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Set<String> keySet() {
    return Collections.emptySet();
  }

  @Override
  public void putAll(Map<? extends String, ? extends String> map) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Collection<String> values() {
    return Collections.emptySet();
  }

  @Override
  public Set<Entry<String, String>> entrySet() {
    return Collections.emptySet();
  }
}
