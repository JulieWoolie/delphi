package com.juliewoolie.delphi.resource;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class PathImpl implements ResourcePath {

  static final char BACKSLASH = '\\';
  static final char PATH_DELIMITER = '/';
  static final char MODULE_SEPARATOR = ':';
  static final char QUERY_SEPARATOR = '?';
  static final char QUERY_DELIMITER = '&';
  static final char KEY_VALUE_SEPARATOR = '=';

  private String moduleName;
  private final List<String> elements = new ArrayList<>();
  private final Map<String, String> queries = new HashMap<>();

  public PathImpl(String moduleName) {
    setModule(moduleName);
  }

  public PathImpl(PathImpl source) {
    this.moduleName = source.moduleName;
    this.elements.addAll(source.elements);
    this.queries.putAll(source.queries);
  }

  public static PathImpl parse(String value, String fallbackModule) {
    Objects.requireNonNull(value, "Null parse string");

    String path = value.replace(BACKSLASH, PATH_DELIMITER);

    int modIndex = path.indexOf(MODULE_SEPARATOR);

    String moduleName;
    String afterModule;

    if (modIndex == -1) {
      if (Strings.isNullOrEmpty(fallbackModule)) {
        int qIdx = path.indexOf(QUERY_SEPARATOR);
        String query;

        if (qIdx != -1) {
          query = path.substring(qIdx + 1);
          moduleName = path.substring(0, qIdx);
        } else {
          moduleName = path;
          query = "";
        }

        ResourcePath.validateModuleName(moduleName);

        PathImpl result = new PathImpl(moduleName);
        parseQueries(result, query);

        return result;
      }

      moduleName = fallbackModule;
      afterModule = path;
    } else {
      moduleName = path.substring(0, modIndex);
      afterModule = path.substring(modIndex + 1);
    }

    ResourcePath.validateModuleName(moduleName);

    String fpath;
    String query;

    int queryStart = afterModule.indexOf(QUERY_SEPARATOR);

    if (queryStart == -1) {
      fpath = afterModule;
      query = null;
    } else {
      fpath = afterModule.substring(0, queryStart);
      query = afterModule.substring(queryStart + 1);
    }

    PathImpl result = new PathImpl(moduleName);
    String[] split = fpath.split(PATH_DELIMITER + "");

    for (String s : split) {
      if (Strings.isNullOrEmpty(s)) {
        continue;
      }

      if ((s.startsWith("\"") && s.endsWith("\"")) || (s.startsWith("'") && s.endsWith("'"))) {
        s = s.substring(1, s.length() - 1);
      }

      ResourcePath.validateFilename(s);
      result.elements.addLast(s);
    }

    if (query == null) {
      return result;
    }

    parseQueries(result, query);
    return result;
  }

  private static void parseQueries(PathImpl result, String queryString) {
    String[] pairs = queryString.split(QUERY_DELIMITER + "");

    for (String pair : pairs) {
      if (Strings.isNullOrEmpty(pair)) {
        continue;
      }

      int eqIdx = pair.indexOf(KEY_VALUE_SEPARATOR);

      String qKey;
      String qValue;

      if (eqIdx == -1) {
        qKey = pair.trim();
        qValue = "";
      } else {
        qKey = pair.substring(0, eqIdx).trim();
        qValue = pair.substring(eqIdx + 1).trim();

        ResourcePath.validateQuery(qValue);
      }

      ResourcePath.validateQuery(qKey);

      result.queries.put(qKey, qValue);
    }
  }

  @Override
  public ResourcePath normalize(@Nullable ResourcePath cwd) {
    PathImpl cwdi = (PathImpl) cwd;
    PathImpl path = new PathImpl(moduleName);
    path.queries.putAll(queries);

    if (cwdi != null) {
      path.elements.addAll(cwdi.elements);
    }

    for (String element : elements) {
      if (element.equals(".")) {
        path.elements.clear();
        if (cwdi != null) {
          path.elements.addAll(cwdi.elements);
        }
        continue;
      }

      if (element.equals("..")) {
        if (!path.elements.isEmpty()) {
          path.elements.removeLast();
        }
        continue;
      }

      path.elements.addLast(element);
    }

    return path;
  }

  void setModule(String moduleName) {
    Objects.requireNonNull(moduleName, "Null module name");
    ResourcePath.validateQuery(moduleName);

    this.moduleName = moduleName;
  }

  @Override
  public @NotNull String getModuleName() {
    return moduleName;
  }

  @Override
  public String getQuery(String key) {
    return queries.get(key);
  }

  @Override
  public Set<String> getQueryKeys() {
    return Collections.unmodifiableSet(queries.keySet());
  }

  @Override
  public List<String> getElements() {
    return Collections.unmodifiableList(elements);
  }

  @Override
  public int elementCount() {
    return elements.size();
  }

  @Override
  public @NotNull String getElement(int index) {
    return elements.get(index);
  }

  @Override
  public @NotNull String path() {
    StringBuilder builder = new StringBuilder();
    appendElements(builder);
    return builder.toString();
  }

  @Override
  public @NotNull String query() {
    StringBuilder builder = new StringBuilder();
    appendQuery(builder);
    return builder.toString();
  }

  @Override
  public String elements() {
    StringBuilder builder = new StringBuilder();
    appendElements(builder);
    appendQuery(builder);
    return builder.toString();
  }

  @Override
  public ResourcePath setModuleName(@NotNull String moduleName) {
    ResourcePath.validateModuleName(moduleName);
    PathImpl p = new PathImpl(this);
    p.setModule(moduleName);
    return p;
  }

  @Override
  public ResourcePath addElement(@NotNull String element) {
    ResourcePath.validateFilename(element);
    PathImpl p = new PathImpl(this);
    p.elements.add(element);
    return p;
  }

  @Override
  public ResourcePath setElement(int index, @NotNull String element) throws IndexOutOfBoundsException {
    ResourcePath.validateFilename(element);
    Objects.checkIndex(index, elements.size());

    PathImpl p = new PathImpl(this);
    p.elements.set(index, element);

    return p;
  }

  @Override
  public ResourcePath setQuery(@NotNull String key, @Nullable String value) {
    ResourcePath.validateQuery(key);

    if (Strings.isNullOrEmpty(value)) {
      value = "";
    } else {
      ResourcePath.validateQuery(value);
    }

    PathImpl p = new PathImpl(this);
    p.queries.put(key, value);

    return p;
  }

  @Override
  public ResourcePath addAllElements(@NotNull ResourcePath path) {
    PathImpl p = new PathImpl(this);
    p.elements.addAll(((PathImpl) path).elements);
    return p;
  }

  @Override
  public ResourcePath setElements(ResourcePath path) {
    PathImpl p = new PathImpl(this);
    p.elements.clear();
    p.elements.addAll(((PathImpl) path).elements);
    return p;
  }

  @Override
  public ResourcePath removeElement(int index) {
    Objects.checkIndex(index, elements.size());
    PathImpl p = new PathImpl(this);
    p.elements.remove(index);
    return p;
  }

  @Override
  public ResourcePath clearElements() {
    if (elements.isEmpty()) {
      return this;
    }

    PathImpl p = new PathImpl(this);
    p.elements.clear();
    return p;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();

    builder.append(moduleName).append(':');

    appendElements(builder);
    appendQuery(builder);
    return builder.toString();
  }

  private void appendQuery(StringBuilder builder) {
    if (queries.isEmpty()) {
      return;
    }

    builder.append('?');

    Iterator<Entry<String, String>> it = queries.entrySet().iterator();
    while (it.hasNext()) {
      Entry<String, String> e = it.next();

      builder.append(e.getKey());

      if (!Strings.isNullOrEmpty(e.getValue())) {
        builder.append('=').append(e.getValue());
      }

      if (it.hasNext()) {
        builder.append('&');
      }
    }
  }

  private void appendElements(StringBuilder builder) {
    Iterator<String> it = elements.iterator();

    while (it.hasNext()) {
      builder.append(escapeElement(it.next()));

      if (it.hasNext()) {
        builder.append('/');
      }
    }
  }

  private static String escapeElement(String element) {
    if (element.contains(" ")) {
      return "\"" + element + "\"";
    }
    return element;
  }
}
