package net.arcadiusmc.delphi.resource;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class MutablePathImpl extends PathImpl implements MutablePagePath {

  public MutablePathImpl(String moduleName) {
    super(moduleName);
  }

  @Override
  public MutablePagePath setModuleName(@NotNull String moduleName) {
    setModule(moduleName);
    return this;
  }

  @Override
  public MutablePagePath setQuery(@NotNull String key, @Nullable String value) {
    PagePath.validateQuery(key);

    if (value != null) {
      PagePath.validateQuery(value);
    } else {
      value = "";
    }

    queries.put(key, value);
    return this;
  }

  @Override
  public MutablePagePath addAllElements(@NotNull PagePath path) {
    elements.addAll(path.getElements());
    return this;
  }

  @Override
  public MutablePagePath addElement(@NotNull String element) {
    PagePath.validateFilename(element);
    elements.add(element);

    return this;
  }

  @Override
  public MutablePagePath setElement(int index, @NotNull String element)
      throws IndexOutOfBoundsException
  {
    PagePath.validateFilename(element);
    elements.set(index, element);

    return this;
  }

  @Override
  public PagePath immutable() {
    return new PathImpl(this);
  }
}
