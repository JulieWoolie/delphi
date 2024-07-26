package net.arcadiusmc.delphi.resource;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A mutable {@link PagePath}
 */
public sealed interface MutablePagePath extends PagePath permits MutablePathImpl {

  /**
   * Sets the module name.
   *
   * @param moduleName New module name
   *
   * @return {@code this}
   *
   * @throws NullPointerException If the module name is null
   * @throws IllegalArgumentException If the module name fails {@link #validateQuery(String)}
   */
  MutablePagePath setModuleName(@NotNull String moduleName);

  /**
   * Adds an element to the path's filepath.
   * @param element Filename
   * @return {@code this}
   *
   * @throws NullPointerException If the {@code element} is {@code null}
   * @throws IllegalArgumentException If the element fails {@link #validateFilename(String)}
   */
  MutablePagePath addElement(@NotNull String element);

  /**
   * Sets the element at a specific index.
   *
   * @param index Element index
   * @param element New element
   *
   * @return {@code this}
   *
   * @throws IndexOutOfBoundsException If the index is less than 0 or greater/equal to
   *                                   {@link #elementCount()}.
   * @throws NullPointerException If the {@code element} is {@code null}
   * @throws IllegalArgumentException If the element fails {@link #validateFilename(String)}
   */
  MutablePagePath setElement(int index, @NotNull String element) throws IndexOutOfBoundsException;

  /**
   * Sets a query value.
   *
   * @param key Query key
   * @param value Query value
   *
   * @return {@code this}
   *
   * @throws NullPointerException If the {@code key} is null
   * @throws IllegalArgumentException If the {@code key} or {@code value} (if not null)
   *                                  fails {@link #validateQuery(String)}.
   */
  MutablePagePath setQuery(@NotNull String key, @Nullable String value);

  /**
   * Adds all elements from the specified path.
   * @param path Path to copy elements from
   * @return {@code this}
   */
  MutablePagePath addAllElements(@NotNull PagePath path);

  /**
   * Creates an immutable copy of this path.
   * @return Immutable copy
   */
  PagePath immutable();
}
