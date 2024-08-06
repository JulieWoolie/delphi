package net.arcadiusmc.delphi.resource;

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
    ResourcePath.validateQuery(moduleName);
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
