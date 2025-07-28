package com.juliewoolie.delphi.resource;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import com.juliewoolie.delphi.Delphi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a module name and a URI used for loading a document's resources.
 * <p>
 * Page paths are made up of 3 elements: the module name, the path and the query.
 *
 * <h2>Module Name</h2>
 * Name of a specific directory/zip archive in the {@link DelphiResources#getModuleDirectory()}
 * directory.
 *
 * <h2>Path</h2>
 * The file path of a file/directory in the directory/zip archive the module name
 * points to.
 *
 * <h2>Query</h2>
 * Optional search parameters that scripts can use.
 */
public sealed interface ResourcePath permits PathImpl {

  Pattern VALID_ELEMENT = Pattern.compile("[a-zA-Z0-9 .\\-_$]+");
  Pattern VALID_QUERY = Pattern.compile("[a-zA-Z0-9.\\-_$]+");

  /**
   * Creates a page path with a specified {@code moduleName}
   *
   * @param moduleName Path Module name
   * @return Created path
   */
  static ResourcePath create(@NotNull String moduleName) {
    return new PathImpl(moduleName);
  }

  /**
   * Validates a filename.
   * <p>
   * For a filename to be "valid" it must not be null and match the {@link #VALID_ELEMENT}
   * regex pattern.
   *
   * @param element Element to validate
   *
   * @throws NullPointerException If the {@code element} is {@code null}
   * @throws IllegalArgumentException If the {@code element} is an invalid filename.
   */
  static void validateFilename(String element) {
    Objects.requireNonNull(element, "Null element");

    if (VALID_ELEMENT.matcher(element).matches()) {
      return;
    }

    throw new IllegalArgumentException("Invalid path element: " + element);
  }

  /**
   * Validates a query key/value.
   * <p>
   * For a query key or value to be "valid" it must not be null and must match the
   * {@link #VALID_QUERY} regex pattern.
   *
   * @param query Query element to validate.
   *
   * @throws NullPointerException If the {@code query} is null.
   * @throws IllegalArgumentException If the {@code query} is invalid.
   */
  static void validateQuery(String query) {
    Objects.requireNonNull(query, "Null query");

    if (VALID_QUERY.matcher(query).matches()) {
      return;
    }

    throw new IllegalArgumentException("Invalid element: " + query);
  }

  /**
   * Gets the module name element of the path.
   * @return Module name
   */
  @NotNull
  String getModuleName();

  /**
   * Sets the module name.
   *
   * @param moduleName New module name
   *
   * @return A copy of this path with the specified module name
   *
   * @throws NullPointerException If the module name is null
   * @throws IllegalArgumentException If the module name fails {@link #validateQuery(String)}
   */
  ResourcePath setModuleName(@NotNull String moduleName);

  /**
   * Adds an element to the path's filepath.
   *
   * @param element Filename
   * @return A copy of this path with the specified element added
   *
   * @throws NullPointerException If the {@code element} is {@code null}
   * @throws IllegalArgumentException If the element fails {@link #validateFilename(String)}
   */
  ResourcePath addElement(@NotNull String element);

  /**
   * Sets the element at a specific index.
   *
   * @param index Element index
   * @param element New element
   *
   * @return A copy of this path with the specified element changed
   *
   * @throws IndexOutOfBoundsException If the index is less than 0 or greater/equal to
   *                                   {@link #elementCount()}.
   * @throws NullPointerException If the {@code element} is {@code null}
   * @throws IllegalArgumentException If the element fails {@link #validateFilename(String)}
   */
  ResourcePath setElement(int index, @NotNull String element) throws IndexOutOfBoundsException;

  /**
   * Sets a query value.
   *
   * @param key Query key
   * @param value Query value
   *
   * @return A copy of this path with the specified query changed
   *
   * @throws NullPointerException If the {@code key} is null
   * @throws IllegalArgumentException If the {@code key} or {@code value} (if not null)
   *                                  fails {@link #validateQuery(String)}.
   */
  ResourcePath setQuery(@NotNull String key, @Nullable String value);

  /**
   * Adds all elements from the specified path.
   * @param path Path to copy elements from
   * @return A copy of this path with all the elements of the specified path added
   */
  ResourcePath addAllElements(@NotNull ResourcePath path);

  /**
   * Sets the elements of this path
   * @param path Path elements
   * @return A copy of this path with the elements of the specified path
   */
  ResourcePath setElements(ResourcePath path);

  /**
   * Removes a path element
   * @param index Index of the element to remove
   * @return A copy of this path with the specified element removed
   */
  ResourcePath removeElement(int index);

  /**
   * Clears all of this path's file elements.
   * @return A copy of this path with no elements
   */
  ResourcePath clearElements();

  /**
   * Gets the value of a query
   * @param key Query key
   * @return Query value, or {@code null}, if there's no query element with the
   *         specified {@code key}.
   */
  @Nullable
  String getQuery(String key);

  /**
   * Gets an unmodifiable set of the query keys.
   * @return Query keys
   */
  Set<String> getQueryKeys();

  /**
   * Gets an unmodifiable list of the path's elements
   * @return Unmodifiable path element list
   */
  List<String> getElements();

  /**
   * Gets the amount of elements in the path
   * @return Element count
   */
  int elementCount();

  /**
   * Gets an element at a specific index
   * <p>
   * Assume we have the path {@code foobar/filepath/filename.ext}, these would be the indexes:
   * <table><thead>
   *   <tr>
   *     <th>Index</th>
   *     <th>Element</th>
   *   </tr></thead>
   * <tbody>
   *   <tr>
   *     <td>0</td>
   *     <td>{@code foobar}</td>
   *   </tr>
   *   <tr>
   *     <td>1</td>
   *     <td>{@code filepath}</td>
   *   </tr>
   *   <tr>
   *     <td>2</td>
   *     <td>{@code filename.ext}</td>
   *   </tr>
   * </tbody>
   * </table>
   *
   * @param index Element index
   * @return Element
   *
   * @throws IndexOutOfBoundsException If {@code index} is less than 0, or greater/equal to
   *                                   {@link #elementCount()}.
   */
  @NotNull
  String getElement(int index) throws IndexOutOfBoundsException;

  /**
   * Gets the file path part of this page path.
   * <p>
   * Example: {@code foo/bar/foobar.json}
   * @return File path string, or {@code ""}, if no file elements were specified
   */
  @NotNull
  String path();

  /**
   * Gets the query part of this path as a string.
   * <p>
   * Example: {@code ?foo=bar&bar=foo}
   * @return Query string, or {@code ""} if there are no query elements.
   */
  @NotNull
  String query();

  /**
   * Gets the file path of this page path combined with the query parameters.
   * <p>
   * Example: {@code dir/file.xml?foo=bar}
   * @return File path + query parameters.
   * @see #query()
   * @see #path()
   */
  String elements();

  /**
   * Combines the module name, elements and queries into a string that can be given to
   * {@link Delphi#parsePath(String)} to parse.
   * <p>
   * Examples:
   * <ul>
   *   <li>{@code module}</li>
   *   <li>{@code module:file.ext}</li>
   *   <li>{@code module:'file with space.ext'}</li>
   *   <li>{@code module:'dir with space'/file.xml}</li>
   *   <li>{@code module:foobar/file.ext}</li>
   *   <li>{@code module:foobar/file.ext?foo=bar&bar=foo}</li>
   *   <li>{@code module:foobar/file.ext?foo&bar=false}</li>
   *   <li>{@code module:?foo=bar&bar=foo}</li>
   * </ul>
   *
   * @return String representation of this path
   */
  String toString();
}
